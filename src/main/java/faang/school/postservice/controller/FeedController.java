package faang.school.postservice.controller;

import faang.school.postservice.model.redis.RedisPost;
import faang.school.postservice.service.feed.FeedHeater;
import faang.school.postservice.service.feed.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class FeedController {

    private final FeedService feedService;
    private final FeedHeater feedHeater;

    @GetMapping("/feed")
    public List<RedisPost> getPostFeedBatch(@RequestParam(value = "postId") Optional<Long> postId) {
        return feedService.getFeed(postId);
    }

    @PutMapping("/start")
    public void startFeedHeating() {
        feedHeater.start();
    }
}
