package za.ac.cput.carrental.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor).addPathPatterns("/**");
    }

    // Car photos the admin uploads are saved in the "uploads/cars" folder and served from /uploads/cars/...
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path dir = Path.of("uploads", "cars").toAbsolutePath();
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            throw new IllegalStateException("Could not create the car photo folder: " + dir, e);
        }
        String location = dir.toUri().toString();
        if (!location.endsWith("/")) location += "/";
        registry.addResourceHandler("/uploads/cars/**").addResourceLocations(location);
    }
}