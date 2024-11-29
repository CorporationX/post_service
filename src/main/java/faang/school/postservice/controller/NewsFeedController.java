package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostNewsFeedDto;
import faang.school.postservice.service.redis.FeedHeatService;
import faang.school.postservice.service.redis.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/feed")
@RestController
public class NewsFeedController {
    private final UserContext userContext;
    private final FeedService feedService;
    private final FeedHeatService feedHeatService;

    @GetMapping("/load")
    public List<PostNewsFeedDto> loadFeed(@RequestParam(required = false) Long postId) {
        long userId = userContext.getUserId();
        return feedService.getFeedForUser(userId, postId);
    }

    @PostMapping("/heat")
    public ResponseEntity<Void> startHeat() {
        feedHeatService.startCacheHeat();
        return ResponseEntity.ok().build();
    }
}