package faang.school.postservice.service.post;

import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ModerationAsyncService {
    private final ModerationProcessingService moderationProcessingService;

    @Async("asyncTaskExecutor")
    public void moderatePostsSublistAsync(List<Post> posts) {
        moderationProcessingService.moderatePostsSublist(posts);
    }
}