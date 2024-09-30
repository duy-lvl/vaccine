package fa.training.backend.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fa.training.backend.dto.request.vaccine.VaccineAddRequest;
import fa.training.backend.dto.request.vaccine.VaccineUpdateRequest;
import fa.training.backend.dto.response.*;
import fa.training.backend.dto.response.vaccine.VaccineIndividualDto;
import fa.training.backend.dto.response.vaccine.VaccineUpdateDto;
import fa.training.backend.exception.common.NotFoundException;
import fa.training.backend.exception.handler.GlobalExceptionHandler;
import fa.training.backend.service.VaccineService;
import fa.training.backend.util.*;
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
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VaccineController.class)
@ContextConfiguration(classes = {VaccineController.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class VaccineControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Gson gson;

    @MockBean
    private VaccineService vaccineService;

    private String uri = "/api/v1/vaccines";

    VaccineControllerTest() throws MalformedURLException {
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

    private VaccineUpdateDto vaccineUpdateDto = new VaccineUpdateDto(
            "1",
            "name1",
            "vaccineTypeId1",
            3,
            "usage1",
            "indication1",
            "contraindication1",
            LocalDate.of(2021, 8, 19),
            LocalDate.of(2021, 8, 20),
            "origin1",
            Status.ACTIVE
    );

    private void mockSave() {
        when(vaccineService.add(any(VaccineAddRequest.class))).thenReturn(true);
    }

    private void mockFindById() {
        when(vaccineService.getById(any(String.class))).thenAnswer(
                answer -> {
                    String id = answer.getArgument(0);
                    if (id.equals("-1")) {
                        throw new NotFoundException(Message.MSG_153, id);
                    }
                    return vaccineUpdateDto;
                }
        );
    }

    private void mockUpdate() {
        when(vaccineService.update(any(VaccineUpdateRequest.class), anyString())).thenAnswer(
                answer -> {
                    VaccineUpdateRequest request = answer.getArgument(0);
                    String id = answer.getArgument(1);
                    if ("-1".equals(id)) {
                        throw new NotFoundException(Message.MSG_153, id);
                    }
                    return true;
                }
        );
    }

    @Test
    void addSuccess() throws Exception {
        mockSave();
        VaccineAddRequest request = new VaccineAddRequest(
                "name1",
                "vaccineTypeId1",
                3,
                "usage1",
                "indication1",
                "contraindication1",
                "origin1"
        );

        mockMvc.perform(
                        post(uri)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(gson.toJson(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(Message.MSG_252.getCode()))
                .andExpect(jsonPath("$.description").value(Message.MSG_252.getMessage()))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void addFail_ViolateConstraint() throws Exception {
        mockSave();
        VaccineAddRequest request = new VaccineAddRequest(
                "",
                "",
                -1,
                "usage1",
                "indication1",
                "contraindication1",
                "origin1"
        );
        mockMvc.perform(
                        post(uri)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(gson.toJson(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Vaccine name is required"))
                .andExpect(jsonPath("$.vaccineTypeId").value("Vaccine type is required"))
                .andExpect(jsonPath("$.numberOfInjection").value("Number of injection must be a positive integer"));
    }

    @Test
    void getById() throws Exception {
        mockFindById();
        String id = "1";
        String uri = "/api/v1/vaccines/" + id;
        mockMvc.perform(
                        get(uri)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(Message.MSG_253.getCode()))
                .andExpect(jsonPath("$.description").value(Message.MSG_253.getMessage()))
                .andExpect(jsonPath("$.data.id").value(vaccineUpdateDto.getId()))
                .andExpect(jsonPath("$.data.name").value(vaccineUpdateDto.getName()))
                .andExpect(jsonPath("$.data.vaccineTypeId").value(vaccineUpdateDto.getVaccineTypeId()))
                .andExpect(jsonPath("$.data.numberOfInjection").value(vaccineUpdateDto.getNumberOfInjection()))
                .andExpect(jsonPath("$.data.usage").value(vaccineUpdateDto.getUsage()))
                .andExpect(jsonPath("$.data.indication").value(vaccineUpdateDto.getIndication()))
                .andExpect(jsonPath("$.data.contraindication").value(vaccineUpdateDto.getContraindication()))
                .andExpect(jsonPath("$.data.timeBeginNextInjection").value(vaccineUpdateDto.getTimeBeginNextInjection().toString()))
                .andExpect(jsonPath("$.data.timeEndNextInjection").value(vaccineUpdateDto.getTimeEndNextInjection().toString()))
                .andExpect(jsonPath("$.data.origin").value(vaccineUpdateDto.getOrigin()))
                .andExpect(jsonPath("$.data.status").value(vaccineUpdateDto.getStatus().toString()));
    }

    @Test
    void getById_NotFound() throws Exception {

        mockFindById();
        String id = "-1";
        String uri = "/api/v1/vaccines/" + id;

        mockMvc.perform(
                        get(uri)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(Message.MSG_153.getCode()))
                .andExpect(jsonPath("$.description").value(Message.MSG_153.getMessage()));
    }

    @Test
    void update_Success() throws Exception {
        mockUpdate();
        mockFindById();

        String id = "1";
        String uri = "/api/v1/vaccines/" + id;
        VaccineUpdateRequest request = new VaccineUpdateRequest(
                "name2",
                "vaccineTypeId2",
                2,
                "usage2",
                "indication2",
                "contraindication2",
                "origin2",
                Status.INACTIVE
        );

        mockMvc.perform(
                        put(uri)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(gson.toJson(request))
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(Message.MSG_254.getCode()))
                .andExpect(jsonPath("$.description").value(Message.MSG_254.getMessage()))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void update_Fail_IdNotFound() throws Exception {
        mockUpdate();
        String id = "-1";
        String uri = "/api/v1/vaccines/" + id;

        VaccineUpdateRequest request = new VaccineUpdateRequest(
                "name1",
                "vaccineTypeId1",
                3,
                "usage1",
                "indication1",
                "contraindication1",
                "origin1",
                Status.ACTIVE
        );

        mockMvc.perform(
                        put(uri)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(gson.toJson(request))
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(Message.MSG_153.getCode()))
                .andExpect(jsonPath("$.description").value(Message.MSG_153.getMessage()));
    }

    @Test
    void getAll_Success() throws Exception {
        VaccineIndividualDto vaccineIndividualDto1 = new VaccineIndividualDto("1", "name1", "vaccineTypeName1", 3, "origin1", Status.ACTIVE);
        VaccineIndividualDto vaccineIndividualDto2 = new VaccineIndividualDto("2", "name2", "vaccineTypeName2", 2, "origin2", Status.ACTIVE);

        PageDto<VaccineIndividualDto> pageDto = new PageDto<>(1, 5, 1, 2, List.of(vaccineIndividualDto1, vaccineIndividualDto2));

        when(vaccineService.getAll(any(Pageable.class), anyString()))
                .thenReturn(pageDto);

        mockMvc.perform(
                        get("/api/v1/vaccines")
                                .param("page", "1")
                                .param("size", "5")
                                .param("query", "name")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(Message.MSG_251.getCode())) // Kiểm tra mã code trả về
                .andExpect(jsonPath("$.data.data[0].id").value(vaccineIndividualDto1.getId())) // Kiểm tra phần tử đầu tiên
                .andExpect(jsonPath("$.data.data[1].id").value(vaccineIndividualDto2.getId())); // Kiểm tra phần tử thứ hai
    }

    @Test
    void getAll_Fail_NoData() throws Exception {
        when(vaccineService.getAll(any(Pageable.class), anyString()))
                .thenReturn(new PageDto<>(1, 5, 1, 0, Collections.emptyList()));

        mockMvc.perform(
                        get("/api/v1/vaccines")
                                .param("page", "1")
                                .param("size", "5")
                                .param("query", "notfound")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())  // Kiểm tra trạng thái trả về là OK (200)
                .andExpect(jsonPath("$.code").value(Message.MSG_251.getCode())) // Kiểm tra mã code trả về khi không tìm thấy
                .andExpect(jsonPath("$.data.data").isEmpty()); // Kiểm tra dữ liệu rỗng
    }
}