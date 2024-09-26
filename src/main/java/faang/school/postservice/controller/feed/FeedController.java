package faang.school.postservice.controller.feed;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.FeedDto;
import faang.school.postservice.dto.FeedPostDto;
import faang.school.postservice.service.FeedHeater;
import faang.school.postservice.service.redis.FeedCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.TreeSet;

@RestController
@RequiredArgsConstructor
@Slf4j
public class FeedController {

    private final UserContext userContext;
    private final FeedCacheService feedService;
    private final FeedHeater feedHeater;

    @GetMapping("/feed")
    public TreeSet<FeedDto> loadPosts(@RequestBody(required = false) FeedPostDto dto) {
        Long userId = userContext.getUserId();
        Set<Long> postsIds = feedService.getPostsIdsForUser(userId, dto);
        return feedService.getFeedDtoResponse(postsIds);
    }

    @GetMapping("/heat")
    public void startCacheHeating(){
        feedHeater.generateFeed();
    }
}
