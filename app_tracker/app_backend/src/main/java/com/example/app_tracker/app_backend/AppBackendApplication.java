package com.example.app_tracker.app_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@EntityScan("com.example.app_tracker.app_backend")
@OpenAPIDefinition(
	info = @Info(
		title = "Application Tracking Application REST API",
		description = "Application Tracking Application backend REST API documentation",
		version = "v0.0.0.0",
		contact = @Contact(
			name = "swelldisco",
			email = "no.contact@no_email.com",
			url = "http://github.com/swelldisco"
		),
		license = @License(
			name = "I have no idea because I have no way to stop people from helping themselves to this.  Also, really?  You shouldn't trust me to create a functional, secure application.",
			url = "http://github.com/swelldisco"
		)
	),
	externalDocs = @ExternalDocumentation(
		description = "Maybe someday there will be proper external documentation?",
		url = "http://no.idea.here")		
)
@SpringBootApplication
public class AppBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppBackendApplication.class, args);
	}

}
