package fa.training.backend.service;

import fa.training.backend.dto.request.employee.EmployeeRequest;
import fa.training.backend.dto.request.employee.EmployeeUpdateDto;
import fa.training.backend.dto.response.employee.EmployeeListDto;
import fa.training.backend.dto.response.PageDto;
import fa.training.backend.exception.common.NotFoundException;
import fa.training.backend.model.Employee;
import fa.training.backend.repository.EmployeeRepository;
import fa.training.backend.service.impl.EmployeeServiceImpl;
import fa.training.backend.util.Gender;
import fa.training.backend.validator.Age;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.*;
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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.net.MalformedURLException;
import java.net.URL;
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
class EmployeeServiceTest {
    @Mock
    private EmployeeRepository employeeRepository;
    @Autowired
    private ModelMapper mapper;

    @Mock
    private FirebaseService firebaseService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Validator validator;

    private EmployeeService employeeService;

    private static List<Employee> employees;

    private static List<String> errorMessages;

    @BeforeAll
    static void setUpAll() {
        employees = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            employees.add(
                    new Employee(
                            String.valueOf(i),
                            "Address" + i,
                            LocalDate.of(1990, 1, i),
                            "email" + i + "@gmail.com",
                            "name" + i,
                            Gender.FEMALE,
                            "image" + i + ".png",
                            "password" + i,
                            "012345678" + i,
                            "position" + i,
                            "username" + i,
                            "workPlace" + i
                    )
            );
        }

