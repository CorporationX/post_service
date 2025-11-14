package faang.school.postservice.schedule;

import faang.school.postservice.service.posts.PostBanService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AuthorBanner {

    private final PostBanService postBanService;

    @Scheduled(cron = "${scheduler.posts.cron}")
    public void publishBanCandidates() {
        postBanService.publishBanCandidates();
    }
}
