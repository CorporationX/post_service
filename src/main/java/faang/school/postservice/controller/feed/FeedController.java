package faang.school.postservice.controller.feed;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostForFeedDto;
import faang.school.postservice.service.feed.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("feed")
@RequiredArgsConstructor
public class FeedController {
    private final FeedService feedService;
    private final UserContext userContext;


    @GetMapping
    public List<PostForFeedDto> getFeed(@RequestParam(required = false) Long lastViewedPostId) {
        //TODO: testing

        Long userId = userContext.getUserId();
        if (lastViewedPostId != null) {
            return feedService.getFeed(userId, lastViewedPostId);
        }

        return feedService.getFeed(userId);
    }
}