        errorMessages = List.of(
                "Name is required",
                "Name length must not exceed 100 characters",
                "Gender is required",
                "Date of birth is required",
                "The age is not valid, please check date of birth",
                "Date of birth must be in the past!",
                "Phone is required",
                "Phone must not be empty!",
                "Phone is invalid",
                "Phone number must be 10 or 11 digits starting with 0",
                "Address is required",
                "Address length must not exceed 100 characters",
                "Email is required",
                "Email is invalid",
                "Wrong email!",
                "Email length must not exceed 100 characters!",
                "Working place's length must not exceed 100 characters!",
                "Position's length must not exceed 100 characters!",
                "Username must be not empty!",
                "Username is required!",
                "Username length must not exceed 255 characters!",
                "Password must be not empty!",
                "Password is required!",
                "Password length must not exceed 72 characters!"
        );
    }

    @AfterEach
    void tearDown() {
    }

    @BeforeEach
    void setUp() {
        this.employeeService = new EmployeeServiceImpl(employeeRepository, firebaseService, mapper, passwordEncoder);
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            this.validator = factory.getValidator();
        }
    }

    private List<Employee> getEmployees(int page, int size, String search) {
        return employees
                .stream()
                .filter(employee -> employee.getName()
                        .contains(search) || employee.getId().contains(search))
                .skip(size * (page - 1))
                .limit(size)
                .collect(Collectors.toList());
    }

    private void mockPagingData() {
        when(employeeRepository.findByNameContainsIgnoreCaseOrIdContainsIgnoreCase(any(Pageable.class), any(String.class)))
                .thenAnswer(
                        answer -> {
                            Pageable p = answer.getArgument(0);
                            String search = answer.getArgument(1);
                            System.out.println("Search " + search);
                            return new PageImpl<>(getEmployees(p.getPageNumber() + 1, p.getPageSize(), search), p, employees.size());
                        }
                );
    }

    private void mockSave() {
        when(employeeRepository.save(any(Employee.class))).then(
                answer -> {
                    Employee e = answer.getArgument(0);
                    if (e.getId() == null) {
                        e.setId(String.valueOf(employees.size()));
                        employees.add(e);
                    } else {
                        int index = Integer.parseInt(e.getId()) - 1;
                        employees.set(index, e);
                    }
                    return e;
                }
        );
    }

    private void mockGetFile() {
        when(firebaseService.getFileUrl(any(String.class))).then(
                answer -> {
                    String image = answer.getArgument(0);
                    return new URL("http://localhost:8080/" + image);
                }
        );
    }

    private void mockGetById() {
        when(employeeRepository.findById(any(String.class))).then(
                answer -> {
                    String id = answer.getArgument(0);
                    return employees.stream().filter(c -> c.getId().equals(id)).findFirst();
                }
        );
    }

    private void mockGetAllById() {
        when(employeeRepository.findAllById(any(List.class))).then(
                answer -> {
                    List<String> ids = answer.getArgument(0);
                    return employees.stream().filter(c -> ids.contains(c.getId())).toList();
                }
        );
    }

    private void mockSaveAll() {
        when(employeeRepository.saveAll(any(List.class))).then(
                answer -> {
                    List<Employee> employeeList = answer.getArgument(0);
                    for (Employee e : employeeList) {
                        if (e.getId() == null) {
                            e.setId(String.valueOf(employees.size()));
                            employees.add(e);
                        } else {
                            int index = Integer.parseInt(e.getId()) - 1;
                            employees.set(index, e);
                        }
                    }
                    return employeeList;
                }
        );
    }

    private Optional<Employee> getEmployeeById(String id) {
        return employees.stream().filter(employee -> employee.getId().equals(id)).findFirst();
    }

    @Test
    void addSuccess() {
        mockSave();
        int listSize = employees.size();
        EmployeeRequest request = new EmployeeRequest(
                "name 1",
                Gender.FEMALE,
                LocalDate.of(1997, 8, 19),
                "0123456789",
                "address1",
                "email1@gmail.com",
                "workplace1",
                "position1",
                "url1.png",
                "username1",
                "123456"
        );
        employeeService.createEmployee(request);
        assertTrue(employees.size() == (listSize + 1));
        Employee addedEmployee = employees.get(listSize);
        assertEquals(request.getName(), addedEmployee.getName());
        assertEquals(request.getGender(), addedEmployee.getGender());
        assertEquals(request.getDateOfBirth(), addedEmployee.getDateOfBirth());
        assertEquals(request.getPhone(), addedEmployee.getPhone());
        assertEquals(request.getAddress(), addedEmployee.getAddress());
        assertEquals(request.getEmail(), addedEmployee.getEmail());
        assertEquals(request.getWorkPlace(), addedEmployee.getWorkPlace());
        assertEquals(request.getPosition(), addedEmployee.getPosition());
        assertEquals(request.getUrlOfImage(), addedEmployee.getImage());
        assertEquals(request.getUsername(), addedEmployee.getUsername());
        assertTrue(passwordEncoder.matches(request.getPassword(), addedEmployee.getPassword()));
        employees.remove(listSize);
    }

    @Test
    void testAddFail_ViolateConstraint() {
        var request = new EmployeeRequest(
                "   ",
                null,
                null,
                "123456789",
                "",
                "email1gmail.com",
                "",
                "",
                "url1.png",
                "username1",
                "123456"
        );

        Set<ConstraintViolation<EmployeeRequest>> violations = this.validator.validate(request);
        assertEquals(7, violations.size());
        for (var violation : violations) {
            assertTrue(errorMessages.contains(violation.getMessage()));
        }
    }


    @Test
    void testUpdateFail_ViolateConstraint() {
        var request = new EmployeeRequest(
                "   ",
                null,
                null,
                "123456789",
                "",
                "email1gmail.com",
                "",
                "",
                "url1.png",
                "username1",
                "123456"
        );

        Set<ConstraintViolation<EmployeeRequest>> violations = this.validator.validate(request);
        assertEquals(7, violations.size());
        for (var violation : violations) {
            assertTrue(errorMessages.contains(violation.getMessage()));
        }
    }

    @Test
    void updateSuccess() throws MalformedURLException {
        mockSave();
        mockGetById();

        var request = new EmployeeUpdateDto(
                "1",
                "address2",
                LocalDate.of(1998, 9, 20),
                "email2@gmail.com",
                "name2",
                Gender.MALE,
                "0123456777",
                "position2",
                "username23",
                "workplace34",
                "image1",
                new URL("http://localhost:8080/image1")
        );

        assertTrue(employeeService.updateEmployee(request, "1"));
        Employee updatedEmployee = employees.get(0);
        assertEquals(request.getName(), updatedEmployee.getName());
        assertEquals(request.getGender(), updatedEmployee.getGender());
        assertEquals(request.getDateOfBirth(), updatedEmployee.getDateOfBirth());
        assertEquals(request.getPhone(), updatedEmployee.getPhone());
        assertEquals(request.getAddress(), updatedEmployee.getAddress());
        assertEquals(request.getEmail(), updatedEmployee.getEmail());
        assertEquals(request.getWorkPlace(), updatedEmployee.getWorkPlace());
        assertEquals(request.getPosition(), updatedEmployee.getPosition());
        assertEquals(request.getUsername(), updatedEmployee.getUsername());
        assertEquals(request.getImage(), updatedEmployee.getImage());
    }

    @Test
    void getAllSuccess() {
        int page = 2;
        int size = 4;
        String search = "";
        Pageable pageable = PageRequest.of(page - 1, size);
        mockPagingData();
        PageDto<EmployeeListDto> actual = employeeService.getAllEmployeesByNameContains(pageable, search);

        PageDto<EmployeeListDto> expected = new PageDto<>();
        expected.setPage(page);
        expected.setSize(size);
        expected.setTotalPages((int) Math.ceil(1.0 * employees.size() / size));
        expected.setTotalElements(employees.size());
        expected.setData(getEmployees(page, size, search).stream().map(employee -> mapper.map(employee, EmployeeListDto.class)).toList());
        assertEquals(expected, actual);
    }

    @Test
    void searchSuccess() {
        int page = 2;
        int size = 4;
        String search = "name";
        Pageable pageable = PageRequest.of(page - 1, size);
        mockPagingData();
        PageDto<EmployeeListDto> actual = employeeService.getAllEmployeesByNameContains(pageable, search);

        PageDto<EmployeeListDto> expected = new PageDto<>();
        expected.setPage(page);
        expected.setSize(size);
        expected.setTotalPages((int) Math.ceil(1.0 * employees.size() / size));
        expected.setTotalElements(employees.size());
        expected.setData(getEmployees(page, size, search).stream().map(employee -> mapper.map(employee, EmployeeListDto.class)).toList());
        assertEquals(expected, actual);
    }

    @Test
    void getByIdSuccess() {
        String id = "2";
        mockGetById();
        mockGetFile();
        EmployeeUpdateDto actual = employeeService.getEmployeeById(id);
        Employee employee = getEmployeeById(id).get();
        EmployeeUpdateDto expected = mapper.map(employee, EmployeeUpdateDto.class);
        expected.setUrlImage(firebaseService.getFileUrl(employee.getImage()));
        assertEquals(expected, actual);
    }

    @Test
    void getByIdNotFound() {
        String id = "33";
        mockGetById();
        NotFoundException exception = assertThrows(NotFoundException.class, () -> employeeService.getEmployeeById(id));
    }

    @Test
    void testDeleteSuccess() {
        mockGetAllById();
        mockSaveAll();
        int listSize = employees.size();
        assertTrue(employeeService.deleteEmployeesByIds("5"));
        assertEquals(listSize, employees.size());
        Optional<Employee> employeeOptional = employees.stream().filter(n -> n.getId().equals("5")).findFirst();
        assertTrue(employeeOptional.isPresent());
        assertTrue(employeeOptional.get().isDeleted());

    }
}