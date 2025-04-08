package faang.school.postservice.component;

import faang.school.postservice.service.userModeration.UserModerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthorBanner {
    private final UserModerationService userModerationService;

    @Scheduled(cron = "${app.cron.author-banner}")
    public void runDailyTask() {
        userModerationService.checkAndBanUsersWithUnverifiedPosts();
    }
}
