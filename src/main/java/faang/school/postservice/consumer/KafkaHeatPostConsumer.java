package faang.school.postservice.consumer;

import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.FolloweeSumDto;
import faang.school.postservice.dto.kafka.KafkaHeatFeedSizeDto;
import faang.school.postservice.mapper.redis.RedisPostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.RedisPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaHeatPostConsumer {
    private final UserContext userContext;
    private final UserServiceClient userServiceClient;
    private final RedisPostRepository redisPostRepository;
    private final RedisPostMapper redisPostMapper;
    private final PostRepository postRepository;

    @KafkaListener(topics = "${kafka.topics.heatPostRequest}", groupId = "my-consumer-group")
    @Transactional(readOnly = true)
    public void counsumePostEvent(KafkaHeatFeedSizeDto kafkaHeatFeedSizeDto) {
        userContext.setUserId(1);

        // getting the portion of users (subscribers)
        List<FolloweeSumDto> authorIds = userServiceClient.getFolloweesPaged(
            kafkaHeatFeedSizeDto.getPageNumber(),
            kafkaHeatFeedSizeDto.getPageSize()
        );
        log.info("Received the batch of {} subscribers.", authorIds.size());

        for (FolloweeSumDto followeeSumDto : authorIds) {
            List<Post> posts = postRepository.findByAuthorId(followeeSumDto.getFolloweeId());
            redisPostRepository.saveAll(redisPostMapper.toRedisPosts(posts));
        }
    }
}
