package faang.school.postservice.controller.feed;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.FeedPostDto;
import faang.school.postservice.service.feed.FeedService;
import faang.school.postservice.service.feed.warmup.FeedWarmupStarter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/feed")
@Tag(name = "Feed", description = "News feed operations")
public class FeedController {

    private final FeedService feedService;
    private final UserContext userContext;
    private final FeedWarmupStarter feedWarmupStarter;

    @Operation(summary = "Get news feed for current user")
    @GetMapping
    public List<FeedPostDto> getFeed(@RequestParam(name = "after", required = false) Long afterPostId) {
        return feedService.getFeedPage(userContext.getUserId(), afterPostId);
    }

    @Operation(summary = "Kick off feed cache warm-up")
    @PostMapping("/heat")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void heat() {
        feedWarmupStarter.startWarmup();
    }
}
