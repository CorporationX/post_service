package faang.school.postservice.ban;

import faang.school.postservice.service.BanUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthorBanner {
    private final BanUserService banUserService;

    @Scheduled(cron = "${scheduler.cron.ban-users}")
    public void banUsers() {
        banUserService.banAuthorsWithUnverifiedPosts();
    }
}
