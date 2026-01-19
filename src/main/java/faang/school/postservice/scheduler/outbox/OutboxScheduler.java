package faang.school.postservice.scheduler.outbox;

import faang.school.postservice.service.outbox.OutboxDispatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxScheduler {
    private final OutboxDispatchService dispatchService;

    @Scheduled(fixedDelayString = "${app.outbox.dispatcher.fixed-delay-ms:1000}")
    public void tick() {
        dispatchService.dispatchOnce(100);
    }
}