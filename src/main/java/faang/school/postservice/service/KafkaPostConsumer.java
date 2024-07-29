package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaPostConsumer {

    private final RedisCacheService redisCacheService;

    @KafkaListener(topics = "posts", groupId = "group_1", containerFactory = "kafkaListenerContainerFactory")
    public void listen(PostEvent message, Acknowledgment acknowledgment) {

        System.out.println(message);
        redisCacheService.addPostToFeed(message);
        acknowledgment.acknowledge();
    }
}
