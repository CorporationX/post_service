package faang.school.postservice.controller;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/feed")
@RequiredArgsConstructor
public class FeedController {
    private final FeedService feedService;

    @GetMapping("/")
    public List<PostDto> getFeed(@RequestParam(required = false) Long lastId) {
        return feedService.getFeed(lastId);
    }
}
