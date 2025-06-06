package faang.school.postservice.config.threads;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Data
@Component
public class ThreadPoolConfig {

    private final ExecutorService threadPool = Executors.newFixedThreadPool(10);
}
