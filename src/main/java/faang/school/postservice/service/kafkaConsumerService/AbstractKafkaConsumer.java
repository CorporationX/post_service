package faang.school.postservice.service.kafkaConsumerService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.hash.PostHash;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.repository.redis.PostRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@RequiredArgsConstructor
@Slf4j
public abstract class AbstractKafkaConsumer<T> {

    public final PostRedisRepository postRedisRepository;

    public T listener(String message, Class<T> eventType) {
        T eventKafka;
        try {
            eventKafka = new ObjectMapper().readValue(
                    message, eventType);
        } catch (JsonProcessingException e) {
            log.error("Failed to make from JSON");
            throw new RuntimeException(Arrays.toString(e.getStackTrace()));
        }
        return eventKafka;
    }

    public PostHash getPostHash(long postId) {
        return postRedisRepository.findById(postId).orElseThrow(() ->
                new DataValidationException("Post not found from Kafka: " + postId));
    }
}
