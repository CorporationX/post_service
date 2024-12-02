package faang.school.postservice.controller.feed;

import faang.school.postservice.publisher.kafka.KafkaHeatTaskProducer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Cache Heat", description = "Operations for cache heating process")
public class FeedHeatController {

    private final KafkaHeatTaskProducer heatTaskProducer;

    @Operation(
            summary = "Start the cache heating process",
            description = "Publishes heat tasks to Kafka to process the cache heating for the users."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Cache heating process started successfully"
    )
    @ApiResponse(
            responseCode = "500",
            description = "Failed to start cache heating process"
    )
    @GetMapping("/heat")
    public ResponseEntity<String> heatCache() {
        try {
            heatTaskProducer.publishHeatTasks();
            return ResponseEntity.ok("Cache heating process started successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to start cache heating process: " + e.getMessage());
        }
    }
}
