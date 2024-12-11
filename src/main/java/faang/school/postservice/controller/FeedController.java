package faang.school.postservice.controller;

import faang.school.postservice.model.dto.redis.cache.RedisPostDto;
import faang.school.postservice.service.FeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/feed")
public class FeedController {

    private final FeedService feedService;

    @Autowired
    public FeedController(FeedService feedService) {
        this.feedService = feedService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<RedisPostDto>> getNewsFeed(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        List<RedisPostDto> posts = feedService.getNewsFeed(userId, page, pageSize);
        return new ResponseEntity<>(posts, HttpStatus.OK);
    }

    @PostMapping("/heat")
    public ResponseEntity<String> heatCache() {
        feedService.startHeatingInBackground();
        return ResponseEntity.ok("Cache heating started");
    }

}
