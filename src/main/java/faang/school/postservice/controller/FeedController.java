package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.feed.FeedPublicationDto;
import faang.school.postservice.service.feed.FeedService;
import faang.school.postservice.service.feed.heater.FeedHeaterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/feed")
@RequiredArgsConstructor
@Tag(name = "Feed Controller")
public class FeedController {

    private final UserContext userContext;
    private final FeedService feedService;
    private final FeedHeaterService feedHeaterService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get news feed page for user")
    @Parameter(in = ParameterIn.HEADER, name = "x-user-id", required = true)
    public List<FeedPublicationDto> getFeed(Pageable pageable) {

        long userId = userContext.getUserId();
        return feedService.getNewsFeed(userId, pageable);
    }

    @PostMapping("/heat")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Heat feed cache")
    public void getFeed() {

        feedHeaterService.heatUp();
    }
}
