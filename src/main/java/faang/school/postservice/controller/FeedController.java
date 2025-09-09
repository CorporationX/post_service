package faang.school.postservice.controller;

import faang.school.postservice.dto.feed.FeedRequest;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.feed.NewsFeedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/feed")
@RequiredArgsConstructor
@Tag(name = "Feed", description = "Operations related to news feed")
public class FeedController {
    private final NewsFeedService service;

    @Operation(summary = "Get news feed for user")
    @GetMapping
    public List<PostDto> get(@ParameterObject @Valid FeedRequest request) {
        return service.getUserFeed(request);
    }
}
