package faang.school.postservice.service;

import faang.school.postservice.dto.FeedDto;
import faang.school.postservice.dto.kafka.KafkaPostEvent;
import org.springframework.data.domain.Page;
import org.springframework.kafka.support.Acknowledgment;

public interface FeedService {

    Page<FeedDto> getFeed(Long postId);

    void addToFeed(KafkaPostEvent kafkaPostEvent, Acknowledgment acknowledgment);
}
