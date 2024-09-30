package fa.training.backend.controller;

import com.google.gson.Gson;
import fa.training.backend.dto.request.vaccine_type.NewVaccineTypeRequest;
import fa.training.backend.dto.request.vaccine_type.UpdateVaccineTypeRequest;
import fa.training.backend.dto.response.PageDto;
import fa.training.backend.dto.response.vaccine_type.VaccineTypeListDto;
import fa.training.backend.dto.response.vaccine_type.VaccineTypeUpdateDto;
import fa.training.backend.exception.handler.GlobalExceptionHandler;
import fa.training.backend.exception.common.NotFoundException;
import fa.training.backend.model.Admin;
import fa.training.backend.service.FirebaseService;
import fa.training.backend.service.JwtService;
import fa.training.backend.service.VaccineTypeService;
import fa.training.backend.service.impl.JwtServiceImpl;
import fa.training.backend.util.Message;
import fa.training.backend.util.Status;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URL;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(VaccineTypeController.class)
@ContextConfiguration(classes = {VaccineTypeController.class, JwtServiceImpl.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class VaccineTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private FirebaseService firebaseService;

    @Autowired
    private Gson gson;

    @MockBean
    private VaccineTypeService vaccineTypeService;

    @Autowired
    private JwtService jwtService;

    @Value("${admin.username}")
    private String username;
    @Value("${admin.password}")
    private String password;


    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
    }
    void mockGetFileUrl() {
        try {
            when(firebaseService.getFileUrl(any(String.class))).thenReturn(new URL("https:google.com"));
        } catch (Exception e) {}

    }

    private VaccineTypeUpdateDto vaccineTypeUpdateDto = new VaccineTypeUpdateDto("id", "code","name", "description", Status.ACTIVE, null,"imageName");
    private void mockSave() {
        when(vaccineTypeService.add(any(NewVaccineTypeRequest.class))).thenReturn("newId");

    }

    private void mockUpdate() {
        when(vaccineTypeService.update(any(UpdateVaccineTypeRequest.class), anyString())).thenAnswer(
                answer -> {
                    UpdateVaccineTypeRequest request = answer.getArgument(0);
                    String id = answer.getArgument(1);
                    if ("-1".equals(id)) {
                        throw new NotFoundException(Message.MSG_128, id);
                    }
                    return true;
                }
        );
    }

    private void mockFindById(){
        when(vaccineTypeService.findById(any(String.class))).thenAnswer(
                answer-> {
                    String id = answer.getArgument(0);
                    if (id.equals("-1")){
                        throw new NotFoundException(Message.MSG_128, id);
                    }
                    return vaccineTypeUpdateDto;
                }
        );
    }

    private void mockUpdateVaccineType() {
        when(vaccineTypeService.update(any(UpdateVaccineTypeRequest.class), any(String.class)))
                .thenReturn(true);
    }


    private String getJwtToken() {
        Admin admin = new Admin(username, password);
        return "Bearer " + jwtService.generateToken(admin);
    }

    @Test
    void addSuccess() throws Exception {
        mockSave();
        String uri = "/api/v1/vaccine-types";
        NewVaccineTypeRequest request = new NewVaccineTypeRequest("code", "name", "description", "image");

        mockMvc.perform(
                    post(uri)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(Message.MSG_221.getCode()))
                .andExpect(jsonPath("$.description").value(Message.MSG_221.getMessage()))
                .andExpect(jsonPath("$.data").value("newId"));
    }

    @Test
    void addFail_ViolateConstraint() throws Exception {
        mockSave();
        String uri = "/api/v1/vaccine-types";
        NewVaccineTypeRequest request = new NewVaccineTypeRequest("", null, "description", "image");

        mockMvc.perform(
                        post(uri)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(gson.toJson(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("Vaccine type code is required"))
                .andExpect(jsonPath("$.name").value("Vaccine type name is required"));
    }

    @Test
    void updateVaccineTypeInactive() {
    }

    @Test
    void getVaccineTypeById() throws Exception {
        mockFindById();
        String id ="a";
        String uri = "/api/v1/vaccine-types/" + id;
        mockMvc.perform(
                get(uri)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(Message.MSG_212.getCode()))
                .andExpect(jsonPath("$.description").value(Message.MSG_212.getMessage()))
                .andExpect(jsonPath("$.data.id").value(vaccineTypeUpdateDto.getId()))
                .andExpect(jsonPath("$.data.code").value(vaccineTypeUpdateDto.getCode()))
                .andExpect(jsonPath("$.data.name").value(vaccineTypeUpdateDto.getName()))
                .andExpect(jsonPath("$.data.description").value(vaccineTypeUpdateDto.getDescription()))
                .andExpect(jsonPath("$.data.status").value(vaccineTypeUpdateDto.getStatus().name()))
                .andExpect(jsonPath("$.data.imageUrl").value(vaccineTypeUpdateDto.getImageUrl()))
                .andExpect(jsonPath("$.data.imageName").value(vaccineTypeUpdateDto.getImageName()));
    }

    @Test
    void getVaccineTypeById_NotFound() throws Exception {

        mockFindById();
        String id = "-1";
        String uri = "/api/v1/vaccine-types/" + id;

        mockMvc.perform(
                        get(uri)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(Message.MSG_128.getCode()))
                .andExpect(jsonPath("$.description").value(Message.MSG_128.getMessage()))
                .andExpect(jsonPath("$.data").value(id));
    }


    @Test
    void updateVaccineType_Success() throws Exception {

        mockUpdateVaccineType();
        mockFindById();

        String id = "1";
        String uri = "/api/v1/vaccine-types/" + id;

        UpdateVaccineTypeRequest request = new UpdateVaccineTypeRequest(
                "Updated Vaccine Code",
                "Updated Vaccine Name",
                "Updated Vaccine Description",
                "image.png",
                Status.INACTIVE
        );

        mockMvc.perform(
                        put(uri)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(gson.toJson(request))
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(Message.MSG_224.getCode()))
                .andExpect(jsonPath("$.description").value(Message.MSG_224.getMessage()))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void updateVaccineType_Fail_IdNotFound() throws Exception {

        mockUpdate();

        String id = "-1";
        String uri = "/api/v1/vaccine-types/" + id;

        UpdateVaccineTypeRequest request = new UpdateVaccineTypeRequest(
                "Updated Vaccine Code",
                "Updated Vaccine Name",
                "Updated Vaccine Description",
                "image.png",
                Status.INACTIVE
        );

        mockMvc.perform(
                        put(uri)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(gson.toJson(request))
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(Message.MSG_128.getCode()))
                .andExpect(jsonPath("$.description").value(Message.MSG_128.getMessage()))
                .andExpect(jsonPath("$.data").value(id));
    }

    @Test
    void searchVaccineTypes_Success() throws Exception {
        VaccineTypeListDto vaccineType1 = new VaccineTypeListDto("id1", "code1", "name1", "description1", Status.ACTIVE);
        VaccineTypeListDto vaccineType2 = new VaccineTypeListDto("id2", "code2", "name2", "description2", Status.ACTIVE);

        PageDto<VaccineTypeListDto> pageDto = new PageDto<>(1, 5, 1, 2, List.of(vaccineType1, vaccineType2));

        when(vaccineTypeService.searchVaccineTypes(anyString(), any(Pageable.class)))
                .thenReturn(pageDto);

        mockMvc.perform(
                        get("/api/v1/vaccine-types")
                                .param("page", "1")
                                .param("size", "5")
                                .param("query", "vaccine")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(Message.MSG_222.getCode()))
                .andExpect(jsonPath("$.data.data[0].id").value(vaccineType1.getId()))
                .andExpect(jsonPath("$.data.data[1].id").value(vaccineType2.getId()));
    }

    @Test
    void searchVaccineTypes_Fail_NoData() throws Exception {
        when(vaccineTypeService.searchVaccineTypes(anyString(), any(Pageable.class)))
                .thenReturn(new PageDto<>(1, 5, 1, 0, Collections.emptyList()));

        mockMvc.perform(
                        get("/api/v1/vaccine-types")
                                .param("page", "1")
                                .param("size", "5")
                                .param("query", "notfound")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(Message.MSG_129.getCode()))
                .andExpect(jsonPath("$.data").isEmpty());
    }

}
