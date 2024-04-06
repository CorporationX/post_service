package faang.school.postservice.service.hash;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.event.CommentEventKafka;
import faang.school.postservice.dto.event.LikeEventKafka;
import faang.school.postservice.dto.event.PostEventKafka;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.KafkaCommentProducer;
import faang.school.postservice.producer.KafkaLikeProducer;
import faang.school.postservice.producer.KafkaPostProducer;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FeedHeater {
    private final UserServiceClient userServiceClient;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final KafkaPostProducer postProducer;
    private final KafkaCommentProducer commentProducer;
    private final KafkaLikeProducer likeProducer;

    @Value("${feed.heat.batch}")
    private int batchSize;

    public void feedHeat() {
        List<Long> userIds = userServiceClient.getUserIds();
        List<List<Long>> partitionUsers = ListUtils.partition(userIds, batchSize);

        for (List<Long> batch : partitionUsers) {
            send(batch);
        }
    }

    @Async("executor")
    public void send(List<Long> usersBatch) {
        usersBatch.forEach(userId -> {
            List<Post> posts = postRepository.findLatestPosts(userId);
            posts.forEach(post -> {
                postProducer.publishHeatCache(new PostEventKafka(post, userServiceClient.getFollowerIds(post.getAuthorId()), userServiceClient.getUser(post.getAuthorId())));
                commentRepository.findLatestComments(post.getId())
                        .forEach(comment -> {
                            commentProducer.publishHeatCache(new CommentEventKafka(post.getId(), comment.getAuthorId(), comment.getContent(), userServiceClient.getUser(comment.getAuthorId())));
                        });
                likeRepository.findByPostId(post.getId())
                        .forEach(like -> {
                            likeProducer.publishHeatCache(new LikeEventKafka(post.getId(), like.getUserId()));
                        });
            });
        });
    }
}
