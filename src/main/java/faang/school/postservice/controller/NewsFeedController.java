package faang.school.postservice.controller;

import faang.school.postservice.cache.model.PostRedis;
import faang.school.postservice.cache.service.NewsFeedService;
import faang.school.postservice.service.NewsFeedHeater;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.TreeSet;

@RestController
@RequestMapping("/feed")
@RequiredArgsConstructor
public class NewsFeedController {
    private final NewsFeedService newsFeedService;
    private final NewsFeedHeater newsFeedHeater;

    @GetMapping
    public TreeSet<PostRedis> getPosts(@RequestHeader("x-user-id") Long userId,
                                       @RequestParam(required = false) Long lastPostId) {
        return newsFeedService.getNewsFeed(userId, lastPostId);
    }

    @PutMapping("/heat")
    public void heat() {
        newsFeedHeater.heat();
    }
}
