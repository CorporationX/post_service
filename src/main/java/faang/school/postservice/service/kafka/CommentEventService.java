package faang.school.postservice.service.kafka;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.kafka.event.CommentEventDto;
import faang.school.postservice.producer.KafkaCommentProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentEventService {
    private final KafkaCommentProducer producer;

    @Async("kafkaProducerExecutor")
    public void produce(Long postId, CommentDto commentDto) {
        CommentEventDto eventDto = CommentEventDto.builder()
            .postId(postId)
            .commentId(commentDto.id())
            .authorId(commentDto.authorId())
            .build();
        producer.send(eventDto);
    }

}
