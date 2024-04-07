package faang.school.postservice.controller;

import faang.school.postservice.model.redis.RedisPost;
import faang.school.postservice.service.newsfeed.FeedHeater;
import faang.school.postservice.service.newsfeed.NewsFeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/newsfeed")
@RequiredArgsConstructor
public class NewsFeedController {

    private final NewsFeedService newsFeedService;
    private final FeedHeater feedHeater;

    @GetMapping("/feed")
    public List<RedisPost> getFeed(@RequestParam(value = "postId") Optional<Long> postId) {
        return newsFeedService.getFeed(postId);
    }

    @PutMapping("/heat")
    public void generateFeeds() {
        feedHeater.generateFeeds();
    }
}
