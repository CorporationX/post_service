package faang.school.postservice.controller;

import faang.school.postservice.cash.NewsFeed;
import faang.school.postservice.service.NewsFeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/newsfeed")
@Slf4j
public class NewsFeedController {
    private final NewsFeedService newsFeedService;

    @GetMapping("/{userId}")
    @ResponseBody
    public NewsFeed getNewsFeed(@PathVariable Long userId) {
        return newsFeedService.getNewsFeed(userId);
    }
}
