package faang.school.postservice.kafka.producer.post;

import school.faang.avro.post.PostCreateEvent;
import school.faang.avro.post.PostCreateFanoutEvent;
import school.faang.avro.post.PostViewEvent;

public interface PostProducer {
    void onPostView(PostViewEvent event);

    void onPostCreate(PostCreateEvent event);

    void onPostFanoutBatch(PostCreateFanoutEvent event);
}
