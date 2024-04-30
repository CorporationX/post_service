package faang.school.postservice.schedulers;

import faang.school.postservice.service.UserBannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserBannerScheduler {
    private final UserBannerService userBannerService;

    @Scheduled(cron = "${scheduled.author-banner}")
    public void scheduledBan(){
        userBannerService.banPosts();
    }
}
