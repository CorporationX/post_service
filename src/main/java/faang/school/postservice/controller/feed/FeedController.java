package faang.school.postservice.controller.feed;


import faang.school.postservice.dto.PostDto;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import faang.school.postservice.service.feed.FeedService;

import java.util.List;

@RestController
@RequestMapping("/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    @GetMapping
    public List<PostDto> getFeed(@RequestParam(required = false) Long lastPostId) {
        return feedService.getUserFeed(lastPostId);
    }
}
