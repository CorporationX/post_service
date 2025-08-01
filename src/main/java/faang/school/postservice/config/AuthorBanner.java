package faang.school.postservice.config;

import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthorBanner {

    private final PostService postService;

    @Scheduled(cron = "${cron.author_baned}")
    public void authorBanned() {
        postService.banUsers();
    }
}
