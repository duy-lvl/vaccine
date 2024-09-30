package fa.training.frontend.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fa.training.frontend.interceptor.SecurityInterceptor;
import fa.training.frontend.util.LocalDateAdapter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDate;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public Gson gson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())  // Register custom LocalDate adapter
                .create();
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Autowired
    private SecurityInterceptor securityInterceptor;

    private final String[] excludeResources = {
            "/css/**",
            "/js/**",
            "/login",
            "/photos/**",
            "/error2",
            "/error",
            "/logout"
    };

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(securityInterceptor)
                .excludePathPatterns(excludeResources);
    }
}
