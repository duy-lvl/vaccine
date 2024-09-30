package fa.training.backend.service;

import fa.training.backend.dto.request.news_type.NewsTypeRequest;
import fa.training.backend.dto.response.NewsTypeDto;

import java.util.List;

public interface NewsTypeService {
    List<NewsTypeDto> getAll();
    boolean create(NewsTypeRequest newsTypeRequest);
    boolean update(NewsTypeRequest newsTypeRequest, String id);
    void deleteByIds(String ids);

    NewsTypeDto getById(String id);

    boolean deleteNewsTypeByIds(String ids);
}
