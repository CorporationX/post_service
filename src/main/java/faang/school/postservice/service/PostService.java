package faang.school.postservice.service;

import faang.school.postservice.dictionary.ModerationDictionary;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final ModerationDictionary moderationDictionary;
    @Value("${post.moderator.count-posts-in-thread}")
    private int countPostsInThread;

    @Bean
    public ExecutorService myPool() {
        return new ThreadPoolExecutor(10, 10, 0L,
                TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(1000));
    }

    public void moderatePosts() {
        List<Post> posts = postRepository.findNotVerified();
        if (posts.isEmpty()) {
            return;
        }
        for (int i = 0; i < posts.size(); i += countPostsInThread) {
            if (i + countPostsInThread > posts.size()) {
                verifyPosts(posts.subList(i, posts.size()));
            } else
                verifyPosts(posts.subList(i, i + countPostsInThread));
        }
    }

    @Async
    public void verifyPosts(List<Post> posts) {
        Set<String> banWords = moderationDictionary.getBadWords();
        for (Post post : posts) {
            Optional<String> foundBanWord = banWords.stream()
                    .filter(x -> post.getContent().toLowerCase().contains(x))
                    .findFirst();

            if (foundBanWord.isPresent()) {
                log.info("Post with id {} contains banned word {}", post.getId(), foundBanWord.get());
                post.setVerified(false);
            } else {
                post.setVerified(true);
                post.setVerifiedDate(LocalDateTime.now());
            }
            postRepository.save(post);
        }
    }
}
