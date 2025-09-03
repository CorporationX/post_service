package faang.school.postservice.kafka.producer.post;

import school.faang.avro.post.PostViewEvent;

public interface PostProducer {
    void onPostView(PostViewEvent event);
}
