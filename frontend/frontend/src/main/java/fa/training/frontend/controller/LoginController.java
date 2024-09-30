package fa.training.frontend.controller;

import fa.training.frontend.dto.request.LoginRequest;
import fa.training.frontend.dto.response.LoginDto;
import fa.training.frontend.service.JwtToken;
import fa.training.frontend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller

public class LoginController {

    private final UserService userService;

    @Autowired
    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String getLogin(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }

    @PostMapping("/login")
    public String postLogin(@ModelAttribute LoginRequest loginRequest) {
        LoginDto loginDto = userService.login(loginRequest);
        if (loginDto == null) {
            return "redirect:/login?error";
        }
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(
            @RequestParam(required = false, defaultValue = "false") boolean error,
            @RequestParam(required = false) String message,
            Model model
    ) {
        model.addAttribute("error", error);
        model.addAttribute("message", message);
        return "dashboard";
    }

    @GetMapping("/logout")
    public String logout() {
        JwtToken.bearerToken = "";
        return "redirect:/login";
    }


}
