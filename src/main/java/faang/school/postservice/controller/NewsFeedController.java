package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.news.feed.NewsFeed;
import faang.school.postservice.service.NewsFeedService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/news/feed")
@RequiredArgsConstructor
public class NewsFeedController {

    private final UserContext userContext;
    private final NewsFeedService newsFeedService;

    @GetMapping
    public NewsFeed getNewsFeed(@RequestParam(required = false) @Positive Long firstPostId) {
        long userId = userContext.getUserId();
        return Optional.ofNullable(firstPostId)
                .map(postId -> newsFeedService.getNewsFeedBy(userId, postId))
                .orElseGet(() -> newsFeedService.getNewsFeedBy(userId));
    }
}
