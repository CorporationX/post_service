package faang.school.postservice.service;

import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.moderation.AsyncModerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private static final String POST = "Post";

    private final PostRepository postRepository;
    private final AsyncModerationService asyncModerationService;

    @Value("${moderation.batch-size}")
    private int batchSize;

    public void moderateAllUnverifiedPosts() {
        List<Post> unverifiedPosts = postRepository.findByVerifiedAtIsNull();
        if (unverifiedPosts.isEmpty()) {
            log.warn("No unverified posts found for moderation");
            return;
        }
        List<List<Post>> batches = partitionList(unverifiedPosts);
        batches.forEach(asyncModerationService::moderateBatchAsync);
    }

    private <T> List<List<T>> partitionList(List<T> list) {
        if (batchSize <= 0) {
            throw new IllegalArgumentException("batchSize must be greater than 0");
        }
        return ListUtils.partition(list, batchSize);
    }

    public Post getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(POST, postId));

        log.info("Get post with id {}", postId);
        return post;
    }
}
