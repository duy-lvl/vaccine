package fa.training.backend.service;

import fa.training.backend.dto.request.vaccine.VaccineAddRequest;
import fa.training.backend.dto.request.vaccine.VaccineUpdateRequest;
import fa.training.backend.dto.response.PageDto;
import fa.training.backend.dto.response.vaccine.VaccineIndividualDto;
import fa.training.backend.dto.response.vaccine.VaccineUpdateDto;
import fa.training.backend.exception.common.NotFoundException;
import fa.training.backend.model.Vaccine;
import fa.training.backend.model.VaccineType;
import fa.training.backend.repository.VaccineRepository;
import fa.training.backend.repository.VaccineTypeRepository;
import fa.training.backend.service.impl.VaccineServiceImpl;
import fa.training.backend.util.Message;
import fa.training.backend.util.Status;
import jakarta.validation.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class VaccineServiceTest {

    @Mock
    private VaccineRepository vaccineRepository;

    @Mock
    private VaccineTypeRepository vaccineTypeRepository;

    @Autowired
    private ModelMapper mapper;

    private Validator validator;

    private VaccineService vaccineService;

    private static List<VaccineType> vaccineTypes;
    private static List<Vaccine> vaccines;

    private static List<String> errorMessages;

    @BeforeAll
    static void setUpAll() {
        vaccineTypes = new ArrayList<>();
        vaccineTypes.add(new VaccineType("1", "Vaccine type 1", "Description 1", "Name 1", "2ae4374c-ed60-4f44-a316-c70c4ab22e76_gau3.jpg", Status.ACTIVE));
        vaccineTypes.add(new VaccineType("2", "Vaccine type 2", "Description 2", "Name 2", "2ae4374c-ed60-4f44-a316-c70c4ab22e76_gau3.jpg", Status.ACTIVE));
        vaccines = new ArrayList<>();

        vaccines.add(new Vaccine("1", "Contraindication 1", "Indication 1", 2, "Vietnam", LocalDate.of(2020, 10, 19), LocalDate.of(2023, 10, 19), "Usage 1", "Vaccine 1", vaccineTypes.get(0), Status.ACTIVE));
        vaccines.add(new Vaccine("2", "Contraindication 2", "Indication 2", 1, "USA", LocalDate.of(2021, 5, 12), LocalDate.of(2024, 5, 12), "Usage 2", "Vaccine 2", vaccineTypes.get(1), Status.INACTIVE));
        vaccines.add(new Vaccine("3", "Contraindication 3", "Indication 3", 3, "UK", LocalDate.of(2019, 12, 5), LocalDate.of(2022, 12, 5), "Usage 3", "Vaccine 3", vaccineTypes.get(1), Status.ACTIVE));
        vaccines.add(new Vaccine("4", "Contraindication 4", "Indication 4", 4, "France", LocalDate.of(2022, 3, 1), LocalDate.of(2025, 3, 1), "Usage 4", "Vaccine 4", vaccineTypes.get(0), Status.ACTIVE));
        vaccines.add(new Vaccine("5", "Contraindication 5", "Indication 5", 2, "Germany", LocalDate.of(2020, 8, 21), LocalDate.of(2023, 8, 21), "Usage 5", "Vaccine 5", vaccineTypes.get(1), Status.INACTIVE));
        vaccines.add(new Vaccine("6", "Contraindication 6", "Indication 6", 1, "Australia", LocalDate.of(2021, 11, 15), LocalDate.of(2024, 11, 15), "Usage 6", "Vaccine 6", vaccineTypes.get(0), Status.ACTIVE));
        vaccines.add(new Vaccine("7", "Contraindication 7", "Indication 7", 3, "Canada", LocalDate.of(2019, 7, 30), LocalDate.of(2022, 7, 30), "Usage 7", "Vaccine 7", vaccineTypes.get(1), Status.INACTIVE));
        vaccines.add(new Vaccine("8", "Contraindication 8", "Indication 8", 2, "Japan", LocalDate.of(2020, 9, 10), LocalDate.of(2023, 9, 10), "Usage 8", "Vaccine 8", vaccineTypes.get(1), Status.ACTIVE));
        vaccines.add(new Vaccine("9", "Contraindication 9", "Indication 9", 4, "India", LocalDate.of(2022, 2, 17), LocalDate.of(2025, 2, 17), "Usage 9", "Vaccine 9", vaccineTypes.get(1), Status.INACTIVE));
        vaccines.add(new Vaccine("10", "Contraindication 10", "Indication 10", 1, "China", LocalDate.of(2021, 6, 23), LocalDate.of(2024, 6, 23), "Usage 10", "Vaccine 10", vaccineTypes.get(1), Status.ACTIVE));

        errorMessages = List.of(
                "Vaccine name is required",
                "Vaccine type Id is required",
                "Vaccine type is required",
                "Time end next injection must be after time begin next injection",
                "Number of injection must be a positive integer"
        );
    }

    @BeforeEach
    void setUp() {
        this.vaccineService = new VaccineServiceImpl(vaccineTypeRepository, vaccineRepository, mapper);
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            this.validator = factory.getValidator();
        }

    }


    private List<Vaccine> getVaccines(int page, int size, String search) {
        return vaccines
                .stream()
                .filter(vaccine -> vaccine.getName()
                        .contains(search))
                .skip(size * (page - 1))
                .limit(size)
                .collect(Collectors.toList());
    }

    private void mockPagingData() {

        when(vaccineRepository.findByNameContainsIgnoreCase(any(Pageable.class), any(String.class)))
                .thenAnswer(
//                        new PageImpl<>(getVaccines(any(Pageable.class).getPageNumber() + 1, any(Pageable.class).getPageSize(), search), any(Pageable.class), vaccines.size())
                        answer -> {
                            Pageable p = answer.getArgument(0);
                            String search = answer.getArgument(1);
                            System.out.println("Search " + search);
                            return new PageImpl<>(getVaccines(p.getPageNumber() + 1, p.getPageSize(), search), p, vaccines.size());
                        }
                );
    }

    private void mockSave() {
        when(vaccineRepository.save(any(Vaccine.class))).then(
                answer -> {
                    Vaccine v = answer.getArgument(0);
                    if (v.getId() == null) {
                        v.setId(String.valueOf(vaccines.size()));
                        vaccines.add(v);
                    } else {
                        int index = Integer.parseInt(v.getId()) - 1;
                        vaccines.set(index, v);
                    }

                    return v;
                }
        );
    }

    private void mockGetById() {
        when(vaccineRepository.findById(any(String.class))).then(
                answer -> {
                    String id = answer.getArgument(0);
                    return vaccines.stream().filter(v -> v.getId().equals(id)).findFirst();
                }
        );
    }

    private void mockExistById() {
        when(vaccineRepository.existsById(any(String.class))).then(
                answer -> {
                    String id = answer.getArgument(0);
                    return vaccines.stream().anyMatch(v -> v.getId().equals(id));
                }
        );
    }

    private void mockTypeExistById() {
        when(vaccineTypeRepository.existsById(any(String.class))).then(
                answer -> {
                    String id = answer.getArgument(0);
                    return vaccineTypes.stream().anyMatch(v -> v.getId().equals(id));
                }
        );
    }


    private Optional<Vaccine> getVaccineById(String id) {
        return vaccines.stream().filter(vaccine -> vaccine.getId().equals(id)).findFirst();
    }


    @AfterEach
    void tearDown() {
    }

    @Test
    void addSuccess() {
        mockSave();
        mockTypeExistById();

        int listSize = vaccines.size();
        VaccineAddRequest request = new VaccineAddRequest(
                "add vaccine name",
                vaccineTypes.get(0).getId(),
                10,
                "add vaccine usage",
                "add vaccine indication",
                "add vaccine contraindication",
                "Vietnam"
        );

        vaccineService.add(request);
        assertTrue(vaccines.size() == (listSize + 1));

        Vaccine addedVaccine = vaccines.get(listSize);
        assertEquals(request.getName(), addedVaccine.getName());
        assertEquals(request.getContraindication(), addedVaccine.getContraindication());
        assertEquals(request.getUsage(), addedVaccine.getUsage());

        vaccines.remove(listSize);

    }

    @Test
    void testAddFail_ViolateConstraint() {
        var request = new VaccineAddRequest(
                "   ",
                "",
                -1,
                "add vaccine usage",
                "add vaccine indication",
                "add vaccine contraindication",
                "Vietnam"
        );

        Set<ConstraintViolation<VaccineAddRequest>> violations = this.validator.validate(request);
        assertEquals(3, violations.size());
        for (var violation : violations) {
            assertTrue(errorMessages.contains(violation.getMessage()));
        }
    }

    @Test
    void testUpdateFail_ViolateConstraint() {
        var request = new VaccineUpdateRequest(
                "   ",
                "",
                -1,
                "add vaccine usage",
                "add vaccine indication",
                "add vaccine contraindication",
                "Vietnam",
                Status.INACTIVE
        );

        Set<ConstraintViolation<VaccineUpdateRequest>> violations = this.validator.validate(request);
        assertEquals(3, violations.size());
        for (var violation : violations) {
            assertTrue(errorMessages.contains(violation.getMessage()));
        }
    }
    @Test
    void updateSuccess() {
        mockSave();

        mockGetById();
        mockTypeExistById();
        var request = new VaccineUpdateRequest(
                "update vaccine name",
                vaccineTypes.get(0).getId(),
                -1,
                "add vaccine usage",
                "add vaccine indication",
                "add vaccine contraindication",
                "Vietnam",
                Status.INACTIVE
        );

        vaccineService.update(request, "1");

        Vaccine updatedVaccine = vaccines.get(0);
        assertEquals(request.getName(), updatedVaccine.getName());
        assertEquals(request.getContraindication(), updatedVaccine.getContraindication());
        assertEquals(request.getUsage(), updatedVaccine.getUsage());

    }

    @Test
    void getAllSuccess() {
        int page = 2;
        int size = 4;
        String search = "";
        Pageable pageable = PageRequest.of(page-1, size);
        mockPagingData();
        PageDto<VaccineIndividualDto> actual = vaccineService.getAll(pageable, search);

        PageDto<VaccineIndividualDto> expected = new PageDto<>();
        expected.setPage(page);
        expected.setSize(size);
        expected.setTotalPages((int) Math.ceil(1.0 * vaccines.size() / size));
        expected.setTotalElements(vaccines.size());
        expected.setData(getVaccines(page, size,search).stream().map(vaccine -> mapper.map(vaccine, VaccineIndividualDto.class)).toList());
        assertEquals(expected, actual);
    }

    @Test
    void searchSuccess() {
        int page = 2;
        int size = 4;
        String search = "name";
        Pageable pageable = PageRequest.of(page-1, size);
        mockPagingData();
        PageDto<VaccineIndividualDto> actual = vaccineService.getAll(pageable, search);

        PageDto<VaccineIndividualDto> expected = new PageDto<>();
        expected.setPage(page);
        expected.setSize(size);
        expected.setTotalPages((int) Math.ceil(1.0 * vaccines.size() / size));
        expected.setTotalElements(vaccines.size());
        expected.setData(getVaccines(page, size, search).stream().map(vaccine -> mapper.map(vaccine, VaccineIndividualDto.class)).toList());
        assertEquals(expected, actual);
    }

    @Test
    void getByIdSuccess() {
        String id = "3";
        mockGetById();

        VaccineUpdateDto actual = vaccineService.getById(id);

        VaccineUpdateDto expected = mapper.map(getVaccineById(id).get(), VaccineUpdateDto.class);

        assertEquals(expected, actual);
    }

    @Test
    void getByIdNotFound() {
        String id = "33";
        mockGetById();

        NotFoundException exception = assertThrows(NotFoundException.class, () -> vaccineService.getById(id));
//        assertEquals(exception.getCode(), Message.MSG_...getCode());
//        assertEquals(exception.getMessage(), Message.MSG_...getMessage());

    }

    @Test
    void switchStatusSuccess() {
        mockGetById();
        mockSave();
        String id = "1";
        Status expected = vaccines.get(0).getStatus() == Status.ACTIVE ? Status.INACTIVE : Status.ACTIVE;
        vaccineService.switchStatus(id);
        Status actual = vaccines.get(0).getStatus();
        assertEquals(expected, actual);
    }

    @Test
    void switchStatusFail_IdNotFound() {
        mockGetById();
        String id = "-1";
        NotFoundException exception = assertThrows(NotFoundException.class, () -> vaccineService.switchStatus(id));

        assertEquals(Message.MSG_153, exception.getDescription());
        assertEquals(id, exception.getId());
    }
}