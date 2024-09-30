package fa.training.backend.service;

import fa.training.backend.dto.request.LoginRequest;
import fa.training.backend.dto.response.LoginDto;

public interface AuthService {
    LoginDto login(LoginRequest request);
}
