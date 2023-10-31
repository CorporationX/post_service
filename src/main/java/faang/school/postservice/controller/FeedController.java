package faang.school.postservice.controller;

import faang.school.postservice.dto.client.UserDto;
import faang.school.postservice.dto.feed.FeedDto;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/feed")
public class FeedController {
    private final FeedService feedService;

    @GetMapping
    public List<FeedDto> getFeed(Long postId) {
        return feedService.getFeed(postId);
    }

    @PostMapping("/start")
    public void startFeed(UserDto userDto) {
        feedService.heatFeed(userDto);
    }
}
