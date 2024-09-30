package fa.training.frontend.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import fa.training.frontend.dto.request.news_type.NewsTypeRequest;
import fa.training.frontend.dto.request.news_type.NewsTypeDto;
import fa.training.frontend.dto.response.Response;
import fa.training.frontend.service.endpoint.NewsTypeEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NewsTypeService {
    private final NewsTypeEndpoint newsTypeEndpoint;
    private final Gson gson;

    @Autowired
    public NewsTypeService(NewsTypeEndpoint newsTypeEndpoint, Gson gson) {
        this.newsTypeEndpoint = newsTypeEndpoint;
        this.gson = gson;
    }
    // CRUD Operation: Delete
    public boolean deleteNewsType(String idList) {
        HttpServiceBase<Object, Response> httpServiceBase = new HttpServiceBase<>();
        Response response = httpServiceBase.deleteFromAPI(newsTypeEndpoint.delete(idList), Response.class);
        return Boolean.parseBoolean(response.getData().toString());
    }
    
    public List<NewsTypeDto> getAll() {
        HttpServiceBase<Void, Response> httpServiceBase = new HttpServiceBase<>();
        String urlWithParam = String.format(newsTypeEndpoint.all());
        Response response = httpServiceBase.getFromAPI(urlWithParam, Response.class);
        String json = gson.toJson(response.getData());
        return gson.fromJson(json, new TypeToken<List<NewsTypeDto>>() {
        }.getType());
    }

    public String create(NewsTypeRequest newsTypeRequest) {
        HttpServiceBase<NewsTypeRequest, Response> httpService = new HttpServiceBase<>();
        Response response = httpService.postToAPI(newsTypeRequest, newsTypeEndpoint.create(), Response.class);
        return response.getDescription();
    }

    public void update(NewsTypeRequest newsTypeUpdateResponse, String id) {
        HttpServiceBase<NewsTypeRequest, Response> httpService = new HttpServiceBase<>();
        httpService.putToAPI(newsTypeUpdateResponse, newsTypeEndpoint.update(id), Response.class);
    }

    public List<NewsTypeDto> getAllNewsType() {
        HttpServiceBase<Void, Response> httpServiceBase = new HttpServiceBase<>();
        Response response = httpServiceBase.getFromAPI(newsTypeEndpoint.all(), Response.class);
        String json = gson.toJson(response.getData());
        return gson.fromJson(json, new TypeToken<List<NewsTypeDto>>() {
        }.getType());
    }

    public NewsTypeDto getById(String id) {
        HttpServiceBase<Object, Response> httpServiceBase = new HttpServiceBase<>();
        String urlWithParam = newsTypeEndpoint.getById(id);
        Response response = httpServiceBase.getFromAPI(urlWithParam, Response.class);
        if (response != null && response.getData() != null) {
            return gson.fromJson(gson.toJson(response.getData()), NewsTypeDto.class);
        }
        return null;
    }
}
