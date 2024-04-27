package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.PostForFeed;
import faang.school.postservice.service.FeedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "FeedController", description = "Request news feed")
@RequestMapping("${server.version}/feed")
@RequiredArgsConstructor
@RestController
public class FeedController {

    private final FeedService feedService;
    private final UserContext userContext;

    @Operation(summary = "Запрос ленты новостей")
    @GetMapping
    public List<PostForFeed> getPostForFeed(
            @RequestParam(required = false) Long lastPostId) {
        return feedService.getFeed(userContext.getUserId(), lastPostId);
    }
}
