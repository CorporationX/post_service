package faang.school.postservice.service;

import faang.school.postservice.dto.event.HeatFeedKafkaEventDto;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FeedHeaterService {
    private final PostService postService;
    private final RedisCashService redisCashService;

    public void fillFeedCache(HeatFeedKafkaEventDto event) {
        for (long userId : event.getUserIds()) {
            List<Post> posts = postService.getPostsByFollowee(userId);
            if (!posts.isEmpty()) {
                redisCashService.createFeedCacheAsync(userId, posts);
            }
        }
    }
}