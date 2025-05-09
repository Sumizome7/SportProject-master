package sportproject.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 添加本地磁盘路径映射
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600);

        registry.addResourceHandler("/videos/**")
                .addResourceLocations("file:///D:/SportProject-profile/upload/videos/")
                .setCachePeriod(0);

        registry.addResourceHandler("/avatars/**")
                .addResourceLocations("file:///D:/SportProject-profile/upload/avatars/");

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:///D:/SportProject-profile/upload/");

        registry.addResourceHandler("/downloads/**")
                .addResourceLocations("file:///D:/SportProject-profile/download/");

        registry.addResourceHandler("/videos/clips/**")
                .addResourceLocations("file:///D:/SportProject-profile/upload/videos/clips/");
        registry.addResourceHandler("/videos/cover/**")
                .addResourceLocations("file:///D:/SportProject-profile/upload/videos/cover/");
    }
}
