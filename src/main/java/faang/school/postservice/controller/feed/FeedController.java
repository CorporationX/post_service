package faang.school.postservice.controller.feed;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.redis.service.FeedCacheService;
import faang.school.postservice.redis.service.FeedHeatService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
@Slf4j
public class FeedController {
    private final FeedCacheService feedCacheService;
    private final FeedHeatService feedHeatService;
    private final UserContext userContext;

    @GetMapping("/feed")
    public ResponseEntity<List<PostDto>> getUserFeed(@Nullable @RequestParam("postId") Long postId) {
        var userId = userContext.getUserId();
        List<PostDto> userFeed = feedCacheService.getFeedByUserId(postId, userId);

        if (userFeed.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(userFeed);
    }

    @GetMapping("/heat")
    public void sendHeatEventsAsync() {
        feedHeatService.sendHeatEvents();
    }
}