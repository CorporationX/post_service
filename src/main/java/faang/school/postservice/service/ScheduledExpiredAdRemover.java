package faang.school.postservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledExpiredAdRemover {
    private final AdService adService;

    @Scheduled(cron = "${ad-remover.cron}")
    public void removeDueAdds() {
        adService.removeDueAdds();
    }
}
