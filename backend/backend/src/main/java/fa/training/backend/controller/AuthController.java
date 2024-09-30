package fa.training.backend.controller;

import fa.training.backend.dto.request.LoginRequest;
import fa.training.backend.dto.response.LoginDto;
import fa.training.backend.dto.response.Response;
import fa.training.backend.service.AuthService;
import fa.training.backend.util.Message;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Response> login(@Valid @RequestBody LoginRequest request) {
        LoginDto loginDto = authService.login(request);
        if (loginDto == null) {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(
                            Response.builder()
                                    .code(Message.MSG_101.getCode())
                                    .description(Message.MSG_101.getMessage())
                                    .data(loginDto).build()
                    );
        }
        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        Response.builder()
                                .code(Message.MSG_201.getCode())
                                .description(Message.MSG_201.getMessage())
                                .data(loginDto).build()
                );
    }
}
