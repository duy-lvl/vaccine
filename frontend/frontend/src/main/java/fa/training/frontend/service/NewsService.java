package fa.training.frontend.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import fa.training.frontend.dto.request.news.NewsAddRequest;
import fa.training.frontend.dto.request.news.NewsRequestDto;
import fa.training.frontend.dto.request.news.NewsResponseDto;
import fa.training.frontend.dto.response.PageDto;
import fa.training.frontend.dto.response.Response;
import fa.training.frontend.service.endpoint.NewsEnpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NewsService {
    @Autowired
    private NewsEnpoint newsEnpoint;

    @Autowired
    private Gson gson;

    public PageDto<NewsResponseDto> getAll(int page, int size, String search) {
        HttpServiceBase<Void, Response> httpService = new HttpServiceBase<>();
        String urlWithParam = String.format(newsEnpoint.getAll(), page, size, search);
        Response response = httpService.getFromAPI(urlWithParam, Response.class);
        String json = gson.toJson(response.getData());

        return gson.fromJson(json, new TypeToken<PageDto<NewsResponseDto>>(){}.getType());

    }

    public NewsRequestDto getById(String id) {
        HttpServiceBase<Object, Response> httpService = new HttpServiceBase<>();
        String urlWithParam = newsEnpoint.getById(id);
        Response response = httpService.getFromAPI(urlWithParam, Response.class);
        if(response != null && response.getData() != null) {
            return gson.fromJson(gson.toJson(response.getData()), NewsRequestDto.class);
        }
        return null;
    }

    public String create(NewsAddRequest newsAddRequest) {
        HttpServiceBase<NewsAddRequest, Response> httpService = new HttpServiceBase<>();
        Response response = httpService.postToAPI(newsAddRequest, newsEnpoint.create(), Response.class);
        return response.getData().toString();
    }

    public void update(NewsRequestDto newsUpdateResponse, String id) {
        HttpServiceBase<NewsRequestDto, Response> httpService = new HttpServiceBase<>();
        Response response = httpService.putToAPI(newsUpdateResponse, newsEnpoint.update(id), Response.class);

    }

    public boolean delete(String id) {
        HttpServiceBase<NewsResponseDto, Response> httpService = new HttpServiceBase<>();
        Response response = httpService.deleteFromAPI(newsEnpoint.delete(id), Response.class);
        return response.isSuccessful();
    }

}
