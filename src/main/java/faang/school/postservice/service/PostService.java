package faang.school.postservice.service;

import faang.school.postservice.model.Post;
import faang.school.postservice.moderation.ModerationDictionary;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
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
    public void moderationPostContent() {
        List<Post> unverifiedPost = postRepository.findReadyToVerified();

        for (int i = 0; i < unverifiedPost.size(); i += sublistLength) {
            List<Post> subList = unverifiedPost.subList(i, Math.min(unverifiedPost.size(), i + sublistLength));

            CompletableFuture<List<Post>> verifiedPosts =
                    CompletableFuture.supplyAsync(() -> moderationDictionary.searchSwearWords(subList), executor);

            verifiedPosts
                    .thenAccept(posts -> posts.forEach(postRepository::save));
        }
    }
}
