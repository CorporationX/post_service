package faang.school.postservice.controller.feed;

import faang.school.postservice.producer.FeedHeatProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/feed")
@RequiredArgsConstructor
@Slf4j
public class FeedHeatController {

    private final FeedHeatProducer feedHeatProducer;

    /**
     * Эндпоинт для запуска прогрева кэша фида.
     * Запускает асинхронную задачу, которая не блокирует HTTP поток.
     * Защищен от повторных запусков.
     * 
     * @return 202 Accepted - задача принята и выполняется в фоне
     * @return 409 Conflict - прогрев уже выполняется
     */
    @PostMapping("/heat")
    public ResponseEntity<Map<String, String>> heatFeed() {
        log.info("Received feed heating request");

        if (feedHeatProducer.isHeating()) {
            log.warn("Feed heating already in progress, rejecting request");
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of(
                    "status", "error",
                    "message", "Feed heating already in progress"
                ));
        }
        
        try {
            feedHeatProducer.sendFeedHeatEvent();
            
            return ResponseEntity.accepted()
                .body(Map.of(
                    "status", "accepted",
                    "message", "Feed heating started. Processing will be distributed across available servers. " +
                               "Check /heat/status for progress."
                ));
        } catch (Exception e) {
            log.error("Error starting feed heating", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "status", "error",
                    "message", "Error starting feed heating: " + e.getMessage()
                ));
        }
    }

    /**
     * Проверяет статус прогрева фида.
     * 
     * @return 200 OK с информацией о статусе прогрева
     */
    @GetMapping("/heat/status")
    public ResponseEntity<Map<String, Object>> getHeatStatus() {
        return ResponseEntity.ok(Map.of(
            "isHeating", feedHeatProducer.isHeating()
        ));
    }
}

