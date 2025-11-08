package gov.uspto.webservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Async configuration for USPTO Patent Web Service.
 * 
 * Configures thread pool for asynchronous operations, coordinating with
 * BulkDownloader's existing async capabilities for patent processing.
 * 
 * Thread pool settings are configured in application.properties:
 * - spring.task.execution.pool.core-size=5
 * - spring.task.execution.pool.max-size=10
 * 
 * This allows long-running patent operations (bulk downloads, transformations)
 * to run asynchronously without blocking the main request threads.
 */
@Configuration
public class AsyncConfig {

    /**
     * Configure task executor for async operations.
     * 
     * Uses Spring Boot's default task executor configuration from
     * application.properties. This executor will be used by methods
     * annotated with @Async.
     * 
     * The BulkDownloader module already has async capabilities for
     * patent downloads and processing. This configuration ensures
     * Spring's @EnableAsync works harmoniously with those existing
     * async features.
     * 
     * @return Executor for async task execution
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("uspto-async-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        return executor;
    }
}
