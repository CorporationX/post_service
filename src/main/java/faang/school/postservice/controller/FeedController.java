package faang.school.postservice.controller;

import faang.school.postservice.config.FeedControllerProperties;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.feed.FeedPostResponseDto;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;
    private final UserContext userContext;
    private final FeedControllerProperties props;

    @GetMapping
    public List<FeedPostResponseDto> getFeed(
            @RequestParam(name = "after", required = false) Long afterPostId
    ) {
        return feedService.getFeed(
                userContext.getUserId(),
                afterPostId,
                props.defaultLimit()
        );
    }
}