package faang.school.postservice.controller;

import faang.school.postservice.dto.feed.FeedPostDto;
import faang.school.postservice.service.feed.FeedRetrievalService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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

    private final FeedRetrievalService feedRetrievalService;

    @GetMapping
    public ResponseEntity<List<FeedPostDto>> getFeed(
            @RequestParam(name = "cursorPostId", required = false) Long cursorPostId,
            @Min(value = 1, message = "Limit must be at least 1.")
            @Max(value = 100, message = "Limit cannot exceed 100.")
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(name = "userId") Long userId
    ) {
        List<FeedPostDto> feed = feedRetrievalService.getFeed(userId, cursorPostId, limit);
        return ResponseEntity.ok(feed);
    }
}
