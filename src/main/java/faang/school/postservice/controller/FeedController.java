package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.NewsFeedPostDto;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Alexander Bulgakov
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/feed")
public class FeedController {

    private final UserContext userContext;
    private final FeedService feedService;

    @GetMapping
    public List<NewsFeedPostDto> getFeed(@RequestParam(required = false) Long postId) {
        long currentUserId = userContext.getUserId();
        return feedService.getPostsForUser(currentUserId, postId);
    }

    @PostMapping("/heat")
    public ResponseEntity<String> heatCache() {
        feedService.heatCache();
        return ResponseEntity.ok("Cache heating started");
    }
}
