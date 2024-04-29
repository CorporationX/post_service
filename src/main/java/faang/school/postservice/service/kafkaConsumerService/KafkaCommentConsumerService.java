package faang.school.postservice.service.kafkaConsumerService;

import faang.school.postservice.dto.event.CommentEventKafka;
import faang.school.postservice.dto.hash.PostHash;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.repository.redis.PostRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KafkaCommentConsumerService extends
        AbstractKafkaConsumer<CommentEventKafka> {

    private final CommentMapper commentMapper;

    @Autowired
    public KafkaCommentConsumerService(PostRedisRepository postRedisRepository, CommentMapper commentMapper) {
        super(postRedisRepository);
        this.commentMapper = commentMapper;
    }

    @Transactional
    @KafkaListener(topics = "${spring.kafka.topics.comment}")
    @Async("executor")
    public void addComment(String message, Acknowledgment acknowledgement) {
        CommentEventKafka commentEventKafka = listener(message, CommentEventKafka.class);

        PostHash postHash = getPostHash(commentEventKafka.getPostId());
        postHash.addComment(commentMapper.toDtoFromKafka(commentEventKafka));
        postRedisRepository.saveInRedis(postHash);
        acknowledgement.acknowledge();
    }
}



