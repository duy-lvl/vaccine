package fa.training.backend.service;

import fa.training.backend.dto.request.news_type.NewsTypeRequest;
import fa.training.backend.dto.response.NewsTypeDto;
import fa.training.backend.model.NewsType;
import fa.training.backend.repository.NewsRepository;
import fa.training.backend.repository.NewsTypeRepository;
import fa.training.backend.service.impl.NewsTypeServiceImpl;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
class NewsTypeServiceTest {

    @Mock
    private NewsTypeRepository newsTypeRepository;
    @Mock
    private NewsRepository newsRepository;
    @Autowired
    private ModelMapper mapper;

    private Validator validator;

    private NewsTypeService newsTypeService;

    private static List<String> errorMessages;
    private static List<NewsType> newsTypes;

    @BeforeAll
    static void setUpAll() {
        newsTypes = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            String id = String.valueOf(i);
            String description = "Desc" + i;
            String name = "Name" + i;

            NewsType newsType = new NewsType(id, description, name);
            newsTypes.add(newsType);
        }
        errorMessages = List.of("Length of description's news type must not exceed 10 characters!",
                "Name's news type must not be null!",
                "Name's news type must not be blank!",
                "Length of name's news type must not exceed 50 characters!"
        );
    }

    @BeforeEach
    void setUp() {
        this.newsTypeService = new NewsTypeServiceImpl(newsTypeRepository, newsRepository, mapper);
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            this.validator = factory.getValidator();
        }
    }

    @AfterEach
    void tearDown() {
    }

    private void mockSave() {
        when(newsTypeRepository.save(any(NewsType.class))).then(
                answer -> {
                    NewsType nt = answer.getArgument(0);
                    if (nt.getId() == null) {
                        nt.setId(String.valueOf(newsTypes.size()));
                        newsTypes.add(nt);
                    } else {
                        int index = Integer.parseInt(nt.getId()) - 1;
                        newsTypes.set(index, nt);
                    }

                    return nt;
                }
        );
    }

    private void mockFindAll() {
        when(newsTypeRepository.findAll()).thenReturn(newsTypes);
    }

    private void mockFindById() {
        when(newsTypeRepository.findById(any(String.class))).thenAnswer(
                answer -> {
                    String id = answer.getArgument(0);
                    return newsTypes.stream().filter(nt -> nt.getId().equals(id)).findFirst();
                }
        );
    }

    @Test
    void getAllSuccess() {
        mockFindAll();
        var actual = newsTypeService.getAll();
        var expected = newsTypes.stream().map(nt -> mapper.map(nt, NewsTypeDto.class)).toList();
        assertEquals(expected, actual);
    }

    @Test
    void createSuccess() {
        mockSave();
        int listSize = newsTypes.size();
        NewsTypeRequest request = new NewsTypeRequest("name", "desc");
        newsTypeService.create(request);

        assertEquals(listSize + 1, newsTypes.size());
        NewsType actual = newsTypes.get(listSize);
        assertEquals(request.getName(), actual.getName());
        assertEquals(request.getDescription(), actual.getDescription());

        newsTypes.remove(listSize);
    }

    @Test
    void testRequestViolateConstraint() {
        NewsTypeRequest request = new NewsTypeRequest("desc", " ");
        Set<ConstraintViolation<NewsTypeRequest>> violations = this.validator.validate(request);
        assertEquals(1, violations.size());
        for (var violation : violations) {
            assertTrue(errorMessages.contains(violation.getMessage()));
        }
    }

    @Test
    void updateSuccess() {
        mockSave();
        mockFindById();
        int listSize = newsTypes.size();
        NewsTypeRequest request = new NewsTypeRequest("name", "desc");
        newsTypeService.update(request, "1");

        assertEquals(listSize, newsTypes.size());
        NewsType actual = newsTypes.get(0);
        assertEquals(request.getName(), actual.getName());
        assertEquals(request.getDescription(), actual.getDescription());
    }
}