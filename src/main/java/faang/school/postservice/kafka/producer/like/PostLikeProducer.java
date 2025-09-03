package faang.school.postservice.kafka.producer.like;


import school.faang.avro.post.LikePublishEvent;

public interface PostLikeProducer {
    void onLikePublished(LikePublishEvent event);
}
