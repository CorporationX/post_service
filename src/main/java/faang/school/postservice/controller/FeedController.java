package faang.school.postservice.controller;

import faang.school.postservice.model.redis.RedisFeed;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/feed")
@RequiredArgsConstructor
public class FeedController {
    private final FeedService feedService;

    public List<RedisFeed> getFeed(@RequestParam long postId,
                                   @RequestParam long userId){
        return new ArrayList<>();
    }
}
