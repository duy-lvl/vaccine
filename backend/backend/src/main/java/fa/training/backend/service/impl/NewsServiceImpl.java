package fa.training.backend.service.impl;

import fa.training.backend.dto.request.news.NewsRequest;
import fa.training.backend.dto.request.NewsRequestDto;
import fa.training.backend.dto.response.NewsDto;
import fa.training.backend.dto.response.PageDto;
import fa.training.backend.exception.common.NotFoundException;
import fa.training.backend.model.News;
import fa.training.backend.model.NewsType;
import fa.training.backend.repository.NewsRepository;
import fa.training.backend.repository.NewsTypeRepository;
import fa.training.backend.service.NewsService;
import fa.training.backend.util.Message;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class NewsServiceImpl implements NewsService {
    private final NewsRepository newsRepository;
    private final ModelMapper modelMapper;
    private final NewsTypeRepository newsTypeRepository;


    @Autowired
    public NewsServiceImpl(NewsRepository newsRepository, ModelMapper modelMapper, NewsTypeRepository newsTypeRepository) {
        this.newsRepository = newsRepository;
        this.modelMapper = modelMapper;
        this.newsTypeRepository = newsTypeRepository;
    }

    @Override
    public PageDto<NewsDto> getAll(int page, int size, String search) {

        Pageable pageable = PageRequest.of(page - 1, size);
        var newsPage = newsRepository.findByTitleContainsIgnoreCaseOrContentContainsIgnoreCase(search, search, pageable);
        var newDtoList = newsPage.getContent().stream()
                .map(news -> {
                    var dto = modelMapper.map(news, NewsDto.class);
                    dto.setNewsTypeId(news.getNewsType().getId());
                    return dto;
                }).toList();
        return new PageDto<>(page, size, newsPage.getTotalPages(), newsPage.getNumberOfElements(), newDtoList);

    }

    @Override
    public boolean create(NewsRequest newsRequest) {
        if (!newsTypeRepository.existsById(newsRequest.getNewsTypeId())) {
            throw new NotFoundException(Message.MSG_131, newsRequest.getNewsTypeId());
        }
        NewsType newsType = newsTypeRepository.findById(newsRequest.getNewsTypeId()).get();
        News news = modelMapper.map(newsRequest, News.class);
        news.setNewsType(newsType);
        news.setId(null);
        newsRepository.save(news);
        return true;
    }

    @Override
    public boolean update(NewsRequest newsRequest, String id) {
        News existingNews = newsRepository.findById(id).orElse(null);
        if (existingNews == null) {
            throw new NotFoundException(Message.MSG_141, id);
        }

        if (!newsTypeRepository.existsById(newsRequest.getNewsTypeId())) {
            throw new NotFoundException(Message.MSG_131, newsRequest.getNewsTypeId());
        }
        existingNews.setTitle(newsRequest.getTitle());
        existingNews.setContent(newsRequest.getContent());
        existingNews.setPreview(newsRequest.getPreview());
        existingNews.setNewsType(newsTypeRepository.findById(newsRequest.getNewsTypeId()).get());
        newsRepository.save(existingNews);
        return true;
    }

    @Override
    public void deleteByIds(String ids) {
        String[] idList = ids.split(",");
        List<News> newsList = newsRepository.findAllById(Arrays.asList(idList));
        for (News news : newsList) {
            news.setDeleted(true);
        }
        newsRepository.saveAll(newsList);
    }

    @Override
    public NewsRequestDto getById(String id) {
        Optional<News> existingNews = newsRepository.findById(id);
        if (existingNews.isEmpty()) {
            throw new NotFoundException(Message.MSG_141, id);
        }
        return modelMapper.map(existingNews.get(), NewsRequestDto.class);
    }
}
