package faang.school.postservice.scheduler;

import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class PostCorrecter {

    @Value("${batch-size}")
    @Setter
    private int BATCH_SIZE;

    private final PostService postService;
    private final PostRepository postRepository;

    @Scheduled(cron = "${post-correcter.scheduler.cron}")
    public void correctPosts() {
        List<Post> posts = postRepository.findReadyToPublish();
        for (int i = 0; i < posts.size(); i += BATCH_SIZE) {
            List<Post> postsToCheck = posts.subList(i, Math.min(posts.size(), i + BATCH_SIZE));
            postService.correctPostsContent(postsToCheck);
        }
    }
}