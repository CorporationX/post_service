package faang.school.postservice.kafka.producer.comment;

import school.faang.avro.post.CommentEvent;

public interface PostCommentProducer {
    void onCommentPublished(CommentEvent event);
}
