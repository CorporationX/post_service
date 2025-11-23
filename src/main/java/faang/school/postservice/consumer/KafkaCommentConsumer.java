package faang.school.postservice.consumer;

import faang.school.postservice.dto.avro.CommentCreatedEventAvro;
import faang.school.postservice.dto.feed.CommentFeedDto;
import faang.school.postservice.dto.redis.UserRedisDto;
import faang.school.postservice.dto.user.UserViewDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.mapper.UserMapper;
import faang.school.postservice.producer.KafkaCommentProducer;
import faang.school.postservice.repository.redis.CommentRedisRepository;
import faang.school.postservice.repository.redis.PostRedisRepository;
import faang.school.postservice.repository.redis.UserRedisRepository;
import faang.school.postservice.retry.RetryUserServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Слушатель ивента создания комментариев
 * {@link KafkaCommentProducer}
 *
 * @author Linempy
 * @since 15.11.2025
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaCommentConsumer {

    private final UserRedisRepository userRepository;
    private final PostRedisRepository postRepository;
    private final CommentRedisRepository commentRepository;
    private final UserMapper userMapper;
    private final CommentMapper commentMapper;
    private final RetryUserServiceClient client;

    @KafkaListener(topics = "${spring.kafka.topics.comments}")
    public void consume(ConsumerRecord<String, CommentCreatedEventAvro> consumerRecord) {
        CommentCreatedEventAvro event = consumerRecord.value();
        log.info("Ивент о создании комментария id:{} был получен!", event.getCommentId());

        Optional<UserRedisDto> userFromRedis = userRepository.getUser(event.getAuthorId());
        log.info("Пользователь из Redis: {}, существует: {}", userFromRedis, userFromRedis.isPresent());

        UserRedisDto user = userFromRedis.orElseGet(() -> {
            log.info("Пользователь {} не был найден в Redis, обращение к клиенту пользователей...", event.getAuthorId());
            UserViewDto response = client.getUser(event.getAuthorId()).orElseGet(
                    () -> new UserViewDto(null, "Пользователь не найден", null)
            );
            UserRedisDto newUser = userMapper.toRedisDto(response);
            log.info("Полученный пользователь от клиента: {}", newUser);
            return newUser;
        });

        CommentFeedDto commentRedisDto = commentMapper.toFeedDto(event, user);
        commentRepository.saveLatestComment(commentRedisDto);
        postRepository.updateLatestComments(event.getPostId(), commentRedisDto.id());
    }
}