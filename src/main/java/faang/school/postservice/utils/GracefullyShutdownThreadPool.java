package faang.school.postservice.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;

import static java.util.concurrent.TimeUnit.SECONDS;

@UtilityClass
@Slf4j
public class GracefullyShutdownThreadPool {

    private static final int MAX_EXPECTATION = 60;

    public static void gracefullyShutdown(ExecutorService executorService) {
        executorService.shutdown();

        try {
            boolean isClose = executorService.awaitTermination(MAX_EXPECTATION, SECONDS);
            if (!isClose) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException ex) {
            log.error("Thread stoppage error");
            executorService.shutdownNow();
        }
    }
}

