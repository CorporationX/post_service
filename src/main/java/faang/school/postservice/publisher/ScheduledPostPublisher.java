package faang.school.postservice.publisher;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ScheduledPostPublisher {
    private final PostService postService;

    @Scheduled(cron = "${post.publisher.scheduler.cron}")
    public List<PostDto> publishScheduledPosts() {
        return postService.publishScheduledPosts();
    }
}
