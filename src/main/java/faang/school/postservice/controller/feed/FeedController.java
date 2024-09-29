package faang.school.postservice.controller.feed;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.redis.service.RedisFeedCacheService;
import faang.school.postservice.service.FeedHeatService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class FeedController {
    private final RedisFeedCacheService redisFeedCacheService;
    private final FeedHeatService feedHeatService;
    private final UserContext userContext;

    @GetMapping("/feed")
    public List<PostDto> getUserFeed(@Nullable @RequestParam("postId") Long postId){
        var userId = userContext.getUserId();
        return redisFeedCacheService.getUserFeed(postId, userId);
    }

    @GetMapping("/heat")
    public void cacheHeat(){
        feedHeatService.sendHeatEvents();
    }
}
