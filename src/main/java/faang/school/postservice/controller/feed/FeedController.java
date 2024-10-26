package faang.school.postservice.controller.feed;

import faang.school.postservice.dto.feed.FeedPostDto;
import faang.school.postservice.service.feed.FeedHeaterService;
import faang.school.postservice.service.feed.FeedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/feed")
@Tag(name = "Feed", description = "APIs for managing users news feed")
public class FeedController {
    private final FeedService feedService;
    private final FeedHeaterService feedHeaterService;

    @GetMapping
    @Operation(summary = "Get news feed", description = "Getting a news feed for user")
    public List<FeedPostDto> getFeed(
            @RequestParam Long userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastSeenDate) {
        return feedService.getFeed(userId, lastSeenDate);
    }

    @PostMapping("/start")
    public void startCacheWarmup() {
        log.info("Received request to start cache warmup.");
        feedHeaterService.startCacheWarmup();
    }
}
