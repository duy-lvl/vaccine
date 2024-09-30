package fa.training.backend.service;

import fa.training.backend.dto.request.injection_schedule.InjectionScheduleAddRequest;
import fa.training.backend.dto.request.injection_schedule.InjectionScheduleUpdateRequest;
import fa.training.backend.dto.response.injection_schedule.InjectionScheduleDetailDto;
import fa.training.backend.dto.response.injection_schedule.InjectionScheduleDto;
import fa.training.backend.dto.response.PageDto;
import fa.training.backend.exception.common.NotFoundException;
import fa.training.backend.model.InjectionSchedule;
import fa.training.backend.model.Vaccine;
import fa.training.backend.model.VaccineType;
import fa.training.backend.repository.InjectionScheduleRepository;
import fa.training.backend.repository.VaccineRepository;
import fa.training.backend.service.impl.InjectionScheduleServiceImpl;
import fa.training.backend.util.InjectionScheduleStatus;
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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class InjectionScheduleServiceTest {

    @Mock
    private InjectionScheduleRepository injectionScheduleRepository;
    @Mock
    private VaccineRepository vaccineRepository;
    @Autowired
    private ModelMapper mapper;

    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private Validator validator;

    private InjectionScheduleService injectionScheduleService;

    private static List<InjectionSchedule> injectionSchedules;
    private static List<Vaccine> vaccines;
    private static List<VaccineType> vaccineTypes;
    private static List<String> errorMessages;

    @BeforeAll
    static void setUpAll() {
        vaccineTypes = new ArrayList<>();
        vaccineTypes.add(new VaccineType("1", "Vaccine type 1", "Description 1", "Name 1", "2ae4374c-ed60-4f44-a316-c70c4ab22e76_gau3.jpg", Status.ACTIVE));
        vaccineTypes.add(new VaccineType("2", "Vaccine type 2", "Description 2", "Name 2", "2ae4374c-ed60-4f44-a316-c70c4ab22e76_gau3.jpg", Status.ACTIVE));

        vaccines = new ArrayList<>();
        vaccines.add(new Vaccine("1", "Contraindication 1", "Indication 1", 2, "Vietnam", LocalDate.of(2020, 10, 19), LocalDate.of(2023, 10, 19), "Usage 1", "Vaccine 1", vaccineTypes.get(0), Status.ACTIVE));
        vaccines.add(new Vaccine("2", "Contraindication 2", "Indication 2", 1, "USA", LocalDate.of(2021, 5, 12), LocalDate.of(2024, 5, 12), "Usage 2", "Vaccine 2", vaccineTypes.get(1), Status.INACTIVE));

        injectionSchedules = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            injectionSchedules.add(
                    new InjectionSchedule(
                            String.valueOf(i),
                            "Description " + i,
                            LocalDate.of(2021, 8, 19),
                            "Place " + i,
                            LocalDate.of(2021, 8, 20),
                            InjectionScheduleStatus.NOT_YET,
                            vaccines.get(i % 2)
                    )
            );
        }
        errorMessages = List.of(
               "Vaccine Id is required",
                "Start date is required",
                "End date is required",
                "Place is required",
                "Status is required",
                "End date must be after start date"
        );
    }

    private void mockGetVaccinesById() {
        when(vaccineRepository.findById(any(String.class))).then(
                answer -> {
                    String id = answer.getArgument(0);
                    return vaccines.stream().filter(c -> c.getId().equals(id)).findFirst();
                }
        );
    }



