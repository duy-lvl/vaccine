package fa.training.frontend.controller;

import fa.training.frontend.service.JwtToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class BaseControllerAdvice {
    @ModelAttribute("avatar")
    public String getAvatar(){
        return JwtToken.avatar;
    }
    @ModelAttribute("username")
    public String getUsername(){
        return JwtToken.name;
    }
}
