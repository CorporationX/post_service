package faang.school.postservice.service.kafka;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.kafka.event.PostEventDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.subscription.SubscriptionUserIdDto;
import faang.school.postservice.producer.KafkaPostProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostEventService {
    private final KafkaPostProducer producer;
    private final UserServiceClient userServiceClient;

    @Value("${spring.data.kafka.producer.producers.post.limit-amount-user-ids}")
    private final int limitAmountUserIds;

    @Async("kafkaProducerExecutor")
    public void produce(PostDto postDto) {
        Long followeeId = postDto.authorId();
        if (followeeId == null) {
            return;
        }

        List<SubscriptionUserIdDto> followerDtos;
        long lastId = 0L;
        do {
            followerDtos = userServiceClient.getFollowerIds(followeeId, lastId, limitAmountUserIds);
            if (followerDtos.isEmpty()) {
                break;
            }
            List<Long> followerIds = followerDtos.stream()
                .map(SubscriptionUserIdDto::followerId)
                .toList();
            lastId = followerIds.get(followerIds.size() - 1);

            PostEventDto eventDto = createPostEventDto(followeeId, postDto, followerIds);
            producer.send(eventDto);
        } while(!followerDtos.isEmpty());
    }

    private PostEventDto createPostEventDto(Long followeeId, PostDto postDto, List<Long> followerIds) {
        return PostEventDto.builder()
            .authorId(followeeId)
            .postId(postDto.id())
            .followerIds(followerIds)
            .build();
    }
}
