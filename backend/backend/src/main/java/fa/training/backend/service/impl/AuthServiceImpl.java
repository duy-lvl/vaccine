package fa.training.backend.service.impl;

import fa.training.backend.dto.request.LoginRequest;
import fa.training.backend.dto.response.LoginDto;
import fa.training.backend.model.Admin;
import fa.training.backend.model.Employee;
import fa.training.backend.service.AuthService;
import fa.training.backend.service.FirebaseService;
import fa.training.backend.service.JwtService;
import fa.training.backend.service.UserService;
import fa.training.backend.util.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.net.URL;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    @Value("${admin.username}")
    private String adminUsername;
    @Value("${admin.password}")
    private String adminPassword;
    @Value("${admin.avatar}")
    private String adminAvatar;
    @Autowired
    private FirebaseService firebaseService;

    @Autowired
    public AuthServiceImpl(UserService userService, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public LoginDto login(LoginRequest request) {
        UserDetails user = userService.userDetailsService().loadUserByUsername(request.getUsername());
        if ((user instanceof Admin || user instanceof Employee)
                && passwordEncoder.matches(request.getPassword(), user.getPassword()))
        {
            String name = "";
            URL avatar;
            try {
                if (user instanceof Employee employee) {
                    name = employee.getName();

                    avatar = firebaseService.getFileUrl(employee.getImage());
                }
                else {
                    name = "Admin";
                    avatar = firebaseService.getFileUrl(adminAvatar);
                }
            } catch (Exception e) {
                avatar = null;
            }

            return new LoginDto(
                    user.getUsername(),
                    user.getAuthorities().stream().map(authority -> Role.valueOf(authority.getAuthority())).toList(),
                    jwtService.generateToken(user),
                    name,
                    avatar
            );
        }
        return null;
    }
}
