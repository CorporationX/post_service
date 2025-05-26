package faang.school.postservice.service.publisher;

public interface KafkaPostViewProducer {

    void publishPostViewEvent(Long postId, Long userId);
}
