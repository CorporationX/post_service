package faang.school.postservice.moderation.logic;

import faang.school.postservice.model.Post;
import faang.school.postservice.moderation.dictionary.ModerationDictionary;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@RequiredArgsConstructor
public class PostModerator {

    private final ModerationDictionary moderationDictionary;
    private final PostRepository postRepository;
    private final EntityManager entityManager;

    private ExecutorService executorService;


    @Value("${poolToPostVerified.poolAmount}")
    private int nThreads;

    public void moderatePosts(List<Post> unverifiedPosts) {
        executorService = Executors.newFixedThreadPool(nThreads);
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        int batchSize = (int) Math.ceil((double) unverifiedPosts.size() / nThreads);

        for (int i = 0; i < unverifiedPosts.size(); i += batchSize) {
            int start = i;
            int end = Math.min(i + batchSize, unverifiedPosts.size());

            List<Post> subList = unverifiedPosts.subList(start, end);

            CompletableFuture<Void> future = CompletableFuture.runAsync(
                    () -> {
                        for (Post post : subList) {
                            boolean containsBadWords = moderationDictionary.isContainsBadWordInTheText(post.getContent());
                            LocalDateTime verifiedDate = LocalDateTime.now();
                            boolean verified = !containsBadWords;

                            postRepository.setVerifiedAndVerifiedDate(post.getId(), verified, verifiedDate);
                        }

                        entityManager.clear();
                    }, executorService
            );

            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenRun(() -> executorService.shutdown())
                .join();
    }
}
