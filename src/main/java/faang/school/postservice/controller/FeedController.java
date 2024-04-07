package faang.school.postservice.controller;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/feeds")
@RequiredArgsConstructor
@Validated
public class FeedController {
    private final FeedService feedService;

    @GetMapping
    public List<PostDto> getFeed (@RequestParam(required = false) Long postId) {
        feedService.getFeed(postId);
    }

}
