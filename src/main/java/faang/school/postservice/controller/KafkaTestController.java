package faang.school.postservice.controller;

import faang.school.postservice.dto.like.LikeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.kafka.core.KafkaTemplate;

@RestController
@RequiredArgsConstructor
public class KafkaTestController {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @PostMapping("/test-kafka")
    public void testKafka(@RequestBody LikeEvent event) {
        kafkaTemplate.send("test-topic", event);
    }
}
