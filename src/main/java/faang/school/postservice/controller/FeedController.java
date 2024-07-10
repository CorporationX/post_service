package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.FeedHeaterService;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.TreeSet;

@RestController
@RequestMapping("/feed")
@RequiredArgsConstructor
public class FeedController {

    private final UserContext userContext;
    private final FeedService feedService;
    private final FeedHeaterService feedHeaterService;

    @GetMapping
    public TreeSet<PostDto> getNewsFeed(@RequestParam(value = "postId", required = false) Long postId) {
        long userId = userContext.getUserId();
        return feedService.getNewsFeed(postId, userId);
    }

    @PostMapping("/heat")
    public void heatFeed() {
        feedHeaterService.feedHeat();
    }
}
