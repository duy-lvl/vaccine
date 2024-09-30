package fa.training.backend.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fa.training.backend.dto.request.customer.CustomerAddRequest;
import fa.training.backend.dto.request.customer.CustomerUpdateRequest;
import fa.training.backend.dto.response.customer.CustomerDto;
import fa.training.backend.dto.response.PageDto;
import fa.training.backend.exception.common.NotFoundException;
import fa.training.backend.exception.handler.GlobalExceptionHandler;
import fa.training.backend.service.CustomerService;
import fa.training.backend.util.Gender;
import fa.training.backend.util.LocalDateAdapter;
import fa.training.backend.util.Message;
import fa.training.backend.util.Status;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
@ContextConfiguration(classes = {CustomerController.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class CustomerControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Gson gson;

    @MockBean
    private CustomerService customerService;

    private String uri = "/api/v1/customers";


    @BeforeEach
    void setUp() {
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter()) // Register LocalDateAdapter
                .create();
    }

    @AfterEach
    void tearDown() {
    }

    private CustomerDto customerDto = new CustomerDto("1", "address1", LocalDate.of(1997, 8, 19), "email1@gmail.com", "fullName1", Gender.FEMALE, "123456789011", "0969158630", "username1", Status.ACTIVE);

    private void mockSave() {
        when(customerService.add(any(CustomerAddRequest.class))).thenReturn(true);
    }

    private void mockFindById() {
        when(customerService.getById(any(String.class))).thenAnswer(
                answer -> {
                    String id = answer.getArgument(0);
                    if (id.equals("-1")) {
                        throw new NotFoundException(Message.MSG_111, id);
                    }
                    return customerDto;
                }
        );
    }

    private void mockUpdate() {
        when(customerService.update(any(CustomerUpdateRequest.class), anyString())).thenAnswer(
                answer -> {
                    CustomerUpdateRequest request = answer.getArgument(0);
                    String id = answer.getArgument(1);
                    if ("-1".equals(id)) {
                        throw new NotFoundException(Message.MSG_111, id);
                    }
                    return true;
                }
        );
    }

    @Test
    void addSuccess() throws Exception {
        mockSave();
        CustomerAddRequest request = new CustomerAddRequest("fullName1", LocalDate.of(1997, 8, 19), Gender.FEMALE, "123456789011", "address1", "username1", "123456", "email1@gmail.com", "0969158630" );

        mockMvc.perform(
                        post(uri)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(gson.toJson(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(Message.MSG_212.getCode()))
                .andExpect(jsonPath("$.description").value(Message.MSG_212.getMessage()))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void addFail_ViolateConstraint() throws Exception {
        mockSave();
        CustomerAddRequest request = new CustomerAddRequest("fullName1", LocalDate.of(1997, 8, 19), Gender.FEMALE, "123456789011", "", "username1", "123", "email1@gmail.com", "0969158630" );

        mockMvc.perform(
                        post(uri)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(gson.toJson(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.address").value("Address must not be null"))
                .andExpect(jsonPath("$.password").value("Password must be at least 6 characters"));
    }

    @Test
    void getCustomerById() throws Exception {
        mockFindById();
        String id = "1";
        String uri = "/api/v1/customers/" + id;
        mockMvc.perform(
                        get(uri)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(Message.MSG_213.getCode()))
                .andExpect(jsonPath("$.description").value(Message.MSG_213.getMessage()))
                .andExpect(jsonPath("$.data.id").value(customerDto.getId()))
                .andExpect(jsonPath("$.data.address").value(customerDto.getAddress()))
                .andExpect(jsonPath("$.data.dateOfBirth").value(customerDto.getDateOfBirth().toString()))
                .andExpect(jsonPath("$.data.email").value(customerDto.getEmail()))
                .andExpect(jsonPath("$.data.fullName").value(customerDto.getFullName()))
                .andExpect(jsonPath("$.data.gender").value(customerDto.getGender().toString()))
                .andExpect(jsonPath("$.data.identityCard").value(customerDto.getIdentityCard()))
                .andExpect(jsonPath("$.data.phone").value(customerDto.getPhone()))
                .andExpect(jsonPath("$.data.username").value(customerDto.getUsername()))
                .andExpect(jsonPath("$.data.status").value(customerDto.getStatus().toString()));
    }

    @Test
    void getVaccineTypeById_NotFound() throws Exception {

        mockFindById();
        String id = "-1";
        String uri = "/api/v1/customers/" + id;

        mockMvc.perform(
                        get(uri)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(Message.MSG_111.getCode()))
                .andExpect(jsonPath("$.description").value(Message.MSG_111.getMessage()));
    }

    @Test
    void updateCustomer_Success() throws Exception {
        mockUpdate();
        mockFindById();

        String id = "1";
        String uri = "/api/v1/customers/" + id;

        CustomerUpdateRequest request = new CustomerUpdateRequest(
                "address2",
                LocalDate.of(1998, 9, 17),
                "email2@gmail",
                "fullName2",
                Gender.MALE,
                "342354365434",
                "0969158631",
                "username2",
                Status.INACTIVE
        );

        mockMvc.perform(
                        put(uri)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(gson.toJson(request))
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(Message.MSG_214.getCode()))
                .andExpect(jsonPath("$.description").value(Message.MSG_214.getMessage()))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void updateCustomer_Fail_IdNotFound() throws Exception {
        mockUpdate();
        String id = "-1";
        String uri = "/api/v1/customers/" + id;

        CustomerUpdateRequest request = new CustomerUpdateRequest(
                "address2",
                LocalDate.of(1998, 9, 17),
                "email2@gmail",
                "fullName2",
                Gender.MALE,
                "342354365434",
                "0969158631",
                "username2",
                Status.INACTIVE
        );

        mockMvc.perform(
                        put(uri)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(gson.toJson(request))
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(Message.MSG_111.getCode()))
                .andExpect(jsonPath("$.description").value(Message.MSG_111.getMessage()));
    }

    @Test
    void getAll_Success() throws Exception {
        CustomerDto customerDto1 = new CustomerDto("1", "address1", LocalDate.of(1997, 8, 19), "email1@gmail.com", "fullname1", Gender.FEMALE, "123456789011", "0969158630", "username1", Status.ACTIVE);
        CustomerDto customerDto2 = new CustomerDto("2", "address2", LocalDate.of(1998, 9, 17), "email2@gmail", "fullname2", Gender.MALE, "342354365434", "0969158631", "username2", Status.INACTIVE);

        PageDto<CustomerDto> pageDto = new PageDto<>(1, 5, 1, 2, List.of(customerDto1, customerDto2));

        when(customerService.getAll( any(Pageable.class),anyString()))
                .thenReturn(pageDto);

        mockMvc.perform(
                        get("/api/v1/customers")
                                .param("page", "1")
                                .param("size", "5")
                                .param("query", "name")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(Message.MSG_211.getCode())) // Kiểm tra mã code trả về
                .andExpect(jsonPath("$.data.data[0].id").value(customerDto1.getId())) // Kiểm tra phần tử đầu tiên
                .andExpect(jsonPath("$.data.data[1].id").value(customerDto2.getId())); // Kiểm tra phần tử thứ hai
    }

    @Test
    void getAll_Fail_NoData() throws Exception {
        when(customerService.getAll(any(Pageable.class), anyString()))
                .thenReturn(new PageDto<>(1, 5, 1, 0, Collections.emptyList()));

        mockMvc.perform(
                        get("/api/v1/customers")
                                .param("page", "1")
                                .param("size", "5")
                                .param("query", "notfound")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())  // Kiểm tra trạng thái trả về là OK (200)
                .andExpect(jsonPath("$.code").value(Message.MSG_211.getCode())) // Kiểm tra mã code trả về khi không tìm thấy
                .andExpect(jsonPath("$.data.data").isEmpty()); // Kiểm tra dữ liệu rỗng
    }

}