package fa.training.frontend.service.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NewsTypeEndpoint {
    @Autowired
    private Endpoint endpoint;

    private static final String NEWS_TYPES_ALL = "news-type.get-all";
    private static final String NEWS_TYPES_CREATE = "news-type.create";
    private static final String NEWS_TYPES_UPDATE = "news-type.update";
    private static final String NEWS_TYPES_GET_BY_ID = "news-type.get-by-id";
    private static final String NEWS_TYPES_DELETE_BY_IDS = "news-type.delete-by-ids";
    public String all(){
        return endpoint.getUrl(NEWS_TYPES_ALL);
    }

    public String create(){
        return endpoint.getUrl(NEWS_TYPES_CREATE);
    }

    public String update(String id){
        return String.format(endpoint.getUrl(NEWS_TYPES_UPDATE), id);
    }
    public String getById(String id){
        return String.format(endpoint.getUrl(NEWS_TYPES_GET_BY_ID), id);
    }

    public String delete(String ids){
        return String.format(endpoint.getUrl(NEWS_TYPES_DELETE_BY_IDS), ids);
    }
}
