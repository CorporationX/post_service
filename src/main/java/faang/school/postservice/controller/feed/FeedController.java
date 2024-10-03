package faang.school.postservice.controller.feed;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostForFeedDto;
import faang.school.postservice.service.feed.FeedService;
import faang.school.postservice.service.feed.HeatFeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/feeds")
@RequiredArgsConstructor
@Slf4j
public class FeedController {

    private final UserContext userContext;
    private final FeedService feedService;
    private final HeatFeedService heatFeedService;

    @GetMapping
    public List<PostForFeedDto> getFeed(@RequestParam(value = "lastPostId", required = false) Long lastPostId) {
        long userId = userContext.getUserId();
        return feedService.getFeed(userId, lastPostId);
    }

    @PostMapping("/heat")
    public void feedHeater() {
        heatFeedService.heatFeed();
        log.info("The user feed started to heat up!");
    }

}
