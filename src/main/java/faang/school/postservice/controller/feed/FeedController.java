package faang.school.postservice.controller.feed;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.redis.cache.model.RedisFeed;
import faang.school.postservice.redis.cache.service.RedisFeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class FeedController {
    private final RedisFeedService feedService;
    private final UserContext userContext;


    @GetMapping("/feed")
    public ResponseEntity<RedisFeed> getFeed(@RequestParam(value = "after", required = false) Long afterPostId,
                                             @RequestParam(value = "pageSize", required = false) Integer pageSize) {
        long userId = userContext.getUserId();
        RedisFeed feed = feedService.getFeed(userId, afterPostId, pageSize);
        return ResponseEntity.ok(feed);
    }
}
