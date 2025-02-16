package faang.school.postservice.controller;

import faang.school.event.Event;
import faang.school.event.UserBanEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class KafkaTestController {
    private final KafkaTemplate<String, Event> kafkaTemplate;

    @Value("${spring.kafka.topics.user-ban-topic.name}")
    private String userBanTopicName;

    @GetMapping("/ban")
    public ResponseEntity<Void> test() {

        Event event = UserBanEvent.builder()
                .userId(1)
                .banned(true)
                .build();

        kafkaTemplate.send(userBanTopicName, event);

        return ResponseEntity.ok().build();
    }
}
