package faang.school.postservice.controller;

import faang.school.postservice.dto.feed.FeedDto;
import faang.school.postservice.service.FeedService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/feed")
public class FeedController {

    public final FeedService feedService;

    @GetMapping("/")
    public FeedDto getFeed(@RequestParam(required = false) Long postId) {
        return feedService.getFeed(postId);
    }
}
