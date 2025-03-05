package faang.school.postservice.service.subscription;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SubscriptionService {

    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;

    @Value("${spring.data.cache.warmup.posts-age-months}")
    private int postsAgeMonths;

    public Set<Long> getAuthorIds() {
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusMonths(postsAgeMonths);
            List<Post> recentPosts = postRepository.findByAuthorIdIsNotNullAndCreatedAtAfterAndPublishedTrue(cutoffDate);

            Set<Long> authorIds = recentPosts.stream()
                    .map(Post::getAuthorId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            Set<Long> existingAuthors = new HashSet<>();
            for (Long authorId : authorIds) {
                if (userServiceClient.findById(authorId).isPresent()) {
                    existingAuthors.add(authorId);
                } else {
                    log.warn("Author {} not found in user service", authorId);
                }
            }
            log.info("Found {} active authors from {} user posts", existingAuthors.size(), recentPosts.size());
            return existingAuthors;
        } catch (Exception e) {
            log.error("Error getting active authors", e);
            throw e;
        }
    }

    public Set<Long> getProjectIds() {
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusMonths(postsAgeMonths);
            List<Post> recentProjectPosts = postRepository.findByProjectIdIsNotNullAndCreatedAtAfterAndPublishedTrue(cutoffDate);

            Set<Long> projectIds = recentProjectPosts.stream()
                    .map(Post::getProjectId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            log.info("Found {} active projects from {} project posts", projectIds.size(), recentProjectPosts.size());
            return projectIds;
        } catch (Exception e) {
            log.error("Error getting active projects", e);
            throw e;
        }
    }

    public Set<Long> getSubscriberIds(Post post) {
        try {
            Set<Long> subscribers = new HashSet<>();

            if (post.getAuthorId() != null) {
                subscribers.addAll(userServiceClient.getUserSubscribersIds(post.getAuthorId()));
                log.debug("Retrieved {} subscribers for author {}",
                        subscribers.size(), post.getAuthorId());
            }

            if (post.getProjectId() != null) {
                subscribers.addAll(userServiceClient.getProjectSubscriptions(post.getProjectId()));
                log.debug("Retrieved {} total subscribers after adding project {} subscribers",
                        subscribers.size(), post.getProjectId());
            }

            if (subscribers.isEmpty()) {
                log.warn("Post {} has no associated subscribers", post.getId());
            }

            return subscribers;
        } catch (Exception e) {
            log.error("Error retrieving subscribers for post {}", post.getId(), e);
            return Collections.emptySet();
        }
    }
}