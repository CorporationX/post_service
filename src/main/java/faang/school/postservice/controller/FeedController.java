package faang.school.postservice.controller;

import faang.school.postservice.dto.redis.PostRedis;
import faang.school.postservice.service.feed.FeedService;
import faang.school.postservice.utils.FeedHeater;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;
    private final FeedHeater feedHeater;

    @GetMapping("/feed")
    public List<PostRedis> getFeeds(@RequestParam Long id) {
        return feedService.getPosts(id);
    }

    @PostMapping("/heat")
    public void feedHeater() {
    feedHeater.heat();
    }
}
