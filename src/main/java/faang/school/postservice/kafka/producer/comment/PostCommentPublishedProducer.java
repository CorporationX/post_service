package faang.school.postservice.kafka.producer.comment;

import faang.school.postservice.dto.comment.CommentDto;

public interface PostCommentPublishedProducer {
    void onCommentPublished(CommentDto dto);
}
