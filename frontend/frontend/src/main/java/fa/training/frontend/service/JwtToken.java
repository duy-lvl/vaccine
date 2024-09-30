package fa.training.frontend.service;

import fa.training.frontend.util.Role;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

public class JwtToken {
    public static final String HEADER = "Authorization";
    public static String bearerToken = "";
    public static List<Role> roles = new ArrayList<>();
    public static String name = "";
    public static String avatar = "";
}
