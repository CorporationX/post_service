package faang.school.postservice.producer.post;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.event.post.PostEvent;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.AbstractProducer;
import faang.school.postservice.producer.PostServiceProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

@Service
@Slf4j
public class PostProducer extends AbstractProducer<PostEvent> implements PostServiceProducer {

    private final UserServiceClient userServiceClient;
    private final Executor taskExecutor;

    @Value("${kafka.topic.posts-topic.batch-size}")
    private int batchSize;

    public PostProducer(KafkaTemplate<String, Object> kafkaTemplate,
                        @Value("${kafka.topic.posts-topic.name}") String topicName,
                        UserServiceClient userServiceClient,
                        @Qualifier("taskExecutor") Executor taskExecutor) {
        super(kafkaTemplate, topicName);
        this.userServiceClient = userServiceClient;
        this.taskExecutor = taskExecutor;
    }

    @Override
    public void send(Post post) {
        List<Long> followers = userServiceClient.getFollowerIds(post.getAuthorId());

        List<List<Long>> batches = splitList(followers, batchSize);

        for (List<Long> batch : batches) {
            taskExecutor.execute(() -> {
                PostEvent postEvent = new PostEvent(post.getId(), post.getAuthorId(), batch);
                sendEvent(postEvent);
                log.info("Sent batch with {} followers", batch.size());
            });
        }
    }

    private List<List<Long>> splitList(List<Long> list, int batchSize) {
        List<List<Long>> result = new ArrayList<>();
        for (int i = 0; i < list.size(); i += batchSize) {
            result.add(list.subList(i, Math.min(i + batchSize, list.size())));
        }
        return result;
    }
}

