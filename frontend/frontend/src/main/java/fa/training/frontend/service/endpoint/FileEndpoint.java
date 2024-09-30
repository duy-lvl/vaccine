package fa.training.frontend.service.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileEndpoint {
    private final Endpoint endpoint;

    @Autowired
    public FileEndpoint(Endpoint endpoint) {
        this.endpoint = endpoint;
    }

    private static final String FILE_UPLOAD= "files.upload";
    private static final String FILE_GET_BY_ID= "files.getById";
    public String fileUpload(){return endpoint.getUrl(FILE_UPLOAD);}
    public String getFileGetById(String id){
        return String.format(endpoint.getUrl(FILE_GET_BY_ID),id);

    }

}
