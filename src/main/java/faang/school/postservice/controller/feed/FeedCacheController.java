package faang.school.postservice.controller.feed;

import faang.school.postservice.service.feed.FeedHeatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/feed")
@RequiredArgsConstructor
public class FeedCacheController {
    private final FeedHeatingService feedHeatingService;

    @PostMapping("/heat")
    public ResponseEntity<String> heatFeedCache() {
        boolean submitted = feedHeatingService.submitHeatingJob();
        if (!submitted) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Heating system overloaded");
        }
        return ResponseEntity.accepted().body("Feed heating process started successfully");
    }
}
