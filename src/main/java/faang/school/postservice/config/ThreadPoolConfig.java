package faang.school.postservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@Slf4j
public class ThreadPoolConfig {

    @Bean
    public ExecutorService scheduledPostExecutorService() {
      log.info("Creating thread pool with 10 threads for scheduled post publishing");
      return Executors.newFixedThreadPool(10);
    }
}