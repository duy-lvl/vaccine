package fa.training.backend.service;

import fa.training.backend.dto.request.news.NewsRequest;
import fa.training.backend.dto.request.NewsRequestDto;
import fa.training.backend.dto.response.NewsDto;
import fa.training.backend.dto.response.PageDto;

public interface NewsService {
    PageDto<NewsDto> getAll(int page, int size, String search);
    boolean create(NewsRequest newsRequest);
    boolean update(NewsRequest newsRequest, String id);
    void deleteByIds(String ids);
    NewsRequestDto getById(String id);
}
