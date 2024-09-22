package faang.school.postservice.controller;

import faang.school.postservice.dto.feed.FeedDto;
import faang.school.postservice.service.feed.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feeds")
public class FeedController {
    private final FeedService feedService;

    @GetMapping("/{afterPostId}")
    public FeedDto getFeed(@PathVariable Long afterPostId) {
        return feedService.getFeed(afterPostId);
    }

    @GetMapping("/postToUserFeed/{postId}/{userId}")
    public void addPostToUserFeed(@PathVariable Long postId, @PathVariable Long userId) {
        feedService.saveFeed(userId, postId);
    }
}
