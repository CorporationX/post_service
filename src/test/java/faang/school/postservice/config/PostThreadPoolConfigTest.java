package faang.school.postservice.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.assertEquals;


@ExtendWith(MockitoExtension.class)
class PostThreadPoolConfigTest {
    @InjectMocks
    private PostThreadPoolConfig postThreadPoolConfig;

    private final Integer postPoolSize = 10;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(postThreadPoolConfig, "postPoolSize", postPoolSize);
    }

    @Test
    void testExecutorPool() {
        Executor executor = postThreadPoolConfig.postExecutorPool();

        ThreadPoolTaskExecutor threadPoolTaskExecutor = (ThreadPoolTaskExecutor) executor;

        assertEquals(10, threadPoolTaskExecutor.getCorePoolSize());
        assertEquals(10, threadPoolTaskExecutor.getMaxPoolSize());
        assertEquals("CustomThreadPool-", threadPoolTaskExecutor.getThreadNamePrefix());
    }
}