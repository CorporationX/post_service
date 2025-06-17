package faang.school.postservice.service.scheduler;

import faang.school.postservice.dto.post.PostOutputDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.PostSchedulerService;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostSchedulerServiceImpl implements PostSchedulerService {
    private final PostRepository postRepository;
    private final PostService postService;
    private final ThreadPoolTaskExecutor taskExecutor;

    @Override
    public void publishScheduledPosts() {
        log.info("Started publishing scheduled posts.");
        List<Post> posts = postRepository.findReadyToPublish();
        log.info("Found {} posts ready for publication", posts.size());

        List<CompletableFuture<PostOutputDto>> futures = posts.stream()
                .map(post ->
                        CompletableFuture.supplyAsync(() -> {
                            log.debug("Publishing post ID: {}", post.getId());
                            PostOutputDto result = postService.publishPost(post.getId());
                            log.info("Successfully published post ID: {}", post.getId());
                            return result;
                        }, taskExecutor)
                ).toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        log.info("Completed scheduled posts publication");
    }
}
