package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.hash.FeedPretty;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.hash.FeedHashService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/feed")
public class FeedController {
    private final FeedHashService feedService;
    private final UserContext userContext;

    @Operation(summary = "Get feed", parameters = {@Parameter(in = ParameterIn.HEADER, name = "x-user-id", description = "User ID", required = true)})
    @GetMapping
    public FeedPretty getFeed(@RequestParam(required = false) Long lastPostId) {
        try {
            return feedService.getFeed(userContext.getUserId(), Optional.ofNullable(lastPostId)).get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new DataValidationException("Не удалось получить пользователя");
        }
    }

    @PostMapping("/heat")
    public void heatCache() {
       // messageBroker.publish("cacheHeatStart", new HeatCacheMessage());
    }
}
