package faang.school.postservice.controller.feed;

import faang.school.postservice.redis.model.dto.FeedDto;
import faang.school.postservice.service.FeedService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping
@Validated
@Tag(name = "Feed Controller", description = "The controller is used to receive feeds")
public class FeedController {
    private final FeedService feedService;

    @GetMapping("/{id}")
    @Operation(summary = "Get feed by ID", description = "Retrieve feed by its ID.")
    @ApiResponse(responseCode = "200", description = "Feed retrieved successfully")
    @ApiResponse(responseCode = "500", description = "Failed to retrieve feed"
    )
    public FeedDto getAlbumById(

            @Parameter(description = "ID of the user", required = true)
            @RequestHeader("x-user-id") Long userId,
            @Positive @RequestParam(required = false) Integer startPostId,
            @Positive @PathVariable Long id) {
        return feedService.getFeed(id, userId, startPostId);
    }
}