//
//    void mockUpdateVaccineDate() {
//        when().then();
//    }

    void mockVaccineExistById() {
        when(vaccineRepository.existsById(anyString())).thenAnswer(
                answer -> {
                    String id = answer.getArgument(0);
                    return vaccines.stream().anyMatch(v -> v.getId().equals(id));
                }
        );
    }

    @AfterEach
    void tearDown() {
    }

    @BeforeEach
    void setUp() {
        this.injectionScheduleService = new InjectionScheduleServiceImpl(vaccineRepository, injectionScheduleRepository, mapper);
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            this.validator = factory.getValidator();
        }
    }

    private List<InjectionSchedule> getInjectionSchedules(int page, int size, String search) {
        return injectionSchedules
                .stream()
                .filter(result -> result.getVaccine().getName()
                        .contains(search))
                .skip(size * (page - 1))
                .limit(size)
                .collect(Collectors.toList());
    }

    private void mockPagingData() {
        when(injectionScheduleRepository.findByVaccineNameContainsIgnoreCase(any(String.class), any(Pageable.class)))
                .thenAnswer(
                        answer -> {
                            Pageable p = answer.getArgument(1);
                            String search = answer.getArgument(0);
                            System.out.println("Search " + search);
                            return new PageImpl<>(getInjectionSchedules(p.getPageNumber() + 1, p.getPageSize(), search), p, injectionSchedules.size());
                        }
                );
    }

    private void mockSave() {
        when(injectionScheduleRepository.save(any(InjectionSchedule.class))).then(
                answer -> {
                    InjectionSchedule schedule = answer.getArgument(0);
                    if (schedule.getId() == null || schedule.getId().isBlank()) {
                        schedule.setId(String.valueOf(injectionSchedules.size()));
                        injectionSchedules.add(schedule);
                    } else {
                        int index = Integer.parseInt(schedule.getId()) - 1;
                        injectionSchedules.set(index, schedule);
                    }
                    return schedule;
                }
        );
    }

    private void mockGetById() {
        when(injectionScheduleRepository.findById(any(String.class))).then(
                answer -> {
                    String id = answer.getArgument(0);
                    return injectionSchedules.stream().filter(c -> c.getId().equals(id)).findFirst();
                }
        );
    }

    private void mockExistById() {
        when(injectionScheduleRepository.existsById(any(String.class))).then(
                answer -> {
                    String id = answer.getArgument(0);
                    return injectionSchedules.stream().anyMatch(result -> result.getId().equals(id));
                }
        );
    }

    private Optional<InjectionSchedule> getInjectionScheduleById(String id) {
        return injectionSchedules.stream().filter(schedule -> schedule.getId().equals(id)).findFirst();
    }

    @Test
    void addSuccess() {
        mockSave();
        mockVaccineExistById();
        int listSize = injectionSchedules.size();
        InjectionScheduleAddRequest request = new InjectionScheduleAddRequest(
                vaccines.get(0).getId(),
                LocalDate.of(2021, 8, 19),
                LocalDate.of(2021, 8, 20),
                "Place 1",
                "Description 1"
        );

        assertTrue( injectionScheduleService.add(request));
        assertEquals( listSize + 1, injectionSchedules.size());
        InjectionSchedule addedInjectionSchedule = injectionSchedules.get(listSize);
        assertEquals(request.getVaccineId(), addedInjectionSchedule.getVaccine().getId());
        assertEquals(request.getStartDate(), addedInjectionSchedule.getStartDate());
        assertEquals(request.getEndDate(), addedInjectionSchedule.getEndDate());
        assertEquals(request.getPlace(), addedInjectionSchedule.getPlace());
        assertEquals(request.getDescription(), addedInjectionSchedule.getDescription());
        injectionSchedules.remove(listSize);
    }

    @Test
    void testAddFail_ViolateConstraint() {
        var request = new InjectionScheduleAddRequest(
                null,
                null,
                null,
                "",
                "Description 1"
        );
        Set<ConstraintViolation<InjectionScheduleAddRequest>> violations = this.validator.validate(request);
        assertEquals(5, violations.size());
        for (var violation : violations) {
            assertTrue(errorMessages.contains(violation.getMessage()));

        }
    }


    @Test
    void testUpdateFail_ViolateConstraint() {
        var request = new InjectionScheduleUpdateRequest(
                null,
                LocalDate.of(2021, 8, 19),
                LocalDate.of(2021, 7, 20),
                "",
                "Description 1"
        );
        Set<ConstraintViolation<InjectionScheduleUpdateRequest>> violations = this.validator.validate(request);
        assertEquals(4, violations.size());
        for (var violation : violations) {
            assertTrue(errorMessages.contains(violation.getMessage()));
        }
    }

    @Test
    void updateSuccess() {
        mockSave();
        mockExistById();

        var request = new InjectionScheduleUpdateRequest(
                vaccines.get(0).getId(),
                LocalDate.of(2021, 8, 19),
                LocalDate.of(2021, 8, 20),
                "Place 1",
                "Description 1"
        );

        assertTrue(injectionScheduleService.update(request, "1"));
        InjectionSchedule updatedInjectionSchedule = injectionSchedules.get(0);
        assertEquals(request.getVaccineId(), updatedInjectionSchedule.getVaccine().getId());
        assertEquals(request.getStartDate(), updatedInjectionSchedule.getStartDate());
        assertEquals(request.getEndDate(), updatedInjectionSchedule.getEndDate());
        assertEquals(request.getPlace(), updatedInjectionSchedule.getPlace());
        assertEquals(request.getDescription(), updatedInjectionSchedule.getDescription());
    }


    @Test
    void getAllSuccess() {
        int page = 2;
        int size = 4;
        String search = "";
        Pageable pageable = PageRequest.of(page - 1, size);
        mockPagingData();
        PageDto<InjectionScheduleDto> actual = injectionScheduleService.get(page, size, search);
        PageDto<InjectionScheduleDto> expected = new PageDto<>();
        expected.setPage(page);
        expected.setSize(size);
        expected.setTotalPages((int) Math.ceil(1.0 * injectionSchedules.size() / size));
        expected.setTotalElements(injectionSchedules.size());
        List<InjectionScheduleDto> expectedData = getInjectionSchedules(page, size, search).stream()
                .map(result -> {
                    InjectionScheduleDto dto = mapper.map(result, InjectionScheduleDto.class);
                    dto.setVaccineName(result.getVaccine().getName());
                    dto.setTime(String.format("From %s to %s",
                            result.getStartDate().format(dtf),
                            result.getEndDate().format(dtf)));
                    return dto;
                })
                .toList();
        expected.setData(expectedData);
        assertEquals(expected, actual);
    }

    @Test
    void searchSuccess() {
        int page = 2;
        int size = 4;
        String search = "name";
        Pageable pageable = PageRequest.of(page - 1, size);
        mockPagingData();
        PageDto<InjectionScheduleDto> actual = injectionScheduleService.get(page, size, search);
        PageDto<InjectionScheduleDto> expected = new PageDto<>();
        expected.setPage(page);
        expected.setSize(size);
        expected.setTotalPages((int) Math.ceil(1.0 * injectionSchedules.size() / size));
        expected.setTotalElements(injectionSchedules.size());
        List<InjectionScheduleDto> expectedData = getInjectionSchedules(page, size, search).stream()
                .map(result -> {
                    InjectionScheduleDto dto = mapper.map(result, InjectionScheduleDto.class);
                    dto.setVaccineName(result.getVaccine().getName());
                    dto.setTime(String.format("From %s to %s",
                            result.getStartDate().format(dtf),
                            result.getEndDate().format(dtf)));
                    return dto;
                })
                .toList();
        expected.setData(expectedData);
        assertEquals(expected, actual);
    }

    @Test
    void getByIdSuccess() {
        String id = "3";
        mockGetById();
        mockExistById();
        InjectionScheduleDetailDto actual = injectionScheduleService.getById(id);
        InjectionScheduleDetailDto expected = mapper.map(getInjectionScheduleById(id).get(), InjectionScheduleDetailDto.class);
        assertEquals(expected, actual);
    }

    @Test
    void getByIdNotFound() {
        String id = "33";
        mockExistById();
        NotFoundException exception = assertThrows(NotFoundException.class, () -> injectionScheduleService.getById(id));
    }

}