package fa.training.backend.service;

import fa.training.backend.dto.request.injection_result.InjectionResultRequest;
import fa.training.backend.dto.response.injection_result.InjectionResultDetailDto;
import fa.training.backend.dto.response.injection_result.InjectionResultDto;
import fa.training.backend.dto.response.PageDto;
import fa.training.backend.exception.common.InvalidStatusException;
import fa.training.backend.exception.common.NotFoundException;
import fa.training.backend.exception.injection_result.InsufficientInjectionException;
import fa.training.backend.model.Customer;
import fa.training.backend.model.InjectionResult;
import fa.training.backend.model.Vaccine;
import fa.training.backend.model.VaccineType;
import fa.training.backend.repository.CustomerRepository;
import fa.training.backend.repository.InjectionResultRepository;
import fa.training.backend.repository.VaccineRepository;
import fa.training.backend.service.impl.InjectionResultServiceImpl;
import fa.training.backend.util.Gender;
import fa.training.backend.util.Message;
import fa.training.backend.util.Status;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
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
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class InjectionResultServiceTest {

    @Mock
    private InjectionResultRepository injectionResultRepository;

    @Mock
    private VaccineRepository vaccineRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Autowired
    private ModelMapper mapper;

    private Validator validator;
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private InjectionResultService injectionResultService;

    private static List<InjectionResult> injectionResults;
    private static List<Customer> customers;
    private static List<Vaccine> vaccines;
    private static List<VaccineType> vaccineTypes;

    private static List<String> errorMessages;

    @BeforeAll
    static void setUpAll() {
        customers = new ArrayList<>();
        customers.add(new Customer("1", "address1", LocalDate.of(1997, 8, 19), "email1@gmail.com", "fullname1", Gender.FEMALE, "123456789121", "password1", "0123456789", "username1", Status.ACTIVE));
        customers.add(new Customer("2", "address2", LocalDate.of(1995, 5, 15), "email2@gmail.com", "fullname2", Gender.MALE, "234567891234", "password2", "0123456788", "username2", Status.INACTIVE));

        vaccineTypes = new ArrayList<>();
        vaccineTypes.add(new VaccineType("1", "Vaccine type 1", "Description 1", "Name 1", "2ae4374c-ed60-4f44-a316-c70c4ab22e76_gau3.jpg", Status.ACTIVE));
        vaccineTypes.add(new VaccineType("2", "Vaccine type 2", "Description 2", "Name 2", "2ae4374c-ed60-4f44-a316-c70c4ab22e76_gau3.jpg", Status.ACTIVE));

        vaccines = new ArrayList<>();
        vaccines.add(new Vaccine("1", "Contraindication 1", "Indication 1", 2, "Vietnam", LocalDate.of(2020, 10, 19), LocalDate.of(2023, 10, 19), "Usage 1", "Vaccine 1", vaccineTypes.get(0), Status.ACTIVE));
        vaccines.add(new Vaccine("2", "Contraindication 2", "Indication 2", 1, "USA", LocalDate.of(2021, 5, 12), LocalDate.of(2024, 5, 12), "Usage 2", "Vaccine 2", vaccineTypes.get(1), Status.INACTIVE));



        errorMessages = List.of(
                "Customer Id is required",
                "Vaccine is required",
                "Next injection date must be after previous injection",
                "Number of injection must be a positive integer",
                "Next injection date is required",
                "Injection place is required",
                "Injection date is required",
                "Next injection date must be after previous injection"
        );
    }

    @AfterEach
    void tearDown() {
    }

    @BeforeEach
    void setUp() {
        injectionResults = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            injectionResults.add(
                    new InjectionResult(
                            String.valueOf(i),
                            customers.get(i % 2),
                            LocalDate.of(2021, 8, i),
                            "Place " + i,
                            LocalDate.of(2021, 8, i + 10),
                            i,
                            "Prevention " + i,
                            vaccines.get(i % 2)
                    )
            );
        }
        this.injectionResultService = new InjectionResultServiceImpl(injectionResultRepository, vaccineRepository, customerRepository, mapper);
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            this.validator = factory.getValidator();
        }
    }

    private List<InjectionResult> getInjectionResults(int page, int size, String search) {
        return injectionResults
                .stream()
                .filter(result -> result.getCustomer().getFullName().contains(search)
                        || result.getVaccine().getName().contains(search))
                .skip(size * (page - 1))
                .limit(size)
                .collect(Collectors.toList());
    }

    private void mockPagingData() {
        when(injectionResultRepository.findByCustomerFullNameContainsIgnoreCaseOrVaccineNameContainsIgnoreCase(any(String.class), any(String.class), any(Pageable.class)))
                .thenAnswer(
                        answer -> {
                            Pageable p = answer.getArgument(2);
                            String search = answer.getArgument(1);
                            return new PageImpl<>(getInjectionResults(p.getPageNumber() + 1, p.getPageSize(), search), p, injectionResults.size());
                        }
                );
    }

    private void mockSave() {
        when(injectionResultRepository.save(any(InjectionResult.class))).then(
                answer -> {
                    InjectionResult result = answer.getArgument(0);
                    if (result.getId() == null || result.getId().isBlank()) {
                        result.setId(String.valueOf(injectionResults.size()));
                        injectionResults.add(result);
                    } else {
                        int index = Integer.parseInt(result.getId()) - 1;
                        injectionResults.set(index, result);
                    }
                    return result;
                }
        );
    }

    private void mockSaveAll() {
        when(injectionResultRepository.saveAll(any(List.class))).then(
                answer -> {
                    List<InjectionResult> results = answer.getArgument(0);
                    for (var result : results) {
                        if (result.getId() == null || result.getId().isBlank()) {
                            result.setId(String.valueOf(injectionResults.size()));
                            injectionResults.add(result);
                        } else {
                            int index = Integer.parseInt(result.getId()) - 1;
                            injectionResults.set(index, result);
                        }
                    }
                    return results;
                }
        );
    }


    private void mockSaveVaccine() {
        when(vaccineRepository.save(any(Vaccine.class))).then(
                answer -> {
                    Vaccine result = answer.getArgument(0);
                    if (result.getId() == null || result.getId().isBlank()) {
                        result.setId(String.valueOf(vaccines.size()));
                        vaccines.add(result);
                    } else {
                        int index = Integer.parseInt(result.getId()) - 1;
                        vaccines.set(index, result);
                    }
                    return result;
                }
        );
    }
    private void mockGetById() {
        when(injectionResultRepository.findById(any(String.class))).then(
                answer -> {
                    String id = answer.getArgument(0);
                    return injectionResults.stream().filter(jr -> jr.getId().equals(id)).findFirst();
                }
        );
    }

    private void mockFindAllById() {
        when(injectionResultRepository.findAllById(any(List.class))).then(
                answer -> {
                    List<String> ids = answer.getArgument(0);
                    return injectionResults.stream().filter(jr -> ids.contains(jr.getId())).toList();
                }
        );
    }

    private void mockGetVaccineById() {
        when(vaccineRepository.findById(any(String.class))).then(
                answer -> {
                    String id = answer.getArgument(0);
                    return vaccines.stream().filter(v -> v.getId().equals(id)).findFirst();
                }
        );
    }
    private void mockGetVaccineByName() {
        when(vaccineRepository.findByName(any(String.class))).then(
                answer -> {
                    String name = answer.getArgument(0);
                    return vaccines.stream().filter(v -> v.getName().equals(name)).findFirst();
                }
        );
    }
    private void mockGetCustomerById() {
        when(customerRepository.findById(any(String.class))).then(
                answer -> {
                    String id = answer.getArgument(0);
                    return customers.stream().filter(c -> c.getId().equals(id)).findFirst();
                }
        );
    }


    private Optional<InjectionResult> getInjectionResultById(String id) {
        return injectionResults.stream().filter(result -> result.getId().equals(id)).findFirst();
    }

    @Test
    void addSuccess() {
        mockSave();
        mockGetCustomerById();
        mockGetVaccineByName();
        int listSize = injectionResults.size();
        InjectionResultRequest request = new InjectionResultRequest(
                customers.get(0).getId(),
                LocalDate.of(2021, 8, 19),
                "Place 1",
                LocalDate.of(2021, 8, 29),
                1,
                "Prevention 1",
                vaccines.get(0).getName()
        );

        injectionResultService.add(request);
        assertTrue(injectionResults.size() == (listSize + 1));
        InjectionResult addedInjectionResult = injectionResults.get(listSize);
        assertEquals(request.getCustomerId(), addedInjectionResult.getCustomer().getId());
        assertEquals(request.getInjectionDate(), addedInjectionResult.getInjectionDate());
        assertEquals(request.getInjectionPlace(), addedInjectionResult.getInjectionPlace());
        assertEquals(request.getNextInjectionDate(), addedInjectionResult.getNextInjectionDate());
        assertEquals(request.getNumberOfInjection(), addedInjectionResult.getNumberOfInjection());
        assertEquals(request.getPrevention(), addedInjectionResult.getPrevention());
        assertEquals(request.getVaccineId(), addedInjectionResult.getVaccine().getId());
        injectionResults.remove(listSize);
    }

    @Test
    void testAddFail_ViolateConstraint() {
        var request = new InjectionResultRequest();
        Set<ConstraintViolation<InjectionResultRequest>> violations = this.validator.validate(request);
        assertEquals(8, violations.size());
        for (var violation : violations) {
            assertTrue(errorMessages.contains(violation.getMessage()));
        }

    }

    @Test
    void testAddFail_VaccineNotExist() {
        var request = new InjectionResultRequest(
                "-1",
                LocalDate.of(2021, 8, 19),
                "Place 1",
                LocalDate.of(2021, 7, 29),
                3,
                "",
                "-1"
        );
        NotFoundException notFoundException = assertThrows(
                NotFoundException.class,
                () -> injectionResultService.add(request)
        );
        assertEquals(Message.MSG_153, notFoundException.getDescription());
        assertEquals(request.getVaccineId(), notFoundException.getId());
    }

    @Test
    void testAddFail_CustomerNotExist() {
        mockGetCustomerById();
        mockGetVaccineByName();
        var request = new InjectionResultRequest(
                "-1",
                LocalDate.of(2021, 8, 19),
                "Place 1",
                LocalDate.of(2021, 7, 29),
                3,
                "",
                vaccines.get(0).getName()
        );
        NotFoundException notFoundException = assertThrows(
                NotFoundException.class,
                () -> injectionResultService.add(request)
        );
        assertEquals(Message.MSG_111, notFoundException.getDescription());
        assertEquals(request.getCustomerId(), notFoundException.getId());
    }

    @Test
    void testAddFail_CustomerStatusInvalid() {
        mockGetCustomerById();
        mockGetVaccineByName();
        var request = new InjectionResultRequest(
                customers.get(1).getId(),
                LocalDate.of(2021, 8, 19),
                "Place 1",
                LocalDate.of(2021, 7, 29),
                3,
                "",
                vaccines.get(0).getName()
        );
        InvalidStatusException invalidStatusException = assertThrows(
                InvalidStatusException.class,
                () -> injectionResultService.add(request)
        );
        assertEquals(Message.MSG_116, invalidStatusException.getDescription());

    }

    @Test
    void testAddFail_InsufficientNumberOfInjection() {
        mockGetCustomerById();
        mockGetVaccineByName();
        var request = new InjectionResultRequest(
                customers.get(0).getId(),
                LocalDate.of(2021, 8, 19),
                "Place 1",
                LocalDate.of(2021, 7, 29),
                100,
                "",
                vaccines.get(0).getName()
        );
        InsufficientInjectionException insufficientInjectionException = assertThrows(
                InsufficientInjectionException.class,
                () -> injectionResultService.add(request)
        );
        assertEquals(Message.MSG_181, insufficientInjectionException.getDescription());

    }


    @Test
    void testUpdateFail_ViolateConstraint() {
        var request = new InjectionResultRequest();
        Set<ConstraintViolation<InjectionResultRequest>> violations = this.validator.validate(request);
        assertEquals(8, violations.size());
        for (var violation : violations) {
            assertTrue(errorMessages.contains(violation.getMessage()));
        }
    }
    @Test
    void testUpdateFail_InjectionResultNotExist() {

        mockGetById();
        var request = new InjectionResultRequest(
                "-1",
                LocalDate.of(2021, 8, 19),
                "Place 1",
                LocalDate.of(2021, 7, 29),
                3,
                "",
                "-1"
        );
        String id = "-1";
        NotFoundException notFoundException = assertThrows(
                NotFoundException.class,
                () -> injectionResultService.update(request, id)
        );
        assertEquals(Message.MSG_182, notFoundException.getDescription());
        assertEquals(id, notFoundException.getId());
    }


    @Test
    void testUpdateFail_VaccineNotExist() {
        mockGetVaccineById();
        mockGetById();
        var request = new InjectionResultRequest(
                "-1",
                LocalDate.of(2021, 8, 19),
                "Place 1",
                LocalDate.of(2021, 7, 29),
                3,
                "",
                "-1"
        );
        NotFoundException notFoundException = assertThrows(
                NotFoundException.class,
                () -> injectionResultService.update(request, "1")
        );
        assertEquals(Message.MSG_153, notFoundException.getDescription());
        assertEquals(request.getVaccineId(), notFoundException.getId());
    }

    @Test
    void testUpdateFail_CustomerNotExist() {
        mockGetCustomerById();
        mockGetVaccineById();
        mockGetById();
        var request = new InjectionResultRequest(
                "-1",
                LocalDate.of(2021, 8, 19),
                "Place 1",
                LocalDate.of(2021, 7, 29),
                3,
                "",
                vaccines.get(0).getId()
        );
        String id = "1";
        NotFoundException notFoundException = assertThrows(
                NotFoundException.class,
                () -> injectionResultService.update(request, id)
        );
        assertEquals(Message.MSG_111, notFoundException.getDescription());
        assertEquals(request.getCustomerId(), notFoundException.getId());
    }

    @Test
    void testUpdateFail_CustomerStatusInvalid() {
        mockGetCustomerById();
        mockGetVaccineById();
        mockGetById();
        var request = new InjectionResultRequest(
                customers.get(1).getId(),
                LocalDate.of(2021, 8, 19),
                "Place 1",
                LocalDate.of(2021, 7, 29),
                3,
                "",
                vaccines.get(0).getId()
        );
        InvalidStatusException invalidStatusException = assertThrows(
                InvalidStatusException.class,
                () -> injectionResultService.update(request, "1")
        );
        assertEquals(Message.MSG_116, invalidStatusException.getDescription());

    }

    @Test
    void testUpdateFail_InsufficientNumberOfInjection() {
        mockGetCustomerById();
        mockGetVaccineById();
        mockGetById();
        var request = new InjectionResultRequest(
                customers.get(0).getId(),
                LocalDate.of(2021, 8, 19),
                "Place 1",
                LocalDate.of(2021, 7, 29),
                100,
                "",
                vaccines.get(0).getId()
        );
        InsufficientInjectionException insufficientInjectionException = assertThrows(
                InsufficientInjectionException.class,
                () -> injectionResultService.update(request, "1")
        );
        assertEquals(Message.MSG_181, insufficientInjectionException.getDescription());

    }
    @Test
    void updateSuccess() {
        mockSave();
        mockGetById();
        mockGetCustomerById();
        mockGetVaccineById();
        var request = new InjectionResultRequest(
                customers.get(0).getId(),
                LocalDate.of(2021, 8, 20),
                "Place 2",
                LocalDate.of(2021, 8, 30),
                2,
                "Prevention 2",
                vaccines.get(0).getId()
        );

        assertTrue(injectionResultService.update(request, "1"));
        InjectionResult updatedInjectionResult = injectionResults.get(0);

        assertEquals(request.getCustomerId(), updatedInjectionResult.getCustomer().getId());
        assertEquals(request.getInjectionDate(), updatedInjectionResult.getInjectionDate());
        assertEquals(request.getInjectionPlace(), updatedInjectionResult.getInjectionPlace());
        assertEquals(request.getNextInjectionDate(), updatedInjectionResult.getNextInjectionDate());
        assertEquals(request.getNumberOfInjection(), updatedInjectionResult.getNumberOfInjection());
        assertEquals(request.getPrevention(), updatedInjectionResult.getPrevention());
        assertEquals(request.getVaccineId(), updatedInjectionResult.getVaccine().getId());
    }

    private void mockExistById() {
        when(injectionResultRepository.existsById(any(String.class))).then(
                answer -> {
                    String id = answer.getArgument(0);
                    return injectionResults.stream().anyMatch(result -> result.getId().equals(id));
                }
        );
    }

    @Test
    void getAllSuccess() {
        int page = 2;
        int size = 4;
        String search = "";
        Pageable pageable = PageRequest.of(page - 1, size);
        mockPagingData();
        PageDto<InjectionResultDto> actual = injectionResultService.getAll(page, size, search);
        PageDto<InjectionResultDto> expected = new PageDto<>();
        expected.setPage(page);
        expected.setSize(size);
        expected.setTotalPages((int) Math.ceil(1.0 * injectionResults.size() / size));
        expected.setTotalElements(injectionResults.size());

        List<InjectionResultDto> expectedData = getInjectionResults(page, size, search).stream()
                .map(result -> {
                    InjectionResultDto dto = mapper.map(result, InjectionResultDto.class);
                    dto.setVaccineName(result.getVaccine().getName());
                    dto.setCustomer(String.format("%s - %s - %s",
                            result.getCustomer().getIdentityCard(),
                            result.getCustomer().getFullName(),
                            result.getCustomer().getDateOfBirth().format(dtf)));
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
        PageDto<InjectionResultDto> actual = injectionResultService.getAll(page, size, search);

        PageDto<InjectionResultDto> expected = new PageDto<>();
        expected.setPage(page);
        expected.setSize(size);
        expected.setTotalPages((int) Math.ceil(1.0 * injectionResults.size() / size));
        expected.setTotalElements(injectionResults.size());
        List<InjectionResultDto> expectedData = getInjectionResults(page, size, search).stream()
                .map(result -> {
                    InjectionResultDto dto = mapper.map(result, InjectionResultDto.class);
                    dto.setVaccineName(result.getVaccine().getName());
                    dto.setCustomer(String.format("%s - %s - %s",
                            result.getCustomer().getIdentityCard(),
                            result.getCustomer().getFullName(),
                            result.getCustomer().getDateOfBirth().format(dtf)));
                    return dto;
                })
                .toList();
        expected.setData(expectedData);
        System.out.println(expected.getData());
        System.out.println(actual.getData());
        assertEquals(expected, actual);
    }

    @Test
    void getByIdSuccess() {
        String id = "3";
        mockGetById();
        mockExistById();
        InjectionResultDetailDto actual = injectionResultService.getById(id);
        InjectionResultDetailDto expected = mapper.map(getInjectionResultById(id).get(), InjectionResultDetailDto.class);
        assertEquals(expected, actual);
    }

    @Test
    void getByIdNotFound() {
        String id = "33";
        mockExistById();
        NotFoundException exception = assertThrows(NotFoundException.class, () -> injectionResultService.getById(id));
    }

    @Test
    void deleteSuccess() {
        String ids = "1,2";
        mockSaveAll();
        mockFindAllById();
        injectionResultService.deleteByIds(ids);
        List<String> idList = Arrays.asList(ids.split(","));
        for (String id : idList) {
            assertTrue(injectionResults.get(Integer.parseInt(id)-1).isDeleted());
        }
    }

}