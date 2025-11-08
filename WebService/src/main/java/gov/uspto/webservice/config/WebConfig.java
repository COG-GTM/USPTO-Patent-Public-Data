package gov.uspto.webservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration for USPTO Patent Web Service.
 * 
 * Configures:
 * - CORS with proper headers for patent file uploads
 * - Multipart file handling for large patent files (500MB limit)
 * - Message converters
 * - Error handling
 * 
 * Note: Multipart file size limits are configured in application.properties
 * to support large patent bulk files (up to 500MB).
 */
@Configuration
public class WebConfig {

    /**
     * Configure CORS (Cross-Origin Resource Sharing) for the API.
     * 
     * Allows cross-origin requests with proper headers including:
     * - Authorization: For JWT tokens (when AuthenticationService is integrated)
     * - Cache-Control: For caching directives
     * - Content-Type: For multipart/form-data patent file uploads
     * 
     * @return WebMvcConfigurer with CORS configuration
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                    .allowedOrigins("*")
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                    .allowedHeaders(
                        "Authorization",
                        "Cache-Control",
                        "Content-Type"
                    )
                    .exposedHeaders(
                        "Authorization",
                        "Cache-Control",
                        "Content-Type"
                    )
                    .maxAge(3600);
            }
        };
    }
}
