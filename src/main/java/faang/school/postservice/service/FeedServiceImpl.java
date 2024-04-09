package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.FeedDto;
import faang.school.postservice.dto.kafka.KafkaPostEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private final UserServiceClient userServiceClient;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Page<FeedDto> getFeed(Long postId) {
        Long userId = userServiceClient.getUser(postId).getId();

        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID is missing");
        }

        return null;
    }

    public void addToFeed(KafkaPostEvent kafkaPostEvent, Acknowledgment acknowledgment) {

    }

}
