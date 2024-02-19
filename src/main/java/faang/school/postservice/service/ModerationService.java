package faang.school.postservice.service;

import faang.school.postservice.model.Post;
import faang.school.postservice.moderation.ModerationDictionary;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ModerationService {
    private final PostRepository postRepository;
    private final ModerationDictionary moderationDictionary;
    @Value("${post.moderation.batchSize}")
    private int batchSize;

    @Transactional
    public void checkPostsWithBadWord() {
        List<Post> allPosts = postRepository.findAllByVerifiedAtNull();
        List<List<Post>> postPartitions = ListUtils.partition(allPosts, batchSize);
        postPartitions.forEach(this::moderatePosts);
    }

    @Async("asyncExecutor")
    private void moderatePosts(List<Post> posts) {
        posts.forEach(post -> {
            post.setVerified(!moderationDictionary.containsBadWord(post.getContent()));
            post.setVerifiedAt(LocalDateTime.now());
        });
        postRepository.saveAll(posts);
    }
}
