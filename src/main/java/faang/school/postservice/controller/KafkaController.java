package faang.school.postservice.controller;

import faang.school.postservice.producer.KafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/kafka")
public class KafkaController {
    /**
     * Класс для тестирования отправки информации в kafka
     */
    private final KafkaProducer kafkaProducer;

    @PostMapping("/{topicName}")
    @ResponseStatus(HttpStatus.OK)
    public void sendKafka(
        @PathVariable(value = "topicName") String topic,
        @RequestBody String text
    ) {
        log.debug("sendKafka text:\n{}", text);
        kafkaProducer.sendMessage(topic, text);
    }
}
