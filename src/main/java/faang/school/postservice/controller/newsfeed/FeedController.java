package faang.school.postservice.controller.newsfeed;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.newsfeed.FeedDto;
import faang.school.postservice.service.newsfeed.FeedService;
import faang.school.postservice.utils.FeedHeater;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/feed")
@RequiredArgsConstructor
@Slf4j
public class FeedController {

    private final UserContext userContext;
    private final FeedService feedService;
    private final FeedHeater feedHeater;

    @GetMapping
    public FeedDto getFeed(@RequestParam(value = "postId", required = false) Long postId){
        log.info("Received a request to fetch the feed");
        Long currentUser = userContext.getUserId();
        String postIdsStr = (postId != null) ? postId.toString() : null;
        return feedService.getFeed(currentUser.toString(), postIdsStr);
    }

    @PostMapping("/heat")
    public void heatCacheForSpecificUsers(@RequestBody List<Long> userIds) {
        log.info("Received a request to heat the cache for specific users");
        feedHeater.heatCacheForSpecificUsers(userIds);
    }
}
