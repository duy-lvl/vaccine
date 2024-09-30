package fa.training.frontend.service.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:endpoints.properties")
public class Endpoint {
    @Autowired
    private Environment environment;

    private static final String HOST = "backend.host";

    private String getHost(){
        return environment.getProperty(HOST);
    }

    public String getUrl(String endpoint){
        return getHost() + environment.getProperty(endpoint);
    }
}
