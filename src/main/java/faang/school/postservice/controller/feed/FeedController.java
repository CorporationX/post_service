package faang.school.postservice.controller.feed;

import faang.school.postservice.dto.feed.FeedPostDto;
import faang.school.postservice.service.feed.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    @GetMapping
    public ResponseEntity<List<FeedPostDto>> getFeed(
            @RequestParam(name = "after", required = false) Long afterPostId,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(name = "userId")  Long userId
    ) {

        List<FeedPostDto> feed = feedService.getFeed(userId, afterPostId, limit);
        return ResponseEntity.ok(feed);
    }

}
