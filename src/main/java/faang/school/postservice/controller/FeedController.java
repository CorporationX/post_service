package faang.school.postservice.controller;

import faang.school.postservice.dto.post.FeedPostDto;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/feed")
public class FeedController {
    private final PostService postService;

    @GetMapping("/user/{userId}/posts/after/{afterPostId}")
    public ResponseEntity<List<FeedPostDto>> getFeed(@PathVariable Long userId,
                                                     @PathVariable Long afterPostId) {

        log.info("Received request to get posts for user with ID {} starting from post {}",userId, afterPostId);
        return ResponseEntity.ok(postService.getFeed(userId, afterPostId));
    }
}
