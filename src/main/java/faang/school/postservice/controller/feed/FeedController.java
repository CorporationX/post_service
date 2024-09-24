package faang.school.postservice.controller.feed;

import faang.school.postservice.dto.post.PostFeedDto;
import faang.school.postservice.service.feed.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.TreeSet;

@RestController
@RequestMapping("/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    @GetMapping("/{userId}")
    public ResponseEntity<TreeSet<PostFeedDto>> getNewsFeed(@PathVariable("userId") long userId,
                                                            @RequestParam(value = "postId", required = false) Long postId){
        return ResponseEntity.status(HttpStatus.OK).body(feedService.getNewsFeed(postId, userId));
    }
}
