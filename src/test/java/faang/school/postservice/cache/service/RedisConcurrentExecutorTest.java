package faang.school.postservice.cache.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.integration.redis.util.RedisLockRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = RedisConcurrentExecutor.class)
@ExtendWith(MockitoExtension.class)
class RedisConcurrentExecutorTest {
    @Autowired
    private RedisConcurrentExecutor redisConcurrentExecutor;
    @MockBean
    private RedisLockRegistry redisLockRegistry;
    @MockBean
    private Lock lock;
    @Spy
    private Runnable runnable;
    @Value("${spring.data.redis.lock-registry.try-lock-millis}")
    private long tryLockMillis;

    String key;
    List<String> runnableTest;
    String value;

    @BeforeEach
    void setUp() {
        key = "key";
        runnableTest = new ArrayList<>();
        value = "value";
        runnable = () -> runnableTest.add(value);
    }

    @Test
    void testExecute() throws InterruptedException {
        when(redisLockRegistry.obtain(key)).thenReturn(lock);
        when(lock.tryLock(tryLockMillis, TimeUnit.MILLISECONDS)).thenReturn(true);

        redisConcurrentExecutor.execute(key, runnable, "any action");

        verify(redisLockRegistry, times(1)).obtain(key);
        verify(lock, times(1)).tryLock(tryLockMillis, TimeUnit.MILLISECONDS);
        assertEquals(value, runnableTest.get(0));
        verify(lock, times(1)).unlock();
    }

    @Test
    void testExecuteWhenLocked() throws InterruptedException {
        when(redisLockRegistry.obtain(key)).thenReturn(lock);
        when(lock.tryLock(tryLockMillis, TimeUnit.MILLISECONDS)).thenReturn(false);

        redisConcurrentExecutor.execute(key, runnable, "any action");

        verify(redisLockRegistry, times(1)).obtain(key);
        verify(lock, times(1)).tryLock(tryLockMillis, TimeUnit.MILLISECONDS);
        assertTrue(runnableTest.isEmpty());
    }
}