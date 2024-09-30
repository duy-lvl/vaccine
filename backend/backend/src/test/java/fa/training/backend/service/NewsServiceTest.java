package fa.training.backend.service;

import fa.training.backend.dto.request.news.NewsRequest;
import fa.training.backend.exception.common.NotFoundException;
import fa.training.backend.model.News;
import fa.training.backend.model.NewsType;
import fa.training.backend.repository.NewsRepository;
import fa.training.backend.repository.NewsTypeRepository;
import fa.training.backend.service.impl.NewsServiceImpl;
import fa.training.backend.util.Message;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class NewsServiceTest {

    private static List<NewsType> newsTypes;
    private static List<News> newsList;
    @Mock
    private NewsTypeRepository newsTypeRepository;
    @Mock
    private NewsRepository newsRepository;
    private static Validator validator;
    private static ValidatorFactory validatorFactory;
    private NewsService newsService;
    private static List<String> errorMessages;
    @Autowired
    private ModelMapper mapper;

    @BeforeAll
    static void setUpAll() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();

        errorMessages = List.of(
                "Content is required",
                "Preview is required",
                "Title is required",
                "News type is required",
                "Invalid news type",
                "Cannot update as news does not exist",
                "News type does not exist",
                "News does not exist"
        );
    }

    void mockGetNewsTypeById() {
        when(newsTypeRepository.findById(any(String.class))).thenAnswer(
                answer -> {
                    String id = answer.getArgument(0);
                    return newsTypes.stream().filter(nt -> nt.getId().equals(id)).findFirst();
                }
        );
    }

    void mockExistNewsTypeById() {
        when(newsTypeRepository.existsById(any(String.class))).thenAnswer(
                answer -> {
                    String id = answer.getArgument(0);
                    return newsTypes.stream().anyMatch(nt -> nt.getId().equals(id));
                }
        );
    }

    void mockExistNewsById() {
        when(newsRepository.existsById(any(String.class))).thenAnswer(
                answer -> {
                    String id = answer.getArgument(0);
                    return newsList.stream().anyMatch(n -> n.getId().equals(id));
                }
        );
    }

    void mockGetNewsById() {
        when(newsRepository.findById(any(String.class))).thenAnswer(
                answer -> {
                    String id = answer.getArgument(0);
                    return newsList.stream().filter(n -> n.getId().equals(id)).findFirst();
                }
        );
    }

    void mockFindAllNewsType() {
        when(newsTypeRepository.findAll()).thenReturn(newsTypes);
    }

    void mockSaveNews() {
        when(newsRepository.save(any(News.class))).then(
                answer -> {
                    News news = answer.getArgument(0);
                    if (news.getId() == null) {
                        news.setId(String.valueOf(newsList.size()));
                        newsList.add(news);
                    } else {
                        int index = Integer.parseInt(news.getId()) - 1;
                        newsList.set(index, news);
                    }
                    return news;
                }
        );
    }

    void mockDeleteNews() {
        when(newsRepository.findAllById(any(List.class))).thenAnswer(
                answer -> {
                    List<String> ids = answer.getArgument(0);
                    return newsList.stream().filter(n->ids.contains(n.getId())).toList();
                }
        );
        when(newsRepository.saveAll(any(List.class))).thenAnswer(
                answer -> {
                    List<News> data = answer.getArgument(0);
                    data.stream().forEach(
                            n -> {
                                newsList.set(Integer.parseInt(n.getId())-1, n);
                            }
                    );
                    return data;
                }
        );
    }

    @AfterAll
    static void tearDownAll() {
        validatorFactory.close();
    }

    @BeforeEach
    void setUp() {
        newsService = new NewsServiceImpl(newsRepository, mapper, newsTypeRepository);
        newsTypes = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            String id = String.valueOf(i);
            String description = "Desc" + i;
            String name = "Name" + i;

            NewsType newsType = new NewsType(id, description, name);
            newsTypes.add(newsType);
        }

        newsList = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            String id = String.valueOf(i);
            String content = "Content for news " + i;
            String preview = "Preview for news " + i;
            String title = "Title " + i;

            Random random = new Random();

            NewsType randomNewsType = newsTypes.get(random.nextInt(newsTypes.size()));

            News news = new News(id, content, preview, title, randomNewsType);
            newsList.add(news);
        }
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void createSuccess() {
        mockSaveNews();
        mockExistNewsTypeById();
        mockGetNewsTypeById();
        int listSize = newsList.size();
        NewsRequest request = new NewsRequest(
                "new content",
                "new review",
                "new title",
                newsTypes.get(1).getId()
        );

        newsService.create(request);
        assertEquals(listSize + 1, newsList.size());

        News addedNews = newsList.get(listSize);
        assertEquals(request.getContent(), addedNews.getContent());
        assertEquals(request.getPreview(), addedNews.getPreview());
        assertEquals(request.getTitle(), addedNews.getTitle());
        assertEquals(request.getNewsTypeId(), addedNews.getNewsType().getId());
    }

    @Test
    void createFail_InvalidNewsTypeId() {
        NewsRequest request = new NewsRequest(
                "new content",
                "new review",
                "new title",
                "-1"
        );
        NotFoundException exception = assertThrows(NotFoundException.class, () -> newsService.create(request));
        assertTrue(errorMessages.contains(exception.getMessage()));
    }

    @Test
    void updateSuccess() {
        mockSaveNews();
        mockExistNewsTypeById();
        mockGetNewsTypeById();
        mockGetNewsById();
        int listSize = newsList.size();
        NewsRequest request = new NewsRequest(
                "new content",
                "new review",
                "new title",
                newsTypes.get(1).getId()
        );

        newsService.update(request, "1");
        assertEquals(listSize, newsList.size());

        News addedNews = newsList.get(0);
        assertEquals(request.getContent(), addedNews.getContent());
        assertEquals(request.getPreview(), addedNews.getPreview());
        assertEquals(request.getTitle(), addedNews.getTitle());
        assertEquals(request.getNewsTypeId(), addedNews.getNewsType().getId());
    }

    @Test
    void updateFail_IdNotFound() {
        mockGetNewsById();
        NewsRequest request = new NewsRequest(
                "new content",
                "new review",
                "new title",
                newsTypes.get(1).getId()
        );

        NotFoundException exception = assertThrows(NotFoundException.class, () -> newsService.update(request, "-1"));
        assertTrue(errorMessages.contains(exception.getMessage()));
    }

    @Test
    void updateFail_InvalidNewsTypeId() {
        mockGetNewsById();

        NewsRequest request = new NewsRequest(
                "new content",
                "new review",
                "new title",
                newsTypes.get(1).getId()
        );

        NotFoundException exception = assertThrows(NotFoundException.class, () -> newsService.update(request, "-1"));
        assertTrue(errorMessages.contains(exception.getMessage()));
    }

    @Test
    void deleteSuccess() {
        String id = "10";
        mockDeleteNews();
        //mockExistNewsById();
        int listSize = newsList.size();
        newsService.deleteByIds(id);
        assertEquals(listSize, newsList.size());
        assertTrue(newsList.stream().anyMatch(n -> n.getId().equals(id)));
    }



    @Test
    void testRequestViolateConstraint() {
        NewsRequest request = new NewsRequest(
                "  ",
                null,
                " ",
                null
        );


        Set<ConstraintViolation<NewsRequest>> violations = validator.validate(request);
        assertEquals(6, violations.size());
        for (var violation : violations) {
            assertTrue(errorMessages.contains(violation.getMessage()));
        }

    }
}