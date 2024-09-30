package fa.training.backend.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fa.training.backend.dto.request.injection_result.InjectionResultRequest;
import fa.training.backend.dto.response.injection_result.InjectionResultDetailDto;
import fa.training.backend.dto.response.injection_result.InjectionResultDto;
import fa.training.backend.dto.response.PageDto;
import fa.training.backend.exception.common.NotFoundException;
import fa.training.backend.exception.handler.GlobalExceptionHandler;
import fa.training.backend.service.InjectionResultService;
import fa.training.backend.service.InjectionScheduleService;
import fa.training.backend.service.impl.InjectionResultServiceImpl;
import fa.training.backend.util.LocalDateAdapter;
import fa.training.backend.util.Message;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InjectionResultController.class)
@ContextConfiguration(classes = {InjectionResultController.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class InjectionResultControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Gson gson;

    @MockBean
    private InjectionResultService injectionResultService;

    @MockBean
    private InjectionScheduleService injectionScheduleService;

    private String uri = "/api/v1/injection-results";

    @BeforeEach
    void setUp() {
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter()) // Register LocalDateAdapter
                .create();
    }

    @AfterEach
    void tearDown() {
    }

    private InjectionResultDetailDto injectionResultDetailDto = new InjectionResultDetailDto("1", "customer1", LocalDate.of(2021, 8, 19), "place1", LocalDate.of(2021, 8, 25), 3, "prevention1", "vaccine1");

    private void mockSave() {
        when(injectionResultService.add(any(InjectionResultRequest.class))).thenReturn(true);
    }

    private void mockFindById() {
        when(injectionResultService.getById(any(String.class))).thenAnswer(
                answer -> {
                    String id = answer.getArgument(0);
                    if (id.equals("-1")) {
                        throw new NotFoundException(Message.MSG_182, id);
                    }
                    return injectionResultDetailDto;
                }
        );
    }


    private void mockUpdate() {
        when(injectionResultService.update(any(InjectionResultRequest.class), anyString())).thenAnswer(
                answer -> {
                    InjectionResultRequest request = answer.getArgument(0);
                    String id = answer.getArgument(1);
                    if ("-1".equals(id)) {
                        throw new NotFoundException(Message.MSG_182, id);
                    }
                    return true;
                }
        );
    }

    @Test
    void addSuccess() throws Exception {
        mockSave();
        InjectionResultRequest request = new InjectionResultRequest( "1", LocalDate.of(2021, 8, 19), "place1", LocalDate.of(2021, 8, 25), 3, "prevention1", "vaccine1");

        mockMvc.perform(
                        post(uri)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(gson.toJson(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(Message.MSG_283.getCode()))
                .andExpect(jsonPath("$.description").value(Message.MSG_283.getMessage()))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void addFail_ViolateConstraint() throws Exception {
        mockSave();
        InjectionResultRequest request = new InjectionResultRequest( "", LocalDate.of(2021, 8, 19), "place1", LocalDate.of(2021, 8, 25), 3, "prevention1", "");

        mockMvc.perform(
                        post(uri)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(gson.toJson(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.customerId").value("Customer Id is required"))
                .andExpect(jsonPath("$.vaccineId").value("Vaccine is required"));
    }


    @Test
    void getInjectionResultById() throws Exception {
        mockFindById();
        String id = "1";
        String uri = "/api/v1/injection-results/" + id;
        mockMvc.perform(
                        get(uri)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(Message.MSG_282.getCode()))
                .andExpect(jsonPath("$.description").value(Message.MSG_282.getMessage()))
                .andExpect(jsonPath("$.data.id").value(injectionResultDetailDto.getId()))
                .andExpect(jsonPath("$.data.customerId").value(injectionResultDetailDto.getCustomerId()))
                .andExpect(jsonPath("$.data.injectionDate").value(injectionResultDetailDto.getInjectionDate().toString()))
                .andExpect(jsonPath("$.data.nextInjectionDate").value(injectionResultDetailDto.getNextInjectionDate().toString()))
                .andExpect(jsonPath("$.data.numberOfInjection").value(injectionResultDetailDto.getNumberOfInjection()))
                .andExpect(jsonPath("$.data.prevention").value(injectionResultDetailDto.getPrevention()))
                .andExpect(jsonPath("$.data.vaccineId").value(injectionResultDetailDto.getVaccineId()));
    }

    @Test
    void getInjectionResultById_NotFound() throws Exception {

        mockFindById();
        String id = "-1";
        String uri = "/api/v1/injection-results/" + id;

        mockMvc.perform(
                        get(uri)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(Message.MSG_182.getCode()))
                .andExpect(jsonPath("$.description").value(Message.MSG_182.getMessage()));
    }


    @Test
    void updateInjectionResult_Success() throws Exception {
        mockUpdate();
        mockFindById();

        String id = "1";
        String uri = "/api/v1/injection-results/" + id;

        InjectionResultRequest request = new InjectionResultRequest( "customer2", LocalDate.of(2021, 8, 20), "place2", LocalDate.of(2021, 8, 26), 2, "prevention2", "vaccine2");


        mockMvc.perform(
                        put(uri)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(gson.toJson(request))
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(Message.MSG_284.getCode()))
                .andExpect(jsonPath("$.description").value(Message.MSG_284.getMessage()))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void updateCustomer_Fail_IdNotFound() throws Exception {
        mockUpdate();
        String id = "-1";
        String uri = "/api/v1/injection-results/" + id;

        InjectionResultRequest request = new InjectionResultRequest( "customer2", LocalDate.of(2021, 8, 20), "place2", LocalDate.of(2021, 8, 26), 2, "prevention2", "vaccine2");

        mockMvc.perform(
                        put(uri)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(gson.toJson(request))
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())  // Kỳ vọng trạng thái 404 Not Found
                .andExpect(jsonPath("$.code").value(Message.MSG_182.getCode())) // Kiểm tra mã lỗi
                .andExpect(jsonPath("$.description").value(Message.MSG_182.getMessage())); // Kiểm tra thông báo lỗi
    }

    @Test
    void getAll_Success() throws Exception {
        // Mock dịch vụ trả về kết quả thành công với dữ liệu
         InjectionResultDto injectionResultDto1 = new InjectionResultDto("1", "customer1", LocalDate.of(2021, 8, 19), LocalDate.of(2021, 8, 25), 3, "prevention1", "vaccine1");
        InjectionResultDto injectionResultDto2 = new InjectionResultDto("2", "customer2", LocalDate.of(2021, 8, 20), LocalDate.of(2021, 8, 26), 2, "prevention2", "vaccine2");

        PageDto<InjectionResultDto> pageDto = new PageDto<>(1, 5, 1, 2, List.of(injectionResultDto1, injectionResultDto2));

        when(injectionResultService.getAll( anyInt() ,anyInt() ,anyString()))
                .thenReturn(pageDto);

        // Thực hiện yêu cầu GET với query
        mockMvc.perform(
                        get("/api/v1/injection-results")
                                .param("page", "1")
                                .param("size", "5")
                                .param("query", "vaccine")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(Message.MSG_281.getCode())) // Kiểm tra mã code trả về
                .andExpect(jsonPath("$.data.data[0].id").value(injectionResultDto1.getId())) // Kiểm tra phần tử đầu tiên
                .andExpect(jsonPath("$.data.data[1].id").value(injectionResultDto2.getId())); // Kiểm tra phần tử thứ hai
    }

    @Test
    void getAll_Fail_NoData() throws Exception {
        // Mock dịch vụ trả về PageDto rỗng
        when(injectionResultService.getAll(anyInt() ,anyInt() ,anyString()))
                .thenReturn(new PageDto<>(1, 5, 1,2, Collections.emptyList()));

        // Thực hiện yêu cầu GET với query nhưng không tìm thấy dữ liệu
        mockMvc.perform(
                        get("/api/v1/injection-results")
                                .param("page", "1")
                                .param("size", "5")
                                .param("query", "notfound")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())  // Kiểm tra trạng thái trả về là OK (200)
                .andExpect(jsonPath("$.code").value(Message.MSG_281.getCode())) // Kiểm tra mã code trả về khi không tìm thấy
                .andExpect(jsonPath("$.data.data").isEmpty()); // Kiểm tra dữ liệu rỗng
    }

    @Test
    void deleteSuccess() throws Exception {
        doNothing().when(injectionResultService).deleteByIds(anyString());
        mockMvc.perform(delete("/api/v1/injection-results/1,2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(Message.MSG_285.getCode()))
                .andExpect(jsonPath("$.description").value(Message.MSG_285.getMessage()))
                .andExpect(jsonPath("$.data").value(true));
    }

}