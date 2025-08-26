package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.feed.FeedPostDto;
import faang.school.postservice.service.feed.FeedReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FeedController {

    private final FeedReadService feedReadService;
    private final UserContext userContext;

    @GetMapping("/feed")
    public ResponseEntity<List<FeedPostDto>> getFeed(
            @RequestParam(name = "after", required = false) Long afterPostId,
            @RequestParam(name = "limit", required = false, defaultValue = "20") Integer limit
    ) {
        Long userId = userContext.getUserId();
        List<FeedPostDto> feed = feedReadService.getFeed(userId, afterPostId, Math.min(Math.max(limit, 1), 50));
        return ResponseEntity.ok(feed);
    }
}
