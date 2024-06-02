package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.service.FeedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feed")
@Validated
public class FeedController {
    private final FeedService feedService;
    private final UserContext userContext;

    @GetMapping
    @Operation(summary = "Get news feed", parameters = {@Parameter(in = ParameterIn.HEADER,
            name = "x-user-id", description = "userId", required = true)})
    public List<Long> getFeed(@RequestParam(name = "afterId", required = false) @Min(1) Long afterId) {
        return feedService.getNext20PostIds(userContext.getUserId(), afterId);
    }
}
