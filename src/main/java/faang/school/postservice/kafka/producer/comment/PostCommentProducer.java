package faang.school.postservice.kafka.producer.comment;

import school.faang.avro.post.PostCommentEvent;

public interface PostCommentProducer {
    void onCommentPublished(PostCommentEvent dto);
}
