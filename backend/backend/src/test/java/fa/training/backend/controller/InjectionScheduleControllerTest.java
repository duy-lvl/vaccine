package fa.training.backend.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fa.training.backend.dto.request.injection_schedule.InjectionScheduleAddRequest;
import fa.training.backend.dto.request.injection_schedule.InjectionScheduleUpdateRequest;
import fa.training.backend.dto.response.injection_schedule.InjectionScheduleDetailDto;
import fa.training.backend.dto.response.injection_schedule.InjectionScheduleDto;
import fa.training.backend.dto.response.PageDto;
import fa.training.backend.exception.common.NotFoundException;
import fa.training.backend.exception.handler.GlobalExceptionHandler;
import fa.training.backend.service.InjectionScheduleService;
import fa.training.backend.util.InjectionScheduleStatus;
import fa.training.backend.util.LocalDateAdapter;
import fa.training.backend.util.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InjectionScheduleController.class)
@ContextConfiguration(classes = {InjectionScheduleController.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class InjectionScheduleControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Gson gson;

    @MockBean
    private InjectionScheduleService injectionScheduleService;

    private String uri = "/api/v1/injection-schedules";


    @BeforeEach
    void setUp() {
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter()) // Register LocalDateAdapter
                .create();
    }

    private InjectionScheduleDetailDto injectionScheduleDetailDto = new InjectionScheduleDetailDto(
            "1",
            "vaccineId1",
            LocalDate.of(2021, 8, 22),
            LocalDate.of(2021, 8, 20),
            "place1",
            "description1",
            InjectionScheduleStatus.OPEN
    );

    private void mockSave() {
        when(injectionScheduleService.add(any(InjectionScheduleAddRequest.class))).thenReturn(true);
    }

    private void mockFindById() {
        when(injectionScheduleService.getById(any(String.class))).thenAnswer(
                answer -> {
                    String id = answer.getArgument(0);
                    if (id.equals("-1")) {
                        throw new NotFoundException(Message.MSG_171, id);
                    }
                    return injectionScheduleDetailDto;
                }
        );
    }

    private void mockUpdate() {
        when(injectionScheduleService.update(any(InjectionScheduleUpdateRequest.class), anyString())).thenAnswer(
                answer -> {
                    InjectionScheduleUpdateRequest request = answer.getArgument(0);
                    String id = answer.getArgument(1);
                    if ("-1".equals(id)) {
                        throw new NotFoundException(Message.MSG_171, id);
                    }
                    return true;
                }
        );
    }

    @Test
    void addSuccess() throws Exception {
        mockSave();
        InjectionScheduleAddRequest request = new InjectionScheduleAddRequest(
                "vaccineId1",
                LocalDate.of(2021, 8, 20),
                LocalDate.of(2021, 8, 22),
                "place1",
                "description1"
        );

        mockMvc.perform(
                        post(uri)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(gson.toJson(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(Message.MSG_273.getCode()))
                .andExpect(jsonPath("$.description").value(Message.MSG_273.getMessage()))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void addFail_ViolateConstraint() throws Exception {
        mockSave();
        InjectionScheduleAddRequest request = new InjectionScheduleAddRequest(
                "",
                null,
                null,
                "",
                "description1"
        );
        mockMvc.perform(
                        post(uri)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(gson.toJson(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.vaccineId").value("Vaccine Id is required"))
                .andExpect(jsonPath("$.startDate").value("Start date is required"))
                .andExpect(jsonPath("$.endDate").value("End date is required"))
                .andExpect(jsonPath("$.place").value("Place is required"));
    }

    @Test
    void getById() throws Exception {
        mockFindById();
        String id = "1";
        String uri = "/api/v1/injection-schedules/" + id;
        mockMvc.perform(
                        get(uri)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(Message.MSG_272.getCode()))
                .andExpect(jsonPath("$.description").value(Message.MSG_272.getMessage()))
                .andExpect(jsonPath("$.data.id").value(injectionScheduleDetailDto.getId()))
                .andExpect(jsonPath("$.data.vaccineId").value(injectionScheduleDetailDto.getVaccineId()))
                .andExpect(jsonPath("$.data.startDate").value(injectionScheduleDetailDto.getStartDate().toString()))
                .andExpect(jsonPath("$.data.endDate").value(injectionScheduleDetailDto.getEndDate().toString()))
                .andExpect(jsonPath("$.data.place").value(injectionScheduleDetailDto.getPlace()))
                .andExpect(jsonPath("$.data.description").value(injectionScheduleDetailDto.getDescription()))
                .andExpect(jsonPath("$.data.status").value(injectionScheduleDetailDto.getStatus().toString()));
    }

    @Test
    void getById_NotFound() throws Exception {

        mockFindById();
        String id = "-1";
        String uri = "/api/v1/injection-schedules/" + id;

        mockMvc.perform(
                        get(uri)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(Message.MSG_171.getCode()))
                .andExpect(jsonPath("$.description").value(Message.MSG_171.getMessage()));
    }

    @Test
    void update_Success() throws Exception {
        mockUpdate();
        mockFindById();

        String id = "1";
        String uri = "/api/v1/injection-schedules/" + id;

        InjectionScheduleUpdateRequest request = new InjectionScheduleUpdateRequest(
                "vaccineId2",
                LocalDate.of(2021, 8, 21),
                LocalDate.of(2021, 8, 23),
                "place2",
                "description2"
        );

        mockMvc.perform(
                        put(uri)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(gson.toJson(request))
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(Message.MSG_274.getCode()))
                .andExpect(jsonPath("$.description").value(Message.MSG_274.getMessage()))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void update_Fail_IdNotFound() throws Exception {
        mockUpdate();
        String id = "-1";
        String uri = "/api/v1/injection-schedules/" + id;

        InjectionScheduleUpdateRequest request = new InjectionScheduleUpdateRequest(
                "vaccineId2",
                LocalDate.of(2021, 8, 21),
                LocalDate.of(2021, 8, 23),
                "place2",
                "description2"
        );

        mockMvc.perform(
                        put(uri)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(gson.toJson(request))
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(Message.MSG_171.getCode()))
                .andExpect(jsonPath("$.description").value(Message.MSG_171.getMessage()));
    }

    @Test
    void getAll_Success() throws Exception {
        InjectionScheduleDto injectionScheduleDto1 = new InjectionScheduleDto("1", "name1", "time1", "place1", InjectionScheduleStatus.OPEN, "descriiption1");
        InjectionScheduleDto injectionScheduleDto2 = new InjectionScheduleDto("2", "name2", "time2", "place2", InjectionScheduleStatus.OPEN, "descriiption2");

        PageDto<InjectionScheduleDto> pageDto = new PageDto<>(1, 5, 1, 2, List.of(injectionScheduleDto1, injectionScheduleDto2));

        when(injectionScheduleService.get(anyInt(), anyInt(), anyString()))
                .thenReturn(pageDto);

        mockMvc.perform(
                        get("/api/v1/injection-schedules")
                                .param("page", "1")
                                .param("size", "5")
                                .param("query", "name")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(Message.MSG_271.getCode())) // Kiểm tra mã code trả về
                .andExpect(jsonPath("$.data.data[0].id").value(injectionScheduleDto1.getId())) // Kiểm tra phần tử đầu tiên
                .andExpect(jsonPath("$.data.data[1].id").value(injectionScheduleDto2.getId())); // Kiểm tra phần tử thứ hai
    }

    @Test
    void getAll_Fail_NoData() throws Exception {
        when(injectionScheduleService.get(anyInt(), anyInt(), anyString()))
                .thenReturn(new PageDto<>(1, 5, 1, 0, Collections.emptyList()));

        mockMvc.perform(
                        get("/api/v1/injection-schedules")
                                .param("page", "1")
                                .param("size", "5")
                                .param("query", "notfound")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())  // Kiểm tra trạng thái trả về là OK (200)
                .andExpect(jsonPath("$.code").value(Message.MSG_271.getCode())) // Kiểm tra mã code trả về khi không tìm thấy
                .andExpect(jsonPath("$.data.data").isEmpty()); // Kiểm tra dữ liệu rỗng
    }
}