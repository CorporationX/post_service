package faang.school.postservice.controller.feed;

import faang.school.postservice.dto.post.PostFeedDto;
import faang.school.postservice.service.feed.FeedService;
import faang.school.postservice.service.feed.HeaterCashFeed;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;
    private final HeaterCashFeed heaterCashFeed;

    @GetMapping("/{userId}")
    public ResponseEntity<List<PostFeedDto>> getNewsFeed(@PathVariable("userId") long userId,
                                                         @RequestParam(value = "postId", required = false) Long postId) {
        return ResponseEntity.status(HttpStatus.OK).body(feedService.getNewsFeed(postId, userId));
    }

    @PostMapping("/heat")
    public ResponseEntity<Void> heatCashFeed() {
        heaterCashFeed.feedHeat();
        return ResponseEntity.ok().build();
    }
}