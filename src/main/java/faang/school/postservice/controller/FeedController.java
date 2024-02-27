package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.hash.FeedDto;
import faang.school.postservice.service.hash.FeedHashService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feed")
public class FeedController {
    private final FeedHashService feedService;
    private final UserContext userContext;

    @Operation(summary = "Get feed", parameters = {@Parameter(in = ParameterIn.HEADER, name = "x-user-id", description = "User ID", required = true)})
    @GetMapping
    public FeedDto getFeed(@RequestParam(required = false) Long lastPostId) {
        long userId = userContext.getUserId();
        return feedService.getFeed(userId, lastPostId);
    }
}
