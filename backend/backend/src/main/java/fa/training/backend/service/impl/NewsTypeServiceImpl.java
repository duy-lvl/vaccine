package fa.training.backend.service.impl;

import fa.training.backend.dto.request.news_type.NewsTypeRequest;
import fa.training.backend.dto.response.NewsTypeDto;
import fa.training.backend.exception.common.NotFoundException;
import fa.training.backend.model.News;
import fa.training.backend.model.NewsType;
import fa.training.backend.repository.NewsRepository;
import fa.training.backend.repository.NewsTypeRepository;
import fa.training.backend.service.NewsTypeService;
import fa.training.backend.util.Message;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class NewsTypeServiceImpl implements NewsTypeService {
    private final NewsTypeRepository newsTypeRepository;
    private final ModelMapper modelMapper;
    private final NewsRepository newsRepository;

    @Autowired
    public NewsTypeServiceImpl(NewsTypeRepository newsTypeRepository, NewsRepository newsRepository, ModelMapper modelMapper) {
        this.newsTypeRepository = newsTypeRepository;
        this.newsRepository = newsRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<NewsTypeDto> getAll() {
        return newsTypeRepository.findAll().stream().map(newsTypes -> modelMapper.map(newsTypes, NewsTypeDto.class)).toList();
    }

    @Override
    public boolean create(NewsTypeRequest newsTypeRequest) {
        NewsType newsType = modelMapper.map(newsTypeRequest, NewsType.class);
        newsTypeRepository.save(newsType);
        return true;
    }

    @Override
    public boolean update(NewsTypeRequest newsTypeRequest, String id) {
        NewsType existingNewsType = newsTypeRepository.findById(id).orElse(null);
        if (existingNewsType == null) {
            throw new NotFoundException(Message.MSG_131, id);
        }
        existingNewsType.setName(newsTypeRequest.getName());
        existingNewsType.setDescription(newsTypeRequest.getDescription());
        newsTypeRepository.save(existingNewsType);
        return true;
    }

    @Override
    public void deleteByIds(String ids) {
        String[] idList = ids.split(",");
        List<NewsType> newsTypes = newsTypeRepository.findAllById(Arrays.asList(idList));
        for (NewsType newsType : newsTypes) {
            newsType.setDeleted(true);
        }
        newsTypeRepository.saveAll(newsTypes);
        List<News> newsList = newsRepository.findByNewsTypeIdIn(Arrays.asList(idList));
        for (News news : newsList) {
            news.setDeleted(true);
        }
        newsRepository.saveAll(newsList);
    }

    @Override
    public NewsTypeDto getById(String id) {
        NewsType newsType = newsTypeRepository.findById(id).orElse(null);
        if (newsType == null) {
            throw new NotFoundException(Message.MSG_131, id);
        }
        return modelMapper.map(newsType, NewsTypeDto.class);
    }

    @Override
    public boolean deleteNewsTypeByIds(String ids) {
        String[] idList = ids.split(",");
        List<NewsType> newsTypes = newsTypeRepository.findAllById(Arrays.asList(idList));
        for (NewsType newsType : newsTypes) {
            newsType.setDeleted(true);
        }
        newsTypeRepository.saveAll(newsTypes);
        List<News> newsList = newsRepository.findByNewsTypeIdIn(Arrays.asList(idList));
        for (News news : newsList) {
            news.setDeleted(true);
        }
        newsRepository.saveAll(newsList);
        return false;
    }

}
