package faang.school.postservice.listener;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.repository.UserRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthorResponseEventListener {

    private final AbstractEventListener abstractEventListener;
    private final UserRedisRepository userRedisRepository;

    @KafkaListener(
            topics = "${spring.data.kafka.topic.author-response.name}",
            groupId = "${spring.data.kafka.consumer.group-id}"
    )
    public void receive(String message, Acknowledgment ack) {
        abstractEventListener.receiveAndHandle(message, UserDto.class, userRedisRepository::saveUser, ack);
    }
}
