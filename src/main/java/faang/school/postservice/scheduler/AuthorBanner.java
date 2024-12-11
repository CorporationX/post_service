package faang.school.postservice.scheduler;

import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthorBanner {

    private final PostService postService;

    @Scheduled(cron = "${schedule.ban-users}")
    public void banAuthors() {
        postService.banAuthorsWithTooManyUnverifiedPosts();
    }
}
