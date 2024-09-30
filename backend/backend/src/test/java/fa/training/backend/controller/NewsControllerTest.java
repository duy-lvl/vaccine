package fa.training.backend.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fa.training.backend.dto.request.news.NewsRequest;
import fa.training.backend.dto.response.NewsDto;
import fa.training.backend.dto.response.PageDto;
import fa.training.backend.exception.common.NotFoundException;
import fa.training.backend.exception.handler.GlobalExceptionHandler;
import fa.training.backend.service.NewsService;
import fa.training.backend.util.LocalDateAdapter;
import fa.training.backend.util.Message;
import org.junit.jupiter.api.AfterEach;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NewsController.class)
@ContextConfiguration(classes = {NewsController.class, GlobalExceptionHandler.class})
@AutoConfigureMockMvc(addFilters = false)
class NewsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Gson gson;

    @MockBean
    private NewsService newsService;

    private String uri = "/api/v1/news";

    @BeforeEach
    void setUp() {
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter()) // Register LocalDateAdapter
                .create();
    }

    @AfterEach
    void tearDown() {
    }

    private void mockSave() {
        when(newsService.create(any(NewsRequest.class))).thenReturn(true);
    }

    private void mockUpdateById() {
        when(newsService.update(any(NewsRequest.class), anyString())).thenAnswer(
                answer -> {
                    NewsRequest request = answer.getArgument(0);
                    String id = answer.getArgument(1);
                    if ("-1".equals(id)) {
                        throw new NotFoundException(Message.MSG_141, id);
                    }
                    return true;
                }
        );
    }

    @Test
    void addSuccess() throws Exception {
        mockSave();
        NewsRequest request = new NewsRequest("content1", "preview1", "title1","newsType1");

        mockMvc.perform(
                        post(uri)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(gson.toJson(request))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(Message.MSG_243.getCode()))
                .andExpect(jsonPath("$.description").value(Message.MSG_243.getMessage()))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void addFail_ViolateConstraint() throws Exception {
        mockSave();
        NewsRequest request = new NewsRequest("", "", "","");

        mockMvc.perform(
                        post(uri)
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(gson.toJson(request))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.content").value("Content is required"))
                .andExpect(jsonPath("$.preview").value("Preview is required"))
                .andExpect(jsonPath("$.title").value("Title is required"))
                .andExpect(jsonPath("$.newsTypeId").value("News type is required"));
    }

    @Test
    void update_Success() throws Exception {
        mockUpdateById();

        String id = "1";
        String uri = "/api/v1/news/" + id;

        NewsRequest request = new NewsRequest("content2", "preview2", "title2","newsType2");

        mockMvc.perform(
                        put(uri)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(gson.toJson(request))
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(Message.MSG_244.getCode()))
                .andExpect(jsonPath("$.description").value(Message.MSG_244.getMessage()))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void update_Fail_IdNotFound() throws Exception {
        mockUpdateById();
        String id = "-1";
        String uri = "/api/v1/news/" + id;

        NewsRequest request = new NewsRequest("content2", "preview2", "title2","newsType2");


        mockMvc.perform(
                        put(uri)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(gson.toJson(request))
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(Message.MSG_141.getCode()))
                .andExpect(jsonPath("$.description").value(Message.MSG_141.getMessage()));
    }

    @Test
    void getAll_Success() throws Exception {
        NewsDto newsDto1 = new NewsDto("1", "content1", "preview1", "title1", "newsType1");
        NewsDto newsDto2 = new NewsDto("2", "content2", "preview2", "title2", "newsType2");

        PageDto<NewsDto> pageDto = new PageDto<>(1, 5, 1, 2, List.of(newsDto1, newsDto2));

        when(newsService.getAll(anyInt(), anyInt(), anyString()))
                .thenReturn(pageDto);

        mockMvc.perform(
                        get("/api/v1/news")
                                .param("page", "1")
                                .param("size", "5")
                                .param("query", "name")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(Message.MSG_241.getCode())) // Kiểm tra mã code trả về
                .andExpect(jsonPath("$.data.data[0].id").value(newsDto1.getId())) // Kiểm tra phần tử đầu tiên
                .andExpect(jsonPath("$.data.data[1].id").value(newsDto2.getId())); // Kiểm tra phần tử thứ hai
    }

    @Test
    void getAll_Fail_NoData() throws Exception {
        when(newsService.getAll(anyInt(), anyInt(),anyString()))
                .thenReturn(new PageDto<>(1, 5, 1, 0, Collections.emptyList()));

        mockMvc.perform(
                        get("/api/v1/news")
                                .param("page", "1")
                                .param("size", "5")
                                .param("query", "notfound")
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())  // Kiểm tra trạng thái trả về là OK (200)
                .andExpect(jsonPath("$.code").value(Message.MSG_241.getCode())) // Kiểm tra mã code trả về khi không tìm thấy
                .andExpect(jsonPath("$.data.data").isEmpty()); // Kiểm tra dữ liệu rỗng
    }
}