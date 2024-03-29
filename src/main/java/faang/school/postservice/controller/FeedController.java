package faang.school.postservice.controller;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostFeedDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.service.EventKafkaService;
import faang.school.postservice.service.FeedHeaterService;
import faang.school.postservice.service.FeedService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.TreeSet;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/feed")
public class FeedController {
    private final FeedService feedService;
    private final UserContext userContext;
    private final UserServiceClient userServiceClient;
    private final EventKafkaService eventKafkaService;

    @GetMapping()
    public TreeSet<PostFeedDto> getFeed(@RequestParam(required = false) Long postId) {
        long userId = userContext.getUserId();
        validateUser(userId);
        return feedService.getFeed(userId, postId);
    }

    @PostMapping("/heat")
    public void heatFeed() {
        eventKafkaService.sendHeatFeedEvent();
    }

    private void validateUser(long userId) {
        if (!userServiceClient.isUserExists(userId)) {
            throw new EntityNotFoundException("User with Id - " + userId + " does not exist!");
        }
    }
}