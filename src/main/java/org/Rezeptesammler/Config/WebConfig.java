package org.Rezeptesammler.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Ermöglicht Zugriff auf Dateien im 'downloads'-Ordner
        registry.addResourceHandler("/downloads/**")
                .addResourceLocations("file:downloads/");
    }
}
