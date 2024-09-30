package fa.training.frontend.interceptor;

import fa.training.frontend.service.JwtToken;
import fa.training.frontend.util.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.ArrayList;
import java.util.List;

@Component
public class SecurityInterceptor implements HandlerInterceptor {
    private List<String> employeeUrls = List.of(
            "/dashboard",
            "/customers",
            "/injection-results",
            "/reports/injection-results",
            "/reports/vaccines"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();

        if (JwtToken.bearerToken.isEmpty()) {

            response.sendRedirect("/login");
            return false;
        }


        if (JwtToken.roles.contains(Role.ADMIN)) {
            return true;
        }

        if (JwtToken.roles.contains(Role.EMPLOYEE) && employeeUrls.stream().anyMatch(url -> uri.startsWith(url))) {
            return true;
        }

        // If access is denied, redirect to an error page
        response.sendRedirect("/dashboard?error=true&message=you are not authorized");
        return false;
    }

}
