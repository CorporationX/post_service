package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.redis.cache.service.feed.FeedCacheService;
import faang.school.postservice.service.feed.FeedHeaterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/feed")
@RequiredArgsConstructor
@Tag(name = "Feed Controller")
public class FeedController {
    private final FeedCacheService feedCacheService;
    private final FeedHeaterService feedHeaterService;
    private final UserContext userContext;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get news feed page for user")
    @Parameter(in = ParameterIn.HEADER, name = "x-user-id", required = true)
    public List<PostDto> getUserFeed(@Nullable @RequestParam(value = "postId", required = false) Long postId) {
        Long userId = userContext.getUserId();
        return feedCacheService.getFeedByUserId(postId, userId);
    }

    @PostMapping("/heat")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Heat feed cache")
    public void sendHeatEventsAsync() {
        feedHeaterService.heatUp();
    }
}
