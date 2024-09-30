package fa.training.frontend.service.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserEndpoint {
    @Autowired
    private Endpoint endpoint;

    private static final String LOGIN = "user.login";

    public String login() {
        return endpoint.getUrl(LOGIN);
    }
}
