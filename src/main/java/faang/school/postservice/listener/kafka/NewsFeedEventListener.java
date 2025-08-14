package faang.school.postservice.listener.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.NewsFeedEventDto;
import faang.school.postservice.listener.AbstractEventListener;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.redis.NewsFeedRedisEntity;
import faang.school.postservice.repository.redis.impl.NewsFeedRedisRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class NewsFeedEventListener extends AbstractEventListener<NewsFeedEventDto> {

    public static final String ERROR = "newsFeed event error: {}";

    private final NewsFeedRedisRepository newsFeedRedisRepository;
    private final PostMapper postMapper;

    public NewsFeedEventListener(
        ObjectMapper objectMapper,
        NewsFeedRedisRepository newsFeedRedisRepository,
        PostMapper postMapper
    ) {
        super(objectMapper);
        this.newsFeedRedisRepository = newsFeedRedisRepository;
        this.postMapper = postMapper;
    }

    @Override
    public String getJsonProcessingExceptionText() {
        return ERROR;
    }

    @KafkaListener(
        topics = "${spring.kafka.topics.newsFeedTopic}",
        groupId = "#{'${spring.kafka.consumer.group-id}'}",
        containerFactory = "kafkaManualAckContainerFactory"
    )
    public void consume(String message, Acknowledgment ack) {
        try {
            NewsFeedEventDto eventDto = getEventDto(message, NewsFeedEventDto.class);
            log.debug("resieve message: {}", message);
            List<Long> followersId = eventDto.getFollowersId();

            followersId.forEach(followerId -> {
                NewsFeedRedisEntity feed = postMapper.toNewsFeedRedisEntity(eventDto, followerId);
                newsFeedRedisRepository.save(feed);
            });
            ack.acknowledge();
        } catch (Exception e) {
            log.error(ERROR, e.getMessage(), e);
        }

    }
}
