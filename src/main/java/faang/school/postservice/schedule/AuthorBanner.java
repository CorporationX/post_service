package faang.school.postservice.schedule;

import faang.school.postservice.service.post.PostServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthorBanner {
    private final PostServiceImpl postService;

    @Scheduled(cron = "${cron.find-authors-for-ban-cron}")
    public void deleteExpiredAds() {
        postService.findAuthorsForBan();
    }
}