package fa.training.backend.service;

import fa.training.backend.dto.request.customer.CustomerAddRequest;
import fa.training.backend.dto.request.customer.CustomerUpdateRequest;
import fa.training.backend.dto.response.customer.CustomerDto;
import fa.training.backend.dto.response.PageDto;
import fa.training.backend.exception.common.NotFoundException;
import fa.training.backend.model.Customer;
import fa.training.backend.repository.CustomerRepository;
import fa.training.backend.service.impl.CustomerServiceImpl;
import fa.training.backend.util.Gender;
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
import org.springframework.security.crypto.password.PasswordEncoder;

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
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;
    @Autowired
    private ModelMapper mapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Validator validator;

    private CustomerService customerService;

    private static List<Customer> customers;

    private static List<String> errorMessages;

    @BeforeAll
    static void setUpAll() {
        customers = new ArrayList<>();
        customers.add(new Customer("1", "address1", LocalDate.of(1997, 8, 19), "email1@gmail.com", "fullname1", Gender.FEMALE, "123456789121", "password1", "0123456789", "username1", Status.ACTIVE));
        customers.add(new Customer("2", "address2", LocalDate.of(1995, 5, 15), "email2@gmail.com", "fullname2", Gender.MALE, "234567891234", "password2", "0123456788", "username2", Status.INACTIVE));
        customers.add(new Customer("3", "address3", LocalDate.of(1999, 3, 22), "email3@gmail.com", "fullname3", Gender.FEMALE, "345678912345", "pass1234", "0123456787", "username3", Status.ACTIVE));
        customers.add(new Customer("4", "address4", LocalDate.of(1990, 1, 10), "email4@gmail.com", "fullname4", Gender.MALE, "456789123456", "passwrd4", "0123456786", "username4", Status.ACTIVE));
        customers.add(new Customer("5", "address5", LocalDate.of(1998, 6, 25), "email5@gmail.com", "fullname5", Gender.FEMALE, "567891234567", "mypwd123", "0123456785", "username5", Status.INACTIVE));
        customers.add(new Customer("6", "address6", LocalDate.of(1996, 12, 8), "email6@gmail.com", "fullname6", Gender.MALE, "678912345678", "secure456", "0123456784", "username6", Status.ACTIVE));
        customers.add(new Customer("7", "address7", LocalDate.of(1993, 11, 14), "email7@gmail.com", "fullname7", Gender.FEMALE, "789123456789", "strong1!", "0123456783", "username7", Status.ACTIVE));
        customers.add(new Customer("8", "address8", LocalDate.of(1992, 7, 9), "email8@gmail.com", "fullname8", Gender.MALE, "891234567891", "better456", "0123456782", "username8", Status.INACTIVE));
        customers.add(new Customer("9", "address9", LocalDate.of(2000, 2, 28), "email9@gmail.com", "fullname9", Gender.FEMALE, "912345678912", "nicepass", "0123456781", "username9", Status.ACTIVE));
        customers.add(new Customer("10", "address10", LocalDate.of(1994, 9, 16), "email10@gmail.com", "fullname10", Gender.MALE, "123456789101", "pass6789", "0123456780", "username10", Status.INACTIVE));

        errorMessages = List.of(
                "Address must not be null",
                "Address length must not exceed 100 characters",
                "Date of birth must not be null",
                "Email must not be null",
                "Email length must not exceed 25 characters",
                "Invalid email",
                "Full name must not be null",
                "Full name length must not exceed 100 characters",
                "Password must not be null",
                "Password must have at least 6 characters",
                "Identity card must not be null",
                "Identity card must be a string of 12 digits",
                "Phone must not be null",
                "Phone number must be 10 or 11 digits starting with 0",
                "Username must not be null",
                "Username length must not exceed 10 characters",
                "Status must not be null"
        );
    }

    @AfterEach
    void tearDown() {
    }

    @BeforeEach
    void setUp() {
        this.customerService = new CustomerServiceImpl(customerRepository, passwordEncoder, mapper);
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            this.validator = factory.getValidator();
        }
    }

    private List<Customer> getCustomers(int page, int size, String search) {
        return customers
                .stream()
                .filter(customer -> customer.getFullName()
                        .contains(search))
                .skip(size * (page - 1))
                .limit(size)
                .collect(Collectors.toList());
    }

    private void mockPagingData() {
        when(customerRepository.findByFullNameContainsIgnoreCase(any(Pageable.class), any(String.class)))
                .thenAnswer(
                        answer -> {
                            Pageable p = answer.getArgument(0);
                            String search = answer.getArgument(1);
                            return new PageImpl<>(getCustomers(p.getPageNumber() + 1, p.getPageSize(), search), p, customers.size());
                        }
                );
    }

    private void mockSave() {
        when(customerRepository.save(any(Customer.class))).then(
                answer -> {
                    Customer c = answer.getArgument(0);
                    if (c.getId() == null) {
                        c.setId(String.valueOf(customers.size()));
                        customers.add(c);
                    } else {
                        int index = Integer.parseInt(c.getId()) - 1;
                        customers.set(index, c);
                    }
                    return c;
                }
        );
    }

    private void mockGetById() {
        when(customerRepository.findById(any(String.class))).then(
                answer -> {
                    String id = answer.getArgument(0);
                    return customers.stream().filter(c -> c.getId().equals(id)).findFirst();
                }
        );
    }

    private Optional<Customer> getCustomerById(String id) {
        return customers.stream().filter(customer -> customer.getId().equals(id)).findFirst();
    }

    @Test
    void addSuccess() {
        mockSave();
        int listSize = customers.size();
        CustomerAddRequest request = new CustomerAddRequest(
                "address 1",
                LocalDate.of(1997, 8, 19),
                Gender.FEMALE,
                "email1@gmail",
                "fullname1",
                "123456",
                "123456789121",
                "0123456789",
                "username1");

        customerService.add(request);
        assertTrue(customers.size() == (listSize + 1));
        Customer addedCustomer = customers.get(listSize);
        assertEquals(request.getAddress(), addedCustomer.getAddress());
        assertEquals(request.getDateOfBirth(), addedCustomer.getDateOfBirth());
        assertEquals(request.getEmail(), addedCustomer.getEmail());
        assertEquals(request.getFullName(), addedCustomer.getFullName());
        assertTrue(passwordEncoder.matches(request.getPassword(), addedCustomer.getPassword()));
        assertEquals(request.getGender(), addedCustomer.getGender());
        assertEquals(request.getIdentityCard(), addedCustomer.getIdentityCard());
        assertEquals(request.getPhone(), addedCustomer.getPhone());
        assertEquals(request.getUsername(), addedCustomer.getUsername());
        customers.remove(listSize);
    }

    @Test
    void testAddFail_ViolateConstraint() {
        var request = new CustomerAddRequest(
                "    ",
                null,
                Gender.FEMALE,
                "",
                "1234",
                null,
                "123456789",
                "emailgmail",
                "    "
        );

        Set<ConstraintViolation<CustomerAddRequest>> violations = this.validator.validate(request);
        assertEquals(9, violations.size());
        for (var violation : violations) {
            assertTrue(errorMessages.contains(violation.getMessage()));
        }
    }


    @Test
    void testUpdateFail_ViolateConstraint() {
        var request = new CustomerUpdateRequest(
                "  ",
                LocalDate.parse("2000-01-01"),
                "",
                null,
                Gender.FEMALE,
                "123456789",
                "",
                null,
                Status.ACTIVE
        );

        Set<ConstraintViolation<CustomerUpdateRequest>> violations = this.validator.validate(request);
        assertEquals(9, violations.size());
        for (var violation : violations) {
            assertTrue(errorMessages.contains(violation.getMessage()));
        }
    }

    @Test
    void updateSuccess() {
        mockSave();
        mockGetById();
        var request = new CustomerUpdateRequest(
                "address 1",
                LocalDate.of(1997, 8, 19),
                "email@gmail.com",
                "Linh",
                Gender.FEMALE,
                "123456789101",
                "0123456789",
                "linhtran",
                Status.ACTIVE
        );

        customerService.update(request, "1");

        Customer updatedCustomer = customers.get(0);
        assertEquals(request.getAddress(), updatedCustomer.getAddress());
        assertEquals(request.getDateOfBirth(), updatedCustomer.getDateOfBirth());
        assertEquals(request.getEmail(), updatedCustomer.getEmail());
        assertEquals(request.getFullName(), updatedCustomer.getFullName());
        assertEquals(request.getGender(), updatedCustomer.getGender());
        assertEquals(request.getIdentityCard(), updatedCustomer.getIdentityCard());
        assertEquals(request.getPhone(), updatedCustomer.getPhone());
        assertEquals(request.getUsername(), updatedCustomer.getUsername());
        assertEquals(request.getStatus(), updatedCustomer.getStatus());
    }

    @Test
    void getAllSuccess() {
        int page = 2;
        int size = 4;
        String search = "";
        Pageable pageable = PageRequest.of(page - 1, size);
        mockPagingData();
        PageDto<CustomerDto> actual = customerService.getAll(pageable, search);

        PageDto<CustomerDto> expected = new PageDto<>();
        expected.setPage(page);
        expected.setSize(size);
        expected.setTotalPages((int) Math.ceil(1.0 * customers.size() / size));
        expected.setTotalElements(customers.size());
        expected.setData(getCustomers(page, size, search).stream().map(customer -> mapper.map(customer, CustomerDto.class)).toList());
        assertEquals(expected, actual);
    }

    @Test
    void searchSuccess() {
        int page = 2;
        int size = 4;
        String search = "name";
        Pageable pageable = PageRequest.of(page - 1, size);
        mockPagingData();
        PageDto<CustomerDto> actual = customerService.getAll(pageable, search);

        PageDto<CustomerDto> expected = new PageDto<>();
        expected.setPage(page);
        expected.setSize(size);
        expected.setTotalPages((int) Math.ceil(1.0 * customers.size() / size));
        expected.setTotalElements(customers.size());
        expected.setData(getCustomers(page, size, search).stream().map(customer -> mapper.map(customer, CustomerDto.class)).toList());
        assertEquals(expected, actual);
    }

    @Test
    void getByIdSuccess() {
        String id = "3";
        mockGetById();

        CustomerDto actual = customerService.getById(id);
        CustomerDto expected = mapper.map(getCustomerById(id).get(), CustomerDto.class);
        assertEquals(expected, actual);
    }

    @Test
    void getByIdNotFound() {
        String id = "33";
        mockGetById();
        NotFoundException exception = assertThrows(NotFoundException.class, () -> customerService.getById(id));
        Message expected = Message.MSG_111;
        assertEquals(expected, exception.getDescription());
    }

    @Test
    void switchStatusSuccess() {
        mockGetById();
        mockSave();
        String id = "1";
        Status expected = customers.get(0).getStatus() == Status.ACTIVE ? Status.INACTIVE : Status.ACTIVE;
        customerService.switchStatus(id);
        Status actual = customers.get(0).getStatus();
        assertEquals(expected, actual);
    }

    @Test
    void switchStatusFail_IdNotFound() {
        mockGetById();
        String id = "-1";
        NotFoundException exception = assertThrows(NotFoundException.class, () -> customerService.switchStatus(id));
        Message expected = Message.MSG_111;
        assertEquals(expected, exception.getDescription());
    }

}