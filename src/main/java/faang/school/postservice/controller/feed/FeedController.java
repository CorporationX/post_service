package faang.school.postservice.controller.feed;

import faang.school.postservice.dto.feed.PostFeedDto;
import faang.school.postservice.service.feed.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST-контроллер для получения feed пользователей.
 *
 * @author Linempy
 * @since 27.09.2025
 */
@RestController
@RequestMapping("/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService service;

    @GetMapping
    public ResponseEntity<List<PostFeedDto>> getFeed(@RequestParam(value = "id", required = false) Long lastPostId) {
        List<PostFeedDto> feed = service.getFeed(lastPostId);
        return ResponseEntity.ok(feed);
    }
}