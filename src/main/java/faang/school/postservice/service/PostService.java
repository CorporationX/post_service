package faang.school.postservice.service;

import faang.school.postservice.model.Post;
import faang.school.postservice.moderation.ModerationDictionary;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
public class PostService {

    @Value("${post.moderation.sublist.length}")
    private int sublistLength;

    private final PostRepository postRepository;
    private final ModerationDictionary moderationDictionary;
    private final ExecutorService executor;

    @Async("executor")
    public void moderatePostsContent() {
        List<Post> unverifiedPosts = postRepository.findReadyToVerified();

        for (int i = 0; i < unverifiedPosts.size(); i += sublistLength) {
            List<Post> subList = unverifiedPosts.subList(i, Math.min(unverifiedPosts.size(), i + sublistLength));

            Map<Long, String> postsContent = new HashMap<>();
            subList.forEach(post -> postsContent.put(post.getId(), post.getContent()));

            CompletableFuture<Map<Long, Boolean>> verifiedPosts =
                    CompletableFuture.supplyAsync(() -> moderationDictionary.searchSwearWords(postsContent), executor);

            verifiedPosts.thenAccept(map -> {
                subList.forEach(post -> {
                    post.setVerified(map.get(post.getId()));
                    post.setVerifiedDate(LocalDateTime.now());

                    postRepository.save(post);
                });
            });
        }
    }
}
