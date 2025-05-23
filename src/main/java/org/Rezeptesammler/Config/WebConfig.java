package org.Rezeptesammler.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
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
    //SessionInterceptor leitet alles auf Login seite weiter außer angegebene Seiten
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SessionInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/*.css","/*.png", "/*.js", "/login", "/register", "/error"); // Seiten, die frei zugänglich sein sollen
    }


    //Cors fehler -> noch keine Auswirkung bis jetzt gehabt
  //  @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        registry.addMapping("/**")
//                .allowedOrigins("http://localhost:8080")
//                .allowedMethods("GET", "POST", "PUT", "DELETE")
//                .allowCredentials(true)
//                .allowedHeaders("*")
//                .exposedHeaders("Authorization");
//    }
}
