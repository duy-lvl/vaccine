package fa.training.frontend.service;

import com.google.gson.Gson;
import fa.training.frontend.dto.request.LoginRequest;
import fa.training.frontend.dto.response.LoginDto;
import fa.training.frontend.dto.response.Response;
import fa.training.frontend.service.endpoint.UserEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserService {
    @Autowired
    private Gson gson;
    @Autowired
    private UserEndpoint userEndpoint;

    public LoginDto login(LoginRequest request) {
        HttpServiceBase<LoginRequest, Response> httpServiceBase = new HttpServiceBase<>();
        var endpoint = userEndpoint.login();
        Response response = httpServiceBase.postToAPI(request, userEndpoint.login(), Response.class);
        if (!response.isSuccessful()) {
            return null;
        }
        var json = gson.toJson(response.getData());
        LoginDto loginDto = gson.fromJson(json, LoginDto.class);

        if (loginDto != null) {
            JwtToken.bearerToken = "Bearer " + loginDto.getJwt();
            JwtToken.roles = loginDto.getRoles();
            JwtToken.name = loginDto.getName();
            JwtToken.avatar = loginDto.getAvatar();
        }

        return loginDto;
    }
}
