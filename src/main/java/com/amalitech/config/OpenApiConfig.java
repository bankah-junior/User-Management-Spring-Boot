package com.amalitech.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI userManagementOpenAPI() {
        Server localServer = new Server();
        localServer.setUrl("http://localhost:8080");
        localServer.setDescription("Local Development Server");
        
        Contact contact = new Contact();
        contact.setName("AmaliTech User Management Team");
        contact.setEmail("support@amalitech.com");
        
        License license = new License();
        license.setName("MIT License");
        license.setUrl("https://opensource.org/licenses/MIT");
        
        Info info = new Info()
                .title("User Management API")
                .version("1.0.0")
                .description("RESTful API for managing user resources with CRUD operations, " +
                        "input validation, and unique email enforcement. " +
                        "Built with Spring Boot and MongoDB.")
                .contact(contact)
                .license(license);
        
        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer));
    }
}
