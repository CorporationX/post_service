package faang.school.postservice.config.thread.pool;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@Data
public class ThreadPoolConfig {

    @Value("${thread-pool.verification-content-pool.num-of-thread}")
    private int verificationPoolNumOfThreads;

    public static final String VERIFICATION_POOL_BEAN_NAME = "verificationContentPool";

    @Bean(name = VERIFICATION_POOL_BEAN_NAME)
    public ExecutorService getVerificationContentPool() {
        return Executors.newFixedThreadPool(verificationPoolNumOfThreads);
    }
}
