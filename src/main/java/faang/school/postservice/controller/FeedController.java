package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostForFeedDto;
import faang.school.postservice.service.FeedService;
import faang.school.postservice.service.feedheater.FeedHeater;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController("api/v1/")
@RequiredArgsConstructor
public class FeedController {
    private final FeedHeater feedHeater;
    private final FeedService feedService;
    private final UserContext userContext;

    @PutMapping("heat")
    public void heat() {
        feedHeater.heat();
    }


    @GetMapping("/feed")
    public List<PostForFeedDto> getFeed(@RequestParam(value = "postId", required = false) Long latestPostId)
            throws ExecutionException, InterruptedException {
        return feedService.getFeed(userContext.getUserId(), latestPostId);
    }
}
