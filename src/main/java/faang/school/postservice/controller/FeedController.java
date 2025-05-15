package faang.school.postservice.controller;

import faang.school.postservice.dto.comment.FeedCommentDto;
import faang.school.postservice.dto.post.FeedPostDto;
import faang.school.postservice.service.feed.FeedService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/v1/feed")
@RequiredArgsConstructor
public class FeedController {
    private final FeedService feedService;

    @GetMapping("/posts/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public Page<FeedPostDto> getFeedPosts(@PathVariable @Valid @NotNull Long userId,
                                          @RequestParam @Valid @PositiveOrZero int offset) {

        log.info("Received request to get post feed from post {} for user with ID: {}", offset, userId);
        return feedService.getFeedPosts(userId, offset);
    }

    @GetMapping("/comments/post/{postId}")
    @ResponseStatus(HttpStatus.OK)
    public Page<FeedCommentDto> getFeedComments(@PathVariable @Valid @NotNull Long postId,
                                                @RequestParam @Valid @PositiveOrZero int offset) {

        log.info("Received request to get comment feed from comment {} for post with ID: {}", offset, postId);
        return feedService.getFeedComments(postId, offset);
    }

    @PostMapping("/heat")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void heatFeedCache() {
        log.info("Received request to start head cache for feed");
        feedService.heatFeedCache();
    }
}
