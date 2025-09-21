package faang.school.postservice.controller.feed;

import faang.school.postservice.dto.feed.FeedPostDto;
import faang.school.postservice.service.feed.FeedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/feed")
@RequiredArgsConstructor
@Validated
@Tag(name = "Feed", description = "Feed operations")
public class FeedController {

    private final FeedService feedService;

    @Operation(
            summary = "Kick off feed cache warmup"
    )
    @PostMapping("/heat")
    public void heat() {
        feedService.queueCacheWarmUp();
    }

    @GetMapping
    public List<FeedPostDto> getFeed(@RequestParam @Nullable Long lastPostId) {
        return feedService.getFeed(lastPostId);
    }

}
