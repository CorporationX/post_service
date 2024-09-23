package faang.school.postservice.controller.feed;

import faang.school.postservice.dto.feed.PostFeedDto;
import faang.school.postservice.service.feed.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feed")
public class FeedController {
    private final FeedService feedService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<PostFeedDto>> getPostsById(@PathVariable("userId") long userId,
                                                          @RequestParam("capacity") long capacity) {
        return ResponseEntity.status(HttpStatus.OK).body(feedService.getPostsBuUserid(userId, capacity));
    }
}
