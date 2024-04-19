package faang.school.postservice.scheduler;

import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthorBanner {
    private final PostService postService;

    @Scheduled(cron = "${post.banner.scheduler.cron}")
    public void banUsers() {
        postService.banUsers();
    }
}
