package faang.school.postservice.controller;

import faang.school.postservice.dto.feed.RequestFeedDto;
import faang.school.postservice.dto.post.PostFeedDto;
import faang.school.postservice.service.redis.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.TreeSet;

@RestController
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    @PostMapping("/feed")
    public TreeSet<PostFeedDto> getPostBatch(@RequestBody RequestFeedDto requestFeedDto) {
        return feedService.getPostFeedDtos(requestFeedDto);
    }
}
