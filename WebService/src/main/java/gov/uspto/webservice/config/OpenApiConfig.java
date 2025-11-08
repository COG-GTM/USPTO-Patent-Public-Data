package gov.uspto.webservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger configuration for USPTO Patent Web Service.
 * 
 * Configures Swagger UI for API documentation and testing.
 * Swagger UI will be accessible at: /swagger-ui/index.html
 * 
 * Note: Security schemes are placeholders pending AuthenticationService integration.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Configure OpenAPI documentation.
     * 
     * @return OpenAPI configuration
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("USPTO Patent Service API")
                .version("0.0.1-SNAPSHOT")
                .description("REST API for USPTO Patent Public Data processing and retrieval. " +
                    "Provides endpoints for patent bulk data download, transformation, and search.")
                .contact(new Contact()
                    .name("USPTO Patent Public Data")
                    .url("https://github.com/COG-GTM/USPTO-Patent-Public-Data"))
                .license(new License()
                    .name("License")
                    .url("https://github.com/COG-GTM/USPTO-Patent-Public-Data/blob/master/LICENSE")))
            .components(new Components()
                .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("JWT Bearer token authentication (pending AuthenticationService integration)")));
    }
}
