package faang.school.postservice.broker.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.config.kafka.CustomKafkaProperties;
import faang.school.postservice.dto.post.PostProcessEvent;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.subscription.SubscriptionUserDto;
import faang.school.postservice.mapper.user.UserMapper;
import faang.school.postservice.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class PostProcessEventProducer extends KafkaProducerService {
    private final UserService userService;
    private final UserContext userContext;

    public PostProcessEventProducer(KafkaTemplate<String, PostProcessEvent> kafkaTemplate,
                                    CustomKafkaProperties customKafkaProperties,
                                    ObjectMapper objectMapper,
                                    UserMapper userMapper,
                                    UserContext userContext,
                                    UserService userService,
                                    @Value("${spring.kafka.topic.posts-process-topic}") String topic) {
        super(kafkaTemplate, objectMapper, topic);
        this.userService = userService;
        this.userContext = userContext;
    }

    @Async("asyncTaskExecutor")
    public void produceProcessPostEventAsync(long userId, PostResponseDto post) {
        userContext.setUserId(userId);
        produceProcessPostEvent(post);
    }

    @Async("asyncTaskExecutor")
    public void produceSubProcessPostEventAsync(long userId, PostResponseDto post, List<Long> followersIds) {
        userContext.setUserId(userId);
        produceProcessPostEventToFollowers(post.id(), followersIds);
        log.info("Processing post {} for sublist of followers ids {}", post.id(), followersIds);
    }

    public void produceProcessPostEvent(PostResponseDto post) {
        List<SubscriptionUserDto> followers = userService.getFollowers(post.authorId());
        List<Long> followersIds = followers.stream()
                .map(SubscriptionUserDto::id)
                .toList();
        produceProcessPostEventToFollowers(post.id(), followersIds);
        log.info("Producing event of processing post {}", post.id());
    }

    private void produceProcessPostEventToFollowers(long postId, List<Long> followersIds) {
        PostProcessEvent postProcessEvent = PostProcessEvent.builder()
                .postId(postId)
                .followersIds(followersIds)
                .build();
        super.sendMessage(postProcessEvent);
        log.info("Sending PostProcessEvent to message broker. Post : {}", postId);
    }
}
