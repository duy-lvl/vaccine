package fa.training.backend.service;

import fa.training.backend.dto.request.vaccine_type.NewVaccineTypeRequest;
import fa.training.backend.dto.request.vaccine_type.UpdateVaccineTypeRequest;
import fa.training.backend.dto.response.PageDto;
import fa.training.backend.dto.response.vaccine_type.VaccineTypeListDto;
import fa.training.backend.exception.common.NotFoundException;
import fa.training.backend.model.VaccineType;
import fa.training.backend.repository.VaccineRepository;
import fa.training.backend.repository.VaccineTypeRepository;
import fa.training.backend.service.impl.VaccineTypeServiceImpl;
import fa.training.backend.util.Message;
import fa.training.backend.util.Status;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class VaccineTypeServiceTest {
    @Mock
    private VaccineTypeRepository vaccineTypeRepository;
    @Mock
    private VaccineRepository vaccineRepository;

    private VaccineTypeService vaccineTypeService;

    @Mock
    private FirebaseService firebaseService;
    private Validator validator;

    @Autowired
    private ModelMapper mapper;

    private static URL imageUrl;
    private static List<VaccineType> vaccineTypes;
    private static List<String> errorMessages;

    @BeforeAll
    static void setUpAll() {
        try {
            imageUrl = new URL("https:google.com");
        } catch (Exception e) {
        }

        errorMessages = List.of("Vaccine type code is required",
                "Vaccine type code length must not exceed 50 characters",
                "Vaccine type name is required",
                "Vaccine type name length must not exceed 50 characters",
                "Description is required",
                "Description's length must not exceed 200 characters"
        );
    }

    @BeforeEach
    void setUp() {
        vaccineTypeService = new VaccineTypeServiceImpl(vaccineTypeRepository, firebaseService, vaccineRepository, mapper);
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            this.validator = factory.getValidator();
        }
        vaccineTypes = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            vaccineTypes.add(
                    new VaccineType(
                            String.valueOf(i),
                            "Vaccine code " + i,
                            "Description " + i,
                            "Name " + i,
                            "image" + i + ".png",
                            Status.ACTIVE
                    )
            );
        }

    }

    private List<VaccineType> getVaccineTypes(int page, int size, String search) {
        return vaccineTypes
                .stream()
                .filter(vaccineType -> vaccineType.getCode().contains(search)
                        || vaccineType.getId().contains(search)
                        || vaccineType.getDescription().contains(search))
                .skip(size * (page - 1))
                .limit(size)
                .collect(Collectors.toList());
    }

    private void mockPagingData() {
        when(vaccineTypeRepository.findByCodeContainingOrNameContainingOrDescriptionContaining(anyString(), anyString(), anyString(), any(Pageable.class)))
                .thenAnswer(
                        answer -> {
                            Pageable p = answer.getArgument(3);
                            String search = answer.getArgument(1);
                            System.out.println("Search " + search);
                            return new PageImpl<>(getVaccineTypes(p.getPageNumber() + 1, p.getPageSize(), search), p, vaccineTypes.size());
                        }
                );
    }

    void mockUpload() {
        try {
            when(firebaseService.upload(any(MultipartFile.class))).thenReturn("image url");
        } catch (Exception e) {
        }

    }

    void mockGetFileUrl() {
        try {
            when(firebaseService.getFileUrl(any(String.class))).thenReturn(new URL("https:google.com"));
        } catch (Exception e) {
        }

    }

    void mockSave() {
        when(vaccineTypeRepository.save(any(VaccineType.class))).thenAnswer(
                answer -> {
                    VaccineType vt = answer.getArgument(0);
                    if (vt.getId() == null) {
                        vt.setId(String.valueOf(vaccineTypes.size()));
                        vaccineTypes.add(vt);
                    } else {
                        int index = Integer.parseInt(vt.getId()) - 1;
                        vaccineTypes.set(index, vt);
                    }
                    return vt;
                }
        );

    }

    void mockFindById() {
        when(vaccineTypeRepository.findById(anyString())).thenAnswer(
                answer -> {
                    String id = answer.getArgument(0);
                    return vaccineTypes.stream().filter(vt -> vt.getId().equals(id)).findFirst();
                }
        );
    }

    void mockExistById() {
        when(vaccineTypeRepository.existsById(anyString())).thenAnswer(
                answer -> {
                    String id = answer.getArgument(0);
                    return vaccineTypes.stream().anyMatch(vt -> vt.getId().equals(id));
                }
        );
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void addSuccess() {
        mockSave();
        int listSize = vaccineTypes.size();
        var request = new NewVaccineTypeRequest(
                "New Vaccine code",
                "Name",
                "Description",
                "image.png"
        );

        String id = String.valueOf(vaccineTypes.size());
        assertEquals(id, vaccineTypeService.add(request));
        assertEquals(listSize + 1, vaccineTypes.size());

        VaccineType addedVaccineType = vaccineTypes.get(listSize);

        assertEquals(request.getCode(), addedVaccineType.getCode());
        assertEquals(request.getName(), addedVaccineType.getName());
        assertEquals(request.getDescription(), addedVaccineType.getDescription());
        assertEquals(request.getImageUrl(), addedVaccineType.getImageUrl());
    }

    @Test
    void addFail_ViolateConstraint() {
        var request = new NewVaccineTypeRequest(
                "",
                "",
                "",
                ""
        );

        Set<ConstraintViolation<NewVaccineTypeRequest>> violations = this.validator.validate(request);
        assertEquals(3, violations.size());
        for (var violation : violations) {
            assertTrue(errorMessages.contains(violation.getMessage()));
        }
    }

    @Test
    void findByIdSuccess() {
        mockGetFileUrl();
        mockFindById();
        String id = "1";
        var expected = vaccineTypes.get(Integer.parseInt(id) - 1);
        var actual = vaccineTypeService.findById(id);
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getCode(), actual.getCode());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getDescription(), actual.getDescription());
        assertEquals(imageUrl, actual.getImageUrl());
        assertEquals(Status.ACTIVE, actual.getStatus());
    }

    @Test
    void findByIdFail_IdNotExist() {
        mockFindById();
        String id = "-1";
        NotFoundException exception = assertThrows(NotFoundException.class, () -> vaccineTypeService.findById(id));
        assertEquals(Message.MSG_128, exception.getDescription());
        assertEquals(id, exception.getId());
    }

    @Test
    void updateSuccess() {
        mockSave();
        mockFindById();
        int listSize = vaccineTypes.size();
        String id = "1";
        var request = new UpdateVaccineTypeRequest(
                "Update Vaccine code",
                "Update Name",
                "Update Description",
                "image.png",
                Status.INACTIVE
        );

        assertTrue(vaccineTypeService.update(request, id));
        assertEquals(listSize, vaccineTypes.size());

        VaccineType updatedVaccineType = vaccineTypes.get(Integer.parseInt(id) - 1);

        assertEquals(request.getCode(), updatedVaccineType.getCode());
        assertEquals(request.getName(), updatedVaccineType.getName());
        assertEquals(request.getDescription(), updatedVaccineType.getDescription());
        assertEquals(request.getImageUrl(), updatedVaccineType.getImageUrl());
        assertEquals(request.getStatus(), updatedVaccineType.getStatus());
    }

    @Test
    void updateFail_IdNotExist() {
        mockFindById();

        String id = "-1";
        var request = new UpdateVaccineTypeRequest(
                "Update Vaccine code",
                "Update Name",
                "Update Description",
                "image.png",
                Status.INACTIVE
        );

        NotFoundException exception = assertThrows(NotFoundException.class, () -> vaccineTypeService.update(request, id));

        assertEquals(Message.MSG_128, exception.getDescription());
        assertEquals(id, exception.getId());
    }


    @Test
    void searchVaccineTypesSuccess() {
        int page = 2;
        int size = 4;
        String search = "name";
        Pageable pageable = PageRequest.of(page - 1, size);
        mockPagingData();
        PageDto<VaccineTypeListDto> actual = vaccineTypeService.searchVaccineTypes(search, pageable);

        PageDto<VaccineTypeListDto> expected = new PageDto<>();
        expected.setPage(page);
        expected.setSize(size);
        expected.setTotalPages((int) Math.ceil(1.0 * vaccineTypes.size() / size));
        expected.setTotalElements(vaccineTypes.size());
        expected.setData(getVaccineTypes(page, size, search).stream().map(vaccineType -> mapper.map(vaccineType, VaccineTypeListDto.class)).toList());
        assertEquals(expected, actual);
    }
}