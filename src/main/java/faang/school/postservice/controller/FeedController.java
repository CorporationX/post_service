package faang.school.postservice.controller;

import faang.school.postservice.dto.feed.FeedDto;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/feed")
public class FeedController {

    private final FeedService feedService;
    private final RedisPostRepository redisPostRepository;
    @GetMapping
    public List<FeedDto> getFeed(@RequestParam(required = false) Long postId) {
        return feedService.getFeed(postId);
    }

    @PostMapping("/heat")
    public void heatFeed() {
        feedService.heatFeed();
    }
}
