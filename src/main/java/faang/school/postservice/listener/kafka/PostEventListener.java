package faang.school.postservice.listener.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.event.NewsFeedEventDto;
import faang.school.postservice.dto.event.PostEventDto;
import faang.school.postservice.listener.AbstractEventListener;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.redis.PostRedisEntity;
import faang.school.postservice.model.redis.UserRedisEntity;
import faang.school.postservice.producer.EventProducer;
import faang.school.postservice.repository.redis.impl.PostRedisRepository;
import faang.school.postservice.repository.redis.impl.UserRedisRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Component
public class PostEventListener extends AbstractEventListener<PostEventDto> {

    public static final String ERROR = "post event error: {}";

    private final UserRedisRepository userRedisRepository;
    private final PostRedisRepository postRedisRepository;
    private final UserServiceClient userServiceClient;
    private final EventProducer<NewsFeedEventDto> eventProducer;
    private final UserContext userContext;
    private final PostMapper postMapper;

    @Value("${application.followers.chunk-size:10}")
    private int followersChunkSize;

    public PostEventListener(
        ObjectMapper objectMapper,
        UserRedisRepository userRedisRepository,
        PostRedisRepository postRedisRepository,
        UserServiceClient userServiceClient,
        EventProducer<NewsFeedEventDto> eventProducer,
        UserContext userContext, PostMapper postMapper
    ) {
        super(objectMapper);
        this.userRedisRepository = userRedisRepository;
        this.postRedisRepository = postRedisRepository;
        this.userServiceClient = userServiceClient;
        this.eventProducer = eventProducer;
        this.userContext = userContext;
        this.postMapper = postMapper;
    }

    @KafkaListener(
        topics = "${spring.kafka.topics.postTopic}",
        groupId = "#{'${spring.kafka.consumer.group-id}'}"
    )
    public void consume(String message) {
        PostEventDto eventDto = getEventDto(message, PostEventDto.class);
        log.debug("resieve message: {}", message);

        UserRedisEntity userRedisEntity = postMapper.toUserRedisEntity(eventDto);
        log.debug("save author info into redis. author is: {}", userRedisEntity);
        userRedisRepository.save(userRedisEntity);

        PostRedisEntity postRedisEntity = postMapper.toPostRedisEntity(eventDto);
        log.debug("save post info into redis. post is: {}", postRedisEntity);
        postRedisRepository.save(postRedisEntity);

        /*
         * todo: получение подписчиков сделано ИМЕННО ЗДЕСЬ вместо того,
         *  чтобы делать обращение к UserService в сессии PostController'а.
         *  Причина: сокращение времени отклика при создании нового поста.
         */
        log.debug("get a list of the author's subscribers! call findFollowersIdByFolloweeId(). AuthorId is: {}",
            eventDto.getAuthorId());
        userContext.setUserId(eventDto.getAuthorId());
        List<Long> userFollowers = userServiceClient.findFollowersIdByFolloweeId(eventDto.getAuthorId());
        log.debug("followers in redis: {}", userFollowers);
        List<List<Long>> userFollowersPartition = splitIntoChunks(userFollowers, followersChunkSize);
        log.debug("partitions in redis: {}", userFollowersPartition);

        userFollowersPartition.forEach(usersList -> {
            NewsFeedEventDto newsFeedEventDto = postMapper.toNewsFeedEventDto(eventDto, usersList);
            eventProducer.publish(newsFeedEventDto);
        });
    }

    @Override
    public String getJsonProcessingExceptionText() {
        return ERROR;
    }

    private List<List<Long>> splitIntoChunks(List<Long> longList, int chunkSize) {
        log.debug("split into max range {}", (longList.size() + chunkSize - 1) / chunkSize);
        return IntStream.range(0, (longList.size() + chunkSize - 1) / chunkSize)
            .mapToObj(i -> longList.subList(i * chunkSize,
                Math.min((i + 1) * chunkSize, longList.size())))
            .collect(Collectors.toList());
    }
}
