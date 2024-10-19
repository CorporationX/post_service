package faang.school.postservice.controller;

import faang.school.postservice.model.redis.PostRedis;
import faang.school.postservice.repository.cache.NewsFeedCacheRepository;
import faang.school.postservice.repository.cache.PostCacheRepository;
import faang.school.postservice.service.NewsFeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("v1/newsfeeds")
public class NewsFeedController {
    private final NewsFeedService newsFeedService;

    @GetMapping("/users/{userId}")
    public List<PostRedis> findById(@PathVariable long userId) {
        return newsFeedService.getPostFromNewsFeed(userId);
    }

}
