package faang.school.postservice.scheduler.service;

import faang.school.postservice.dto.post.PostOutputDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.scheduler.SchedulerService;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class SchedulerServiceImpl implements SchedulerService {
    private final PostRepository postRepository;
    private final PostService postService;
    private final ThreadPoolTaskExecutor taskExecutor;

    @Override
    public void publishScheduledPosts() {
        List<Post> posts = postRepository.findReadyToPublish();

        List<CompletableFuture<PostOutputDto>> futures = posts.stream()
                .map(post ->
                        CompletableFuture.supplyAsync(() -> postService.publishPost(post.getId()), taskExecutor)
                ).toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }
}
