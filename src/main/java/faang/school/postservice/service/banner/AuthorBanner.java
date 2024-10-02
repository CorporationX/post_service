package faang.school.postservice.service.banner;

import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorBanner {

    private final PostService postService;

    @Scheduled(cron = "${redis.banner.schedule}")
    public void checkUnverifiedPosts() {
        postService.publishingUsersForBan();
    }
}