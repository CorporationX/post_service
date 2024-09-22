package faang.school.postservice.controller;

import faang.school.postservice.cache.FeedCache;
import faang.school.postservice.cache.dto.CachedPost;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feed")
public class FeedController {

    private final FeedCache feedCache;

    @GetMapping
    public List<CachedPost> getFeed(@RequestParam(required = false) Long lastPostId) {
        return feedCache.getFeed(lastPostId);
    }
}