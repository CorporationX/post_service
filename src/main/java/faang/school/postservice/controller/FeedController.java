package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.feed_dto.PostForFeedDto;
import faang.school.postservice.service.cash.FeedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.TreeSet;


@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/feed")
@Tag(
        name = "News Feed",
        description = "News Feed API"
)
public class FeedController {
    private final FeedService feedService;
    private final UserContext userContext;

    @GetMapping
    @Operation(summary = "Get news feed", parameters = {@Parameter(in = ParameterIn.HEADER,
            name = "x-user-id", description = "user Id", required = true)})
    public TreeSet<PostForFeedDto> getFeed(@RequestParam(name = "afterId", required = false) @Min(1) Long afterId) {
        long currentUserId = userContext.getUserId();
        return feedService.getFeed(currentUserId, afterId);
    }
}
