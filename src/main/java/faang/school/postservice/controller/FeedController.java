package faang.school.postservice.controller;

import faang.school.postservice.dto.feed.FeedPostDto;
import faang.school.postservice.service.feed.FeedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/feed")
@RequiredArgsConstructor
@Tag(name = "Feed API", description = "Endpoints for operations with Feed")
public class FeedController {

    private final FeedService feedService;

    @GetMapping
    @Operation(summary = "Receive feed for user")
    public ResponseEntity<List<FeedPostDto>> get(@RequestParam(required = false)
                                             Long lastPostId,
                                                 @RequestHeader("x-user-id")
                                             Long userId) {
        log.info("Request to receive a feed from userId: {}", userId);
        return ResponseEntity.ok(feedService.get(userId, lastPostId));
    }
}
