package faang.school.postservice.controller.feed;

import faang.school.postservice.dto.FeedPostDto;
import faang.school.postservice.dto.Post.PostDto;
import faang.school.postservice.repository.redis.FeedRepository;
import faang.school.postservice.service.redis.FeedCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Component
@RequiredArgsConstructor
public class ValidateFeedInput {
    @Value(value = "${news-feed.feed.amount}")
    private int amount;

    private final FeedCacheService feedCacheService;

    public Set<Long> getPostsForUser(Long userId, FeedPostDto dto) {
        if (dto == null) {
            return feedCacheService.getPostsForUser(userId, amount);
        } else {
            if (dto.containsPageAmount() && !dto.containsPostId()) {
                return feedCacheService.getPostsForUser(userId, dto.getPageAmount());
            } else if (dto.containsPostId() && dto.containsPageAmount()) {
                return feedCacheService.getPostsForUserFromPostId(userId, dto.getPostId(), dto.getPageAmount());
            } else {
                return feedCacheService.getPostsForUserFromPostId(userId, dto.getPostId(), amount);
            }
        }
    }
}
