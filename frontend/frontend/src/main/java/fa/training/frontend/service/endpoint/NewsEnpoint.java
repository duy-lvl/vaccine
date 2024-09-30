package fa.training.frontend.service.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NewsEnpoint {
    @Autowired
    private Endpoint endpoint;

    private static final String NEWS_GET_ALL = "news.get-all";
    private static final String NEWS_GET_BY_ID = "news.get-by-id";
    private static final String NEWS_UPDATE = "news.update";
    private static final String NEWS_DELETE = "news.delete";
    private static final String NEWS_CREATE = "news.create";

    public String getAll(){
        return endpoint.getUrl(NEWS_GET_ALL);
    }

    public String getById(String id){
        return String.format(endpoint.getUrl(NEWS_GET_BY_ID), id);
    }

    public String update(String id){
        return String.format(endpoint.getUrl(NEWS_UPDATE), id);
    }

    public String delete(String id){
        return String.format(endpoint.getUrl(NEWS_DELETE), id);
    }

    public String create(){
        return endpoint.getUrl(NEWS_CREATE);
    }
}
