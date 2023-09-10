package faang.school.postservice;

import faang.school.postservice.publisher.AuthorBannerPublisher;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthorBanner {

    private final PostService postService;
    private final AuthorBannerPublisher authorBannerPublisher;

    @Scheduled(cron = "${author_banner.start_scheduler_cron}")
    public void checkVerified() {
        List<Long> posts = postService.getByPostIsVerifiedFalse();
        if (!posts.isEmpty()) {
            authorBannerPublisher.publish(posts);
        }
    }
}
