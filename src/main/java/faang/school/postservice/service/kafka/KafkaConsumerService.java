package faang.school.postservice.service.kafka;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.KafkaMessageException;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumerService {
    private final PostService postService;
    private String messageLogInfo = "Received message class: {}, message object: {}, time: {}";

    @KafkaListener(topics = "${spring.kafka.topic.consumer.post-views}",
            groupId = "${spring.kafka.topic.consumer.group-id.post-views}")
    public void listenPaymentRequest(ConsumerRecord<String, PostDto> record, Acknowledgment acknowledgment) {
        try {
            PostDto postDto = record.value();
            log.info(messageLogInfo, postDto.getClass(), postDto, LocalDateTime.now());
            postService.savePostInDataBase(postDto);
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("Error getting PostDto from KafkaConsumerService. exception: {}, time: {}, cause: {}, message: {}",
                    e.getClass().getName(), LocalDateTime.now(), e.getCause(), e.getMessage());
            throw new KafkaMessageException("Error processing message: " + e.getMessage());
        }
    }
}