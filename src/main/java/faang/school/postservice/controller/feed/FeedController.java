package faang.school.postservice.controller.feed;

import faang.school.postservice.dto.feed.FeedDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.redis.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    @GetMapping("/feed/{followerId}")
    public ResponseEntity<List<PostDto>> getFollowerFeed(@PathVariable Long followerId) {
        FeedDto feedDto = feedService.getFeed(followerId);
        return new ResponseEntity<>(feedDto.posts(), HttpStatus.OK);
    }
}
