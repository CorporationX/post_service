package faang.school.postservice.service.kafka;

import faang.school.postservice.event.PostCommentEvent;
import faang.school.postservice.model.Post;

public interface KafkaService {
    void sendPostEvent(Post post, long contextUserId);
    void sendPostViewEvent(Long postId);
    void sendLikeEvent(Long postId, Long userId);
    void sendCommentEvent(PostCommentEvent event);
}
