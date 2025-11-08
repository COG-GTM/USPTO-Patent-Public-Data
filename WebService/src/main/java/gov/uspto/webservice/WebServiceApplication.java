package gov.uspto.webservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Main Spring Boot application class for USPTO Patent Web Service.
 * 
 * This application provides REST API endpoints for patent data processing,
 * integrating with the existing BulkDownloader and PatentDocument modules.
 * 
 * @EnableAsync enables asynchronous processing capabilities for long-running
 * patent operations, coordinating with BulkDownloader's existing async features.
 */
@SpringBootApplication
@EnableAsync
public class WebServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebServiceApplication.class, args);
    }
}
