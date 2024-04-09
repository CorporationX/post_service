package faang.school.postservice.kafka.producer;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.kafka.KafkaLikeEvent;
import faang.school.postservice.dto.user.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaLikeProducer extends AbstractKafkaProducer<LikeDto> {

    private final UserServiceClient userServiceClient;

    @Value("${spring.kafka.topics.like.name}")
    private String likeTopic;

    public void publishKafkaLikeEvent(LikeDto like) {
        UserDto author = userServiceClient.getUser(like.getAuthorId());
        KafkaLikeEvent kafkaLikeEvent = new KafkaLikeEvent(like, author);
        publishKafkaEvent(kafkaLikeEvent.getLike(), likeTopic);
    }
    
}