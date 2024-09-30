package fa.training.backend.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fa.training.backend.dto.request.employee.EmployeeRequest;
import fa.training.backend.dto.request.employee.EmployeeUpdateDto;
import fa.training.backend.dto.response.employee.EmployeeListDto;
import fa.training.backend.dto.response.PageDto;
import fa.training.backend.exception.common.NotFoundException;
import fa.training.backend.exception.handler.GlobalExceptionHandler;
import fa.training.backend.service.EmployeeService;
import fa.training.backend.util.Gender;
import fa.training.backend.util.LocalDateAdapter;
import fa.training.backend.util.Message;
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

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
@ContextConfiguration(classes = {EmployeeController.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class EmployeeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Gson gson;

    @MockBean
    private EmployeeService employeeService;

    private String uri = "/api/v1/employees";

    EmployeeControllerTest() throws MalformedURLException {
    }


    @BeforeEach
    void setUp() {
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter()) // Register LocalDateAdapter
                .create();
    }

    @AfterEach
    void tearDown() {
    }

    private EmployeeUpdateDto employeeUpdateDto = new EmployeeUpdateDto("1", "address1", LocalDate.of(1997, 8, 19), "email1@gmail.com", "name1", Gender.FEMALE, "0969158630", "position1", "username1", "workplace1", "image1", new URL("http://localhost:8080/api/v1/employees/1/image"));


    private void mockSave() {
        when(employeeService.createEmployee(any(EmployeeRequest.class))).thenReturn(true);
    }

    private void mockFindById() {
        when(employeeService.getEmployeeById(any(String.class))).thenAnswer(
                answer -> {
                    String id = answer.getArgument(0);
                    if (id.equals("-1")) {
                        throw new NotFoundException(Message.MSG_161, id);
                    }
                    return employeeUpdateDto;
                }
        );
    }

    private void mockUpdate() {
        when(employeeService.updateEmployee(any(EmployeeUpdateDto.class), anyString())).thenAnswer(
                answer -> {
                    EmployeeUpdateDto request = answer.getArgument(0);
                    String id = answer.getArgument(1);
                    if ("-1".equals(id)) {
                        throw new NotFoundException(Message.MSG_161, id);
                    }
                    return true;
                }
        );
    }

    @Test
    void addSuccess() throws Exception {
        mockSave();
        EmployeeRequest request = new EmployeeRequest(
                "name1",
                Gender.FEMALE,
                LocalDate.of(1997, 8, 19),
                "0969158630",
                "address1",
                "email1@gmail.com",
                "workplace1",
                "position1",
                "url1",
                "username1",
                "123456"
        );

        mockMvc.perform(
                        post(uri)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(gson.toJson(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(Message.MSG_263.getCode()))
                .andExpect(jsonPath("$.description").value(Message.MSG_263.getMessage()))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void addFail_ViolateConstraint() throws Exception {
        mockSave();
        EmployeeRequest request = new EmployeeRequest(
                "",
                null,
                LocalDate.of(1997, 8, 19),
                "09691",
                "",
                "email1",
                "workplace1",
                "position1",
                "url1",
                "username1",
                "123456"
        );
        mockMvc.perform(
                        post(uri)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(gson.toJson(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Name is required"))
                .andExpect(jsonPath("$.gender").value("Gender is required"))
                .andExpect(jsonPath("$.phone").value("Phone number must be 10 or 11 digits starting with 0"))
                .andExpect(jsonPath("$.address").value("Address is required"))
                .andExpect(jsonPath("$.email").value("Wrong email!"));
    }

    @Test
    void getById() throws Exception {
        mockFindById();
        String id = "1";
        String uri = "/api/v1/employees/" + id;
        mockMvc.perform(
                        get(uri)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(Message.MSG_262.getCode()))
                .andExpect(jsonPath("$.description").value(Message.MSG_262.getMessage()))
                .andExpect(jsonPath("$.data.id").value(employeeUpdateDto.getId()))
                .andExpect(jsonPath("$.data.name").value(employeeUpdateDto.getName()))
                .andExpect(jsonPath("$.data.gender").value(employeeUpdateDto.getGender().toString()))
                .andExpect(jsonPath("$.data.dateOfBirth").value(employeeUpdateDto.getDateOfBirth().toString()))
                .andExpect(jsonPath("$.data.phone").value(employeeUpdateDto.getPhone()))
                .andExpect(jsonPath("$.data.address").value(employeeUpdateDto.getAddress()))
                .andExpect(jsonPath("$.data.email").value(employeeUpdateDto.getEmail()))
                .andExpect(jsonPath("$.data.workPlace").value(employeeUpdateDto.getWorkPlace()))
                .andExpect(jsonPath("$.data.position").value(employeeUpdateDto.getPosition()))
                .andExpect(jsonPath("$.data.username").value(employeeUpdateDto.getUsername()));
    }

    @Test
    void getById_NotFound() throws Exception {

        mockFindById();
        String id = "-1";
        String uri = "/api/v1/employees/" + id;

        mockMvc.perform(
                        get(uri)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(Message.MSG_161.getCode()))
                .andExpect(jsonPath("$.description").value(Message.MSG_161.getMessage()));
    }

    @Test
    void update_Success() throws Exception {
        mockUpdate();
        mockFindById();

        String id = "1";
        String uri = "/api/v1/employees/" + id;

        EmployeeUpdateDto request = new EmployeeUpdateDto(
                "1",
                "address2",
                LocalDate.of(1997, 8, 20),
                "email2@gmail.com",
                "name2",
                Gender.FEMALE,
                "0969158631",
                "position2",
                "username2",
                "workplace2",
                "image2",
                new URL("http://localhost:8080/api/v1/employees/1/image")
        );

        mockMvc.perform(
                        put(uri)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(gson.toJson(request))
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(Message.MSG_264.getCode()))
                .andExpect(jsonPath("$.description").value(Message.MSG_264.getMessage()))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void update_Fail_IdNotFound() throws Exception {
        mockUpdate();
        String id = "-1";
        String uri = "/api/v1/employees/" + id;

        EmployeeUpdateDto request = new EmployeeUpdateDto(
                "1",
                "address2",
                LocalDate.of(1997, 8, 20),
                "email2@gmail.com",
                "name2",
                Gender.FEMALE,
                "0969158631",
                "position2",
                "username2",
                "workplace2",
                "image2",
                new URL("http://localhost:8080/api/v1/employees/1/image")
        );

        mockMvc.perform(
                        put(uri)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(gson.toJson(request))
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(Message.MSG_161.getCode()))
                .andExpect(jsonPath("$.description").value(Message.MSG_161.getMessage()));
    }

    @Test
    void getAll_Success() throws Exception {
        EmployeeListDto employeeListDto1 = new EmployeeListDto("1", "name1", LocalDate.of(1997, 8, 19), Gender.FEMALE, "0969158630", "address1", new URL("http://localhost:8080/api/v1/employees/1/image"));
        EmployeeListDto employeeListDto2 = new EmployeeListDto("2", "name2", LocalDate.of(1997, 8, 29), Gender.MALE, "0969158631", "address2", new URL("http://localhost:8080/api/v1/employees/2/image"));

        PageDto<EmployeeListDto> pageDto = new PageDto<>(1, 5, 1, 2, List.of(employeeListDto1, employeeListDto2));

        when(employeeService.getAllEmployeesByNameContains(any(Pageable.class), anyString()))
                .thenReturn(pageDto);

        mockMvc.perform(
                        get("/api/v1/employees")
                                .param("page", "1")
                                .param("size", "5")
                                .param("query", "name")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(Message.MSG_261.getCode())) // Kiểm tra mã code trả về
                .andExpect(jsonPath("$.data.data[0].id").value(employeeListDto1.getId())) // Kiểm tra phần tử đầu tiên
                .andExpect(jsonPath("$.data.data[1].id").value(employeeListDto2.getId())); // Kiểm tra phần tử thứ hai
    }

    @Test
    void getAll_Fail_NoData() throws Exception {
        when(employeeService.getAllEmployeesByNameContains(any(Pageable.class), anyString()))
                .thenReturn(new PageDto<>(1, 5, 1, 0, Collections.emptyList()));

        mockMvc.perform(
                        get("/api/v1/employees")
                                .param("page", "1")
                                .param("size", "5")
                                .param("query", "notfound")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())  // Kiểm tra trạng thái trả về là OK (200)
                .andExpect(jsonPath("$.code").value(Message.MSG_261.getCode())) // Kiểm tra mã code trả về khi không tìm thấy
                .andExpect(jsonPath("$.data.data").isEmpty()); // Kiểm tra dữ liệu rỗng
    }
}