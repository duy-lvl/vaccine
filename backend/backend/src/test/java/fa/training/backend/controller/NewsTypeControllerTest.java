package fa.training.backend.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fa.training.backend.dto.request.news_type.NewsTypeRequest;
import fa.training.backend.dto.response.NewsTypeDto;
import fa.training.backend.exception.common.NotFoundException;
import fa.training.backend.exception.handler.GlobalExceptionHandler;
import fa.training.backend.service.NewsTypeService;
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
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NewsTypeController.class)
@ContextConfiguration(classes = {NewsTypeController.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class NewsTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Gson gson;

    @MockBean
    private NewsTypeService newsTypeService;

    private String uri = "/api/v1/news-types";

    @BeforeEach
    void setUp() {
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter()) // Register LocalDateAdapter
                .create();
    }

    private void mockSave() {
        when(newsTypeService.create(any(NewsTypeRequest.class))).thenReturn(true);
    }


    private void mockUpdate() {
        when(newsTypeService.update(any(NewsTypeRequest.class), anyString())).thenAnswer(
                answer -> {
                    NewsTypeRequest request = answer.getArgument(0);
                    String id = answer.getArgument(1);
                    if ("-1".equals(id)) {
                        throw new NotFoundException(Message.MSG_131, id);
                    }
                    return true;
                }
        );
    }

    @Test
    void addSuccess() throws Exception {
        mockSave();
        NewsTypeRequest request = new NewsTypeRequest("describe", "name1");

        mockMvc.perform(
                        post(uri)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(gson.toJson(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(Message.MSG_233.getCode()))
                .andExpect(jsonPath("$.description").value(Message.MSG_233.getMessage()))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void addFail_ViolateConstraint() throws Exception {
        mockSave();
        NewsTypeRequest request = new NewsTypeRequest("", "");

        mockMvc.perform(
                        post(uri)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(gson.toJson(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Name's news type must not be blank!"));
    }

    @Test
    void update_Success() throws Exception {
        mockUpdate();

        String id = "1";
        String uri = "/api/v1/news-types/" + id;

        NewsTypeRequest request = new NewsTypeRequest("type2", "name2");

        mockMvc.perform(
                        put(uri)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(gson.toJson(request))
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(Message.MSG_234.getCode()))
                .andExpect(jsonPath("$.description").value(Message.MSG_234.getMessage()))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void update_Fail_IdNotFound() throws Exception {
        mockUpdate();
        String id = "-1";
        String uri = "/api/v1/news-types/" + id;

        NewsTypeRequest request = new NewsTypeRequest("describe", "name2");

        mockMvc.perform(
                        put(uri)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(gson.toJson(request))
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(Message.MSG_131.getCode()))
                .andExpect(jsonPath("$.description").value(Message.MSG_131.getMessage()));
    }

    @Test
    void getAll_Success() throws Exception {
        NewsTypeDto newsTypeDto1 = new NewsTypeDto("1", "name1", "description1");
        NewsTypeDto newsTypeDto2 = new NewsTypeDto("2", "name2", "description2");
        List<NewsTypeDto> list = List.of(newsTypeDto1, newsTypeDto2);
        when(newsTypeService.getAll())
                .thenReturn(list);

        mockMvc.perform(
                        get("/api/v1/news-types")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(Message.MSG_231.getCode())) // Kiểm tra mã code trả về
                .andExpect(jsonPath("$.data[0].id").value(newsTypeDto1.getId())) // Kiểm tra phần tử đầu tiên
                .andExpect(jsonPath("$.data[1].id").value(newsTypeDto2.getId())); // Kiểm tra phần tử thứ hai
    }

    @Test
    void getAll_Fail_NoData() throws Exception {
        when(newsTypeService.getAll())
                .thenReturn(Collections.emptyList());

        mockMvc.perform(
                        get("/api/v1/news-types")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())  // Kiểm tra trạng thái trả về là OK (200)
                .andExpect(jsonPath("$.code").value(Message.MSG_231.getCode())) // Kiểm tra mã code trả về khi không tìm thấy
                .andExpect(jsonPath("$.data").isEmpty()); // Kiểm tra dữ liệu rỗng
    }
}