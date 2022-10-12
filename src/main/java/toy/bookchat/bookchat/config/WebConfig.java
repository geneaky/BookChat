package toy.bookchat.bookchat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8080/chat/rooms", "http://localhost:8080/chat/room/*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH");
    }
}
