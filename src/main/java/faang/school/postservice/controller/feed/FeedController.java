package faang.school.postservice.controller.feed;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.redis.service.FeedCacheService;
import faang.school.postservice.redis.service.FeedHeatService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class FeedController {
    private final FeedCacheService feedCacheService;
    private final FeedHeatService feedHeatService;
    private final UserContext userContext;

    @GetMapping("/feed")
    public List<PostDto> getUserFeed(@Nullable @RequestParam("postId") Long postId){
        var userId = userContext.getUserId();
        return feedCacheService.getFeedByUserId(postId, userId);
    }

    @GetMapping("/heat")
    public CompletableFuture<Void> cacheHeat(){
        return feedHeatService.sendHeatEvents();
    }
}