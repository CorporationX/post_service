package faang.school.postservice.service.kafka;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.event.post.PostEventDto;
import faang.school.postservice.publisher.kafka.post.PostEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class KafkaPostService {

    private final UserServiceClient userServiceClient;
    private final PostEventPublisher postEventPublisher;

    public void sendPostToKafka(){
        Set<Long> followers = userServiceClient.getFollowersById();
        PostEventDto postEventDto = PostEventDto.builder()
                .authorSubscriberIds()
                .build();
        postEventPublisher.publish(postEventDto);
    }
}
