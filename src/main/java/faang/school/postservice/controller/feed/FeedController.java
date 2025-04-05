package faang.school.postservice.controller.feed;

import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.service.feed.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/feed")
public class FeedController {

    private final FeedService feedService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserFeed(@PathVariable("userId") Integer userId,
                                         @RequestParam(required = false) Integer pageNum) {
        try {
            if (pageNum == null) {
                pageNum = 0;
            }
            Set<PostResponseDto> feed = feedService.getFeed(userId, pageNum);
            return ResponseEntity.ok(feed);

        } catch (Exception e) {
            log.error("Failed to get feed for user {}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "Feed loading failed",
                            "message", e.getMessage()
                    ));
        }
    }
}
