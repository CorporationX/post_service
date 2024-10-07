package faang.school.postservice.producer.like;

import faang.school.postservice.event.like.KafkaLikeEvent;
import faang.school.postservice.model.Like;
import faang.school.postservice.producer.BaseProducer;

public interface LikeServiceProducer extends BaseProducer<Like> {
}
