package faang.school.postservice.controller;

import faang.school.postservice.cache.redis.FeedCache;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.CachedPostDto;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
public class FeedController {
    private final FeedCache feedCache;
    private final UserContext userContext;

    @GetMapping("/feed")
    public List<CachedPostDto> getFeed(@RequestParam(required = false) Long fromPostId) {
        Long userId = userContext.getUserId();
        return feedCache.getFeed(userId, fromPostId);
    }
}
