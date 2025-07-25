package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feed")
public class FeedController {

    private final FeedService feedService;

    @GetMapping
    public List<PostResponseDto> feed(@RequestParam(required = false) Long postId) {
        return feedService.getFeed(postId);
    }

}
