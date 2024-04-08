package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.FeedDto;
import faang.school.postservice.service.FeedService;
import faang.school.postservice.service.hash.FeedHeater;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/feed")
public class FeedController {
    private final FeedHeater feedHeater;
    private final UserContext userContext;
    private final FeedService feedService;

    @PostMapping("/heat")
    public void create() {
        feedHeater.feedHeat();
    }

    @GetMapping("/start")
    public List<FeedDto> getFeed(@RequestParam(required = false) Long postId) {
        return feedService.getFeed(userContext.getUserId(), postId);
    }
}
