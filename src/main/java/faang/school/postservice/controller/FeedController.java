package faang.school.postservice.controller;

import faang.school.postservice.dto.FeedDto;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/feed")
@RequiredArgsConstructor
@Slf4j
public class FeedController {

    private final FeedService feedService;

    @GetMapping
    public List<FeedDto> getFeed(@RequestParam(value = "postId", required = false) Long postId) {
        log.info("Received request to FeedController to get feed from post with id={}", postId);
        return feedService.getFeed(postId);
    }

    @PostMapping("/heat")
    public void heatFeed() {
        feedService.heatFeed();
    }
}
