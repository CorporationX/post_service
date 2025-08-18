package faang.school.postservice.kafka.producer.comment;

import faang.school.postservice.kafka.dto.comment.PostCommentPublishedDto;

public interface PostCommentPublishedProducer {
    void onCommentPublished(PostCommentPublishedDto dto);
}
