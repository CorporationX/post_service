package faang.school.postservice.service;

import faang.school.postservice.job.moderation.PostModeratingProcessor;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final PostModeratingProcessor postModeratingProcessor;

    @Value("${post.moderation.batch-size}")
    private int batchSize;

    @Transactional
    public void moderateNewPosts() {
        List<Post> posts = postRepository.findAllByVerifiedDateIsNull();
        List<List<Post>> batches = splitIntoBatches(posts);
        CompletableFuture<Void>[] futures = batches.stream()
                .map(postModeratingProcessor::processBatch)
                .toArray(CompletableFuture[]::new);
        CompletableFuture<Void> allOfFuture = CompletableFuture.allOf(futures);
        allOfFuture.join();
        postRepository.updateVerifiedInfo(posts);
    }

    private List<List<Post>> splitIntoBatches(List<Post> posts) {
        List<List<Post>> batches = new ArrayList<>();
        for (int i = 0; i < posts.size(); i += batchSize) {
            batches.add(posts.subList(i, Math.min(i + batchSize, posts.size())));
        }
        return batches;
    }
}
