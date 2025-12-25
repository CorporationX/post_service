package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostFeedDto;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/feed")
public class FeedController {
    private final FeedService feedService;
    private final UserContext userContext;

    @Value("${app.feed.default-size:20}")
    private int defaultSize;

    @GetMapping
    public List<PostFeedDto> getFeed(
            @RequestParam(required = false) Long after,
            @RequestParam(required = false) Integer size
    ) {
        long userId = userContext.getUserId();
        return feedService.getUserFeed(userId, after, size);
    }
}
