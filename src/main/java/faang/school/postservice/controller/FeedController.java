package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.PostForFeed;
import faang.school.postservice.service.feed.FeedHeater;
import faang.school.postservice.service.feed.FeedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "FeedController", description = "Request news feed")
@RequestMapping("${server.version}")
@RequiredArgsConstructor
@RestController
public class FeedController {

    private final FeedService feedService;
    private final UserContext userContext;
    private final FeedHeater feedHeater;

    @Operation(summary = "Запрос ленты новостей")
    @GetMapping("/feed")
    public List<PostForFeed> getPostForFeed(
            @RequestParam(required = false) Long lastPostId) {
        return feedService.getFeed(userContext.getUserId(), lastPostId);
    }

    @Operation(summary = "Запускаем кэш в фоновом режиме")
    @PostMapping("/heat")
    private void heatFeed() {
        feedHeater.feedHeatProducer();
    }
}
