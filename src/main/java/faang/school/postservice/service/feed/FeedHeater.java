package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.dto.event.CommentEventKafka;
import faang.school.postservice.dto.event.LikeEventKafka;
import faang.school.postservice.dto.event.PostEventKafka;
import faang.school.postservice.dto.event.ViewEventKafka;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.KafkaCommentProducer;
import faang.school.postservice.producer.KafkaLikeProducer;
import faang.school.postservice.producer.KafkaPostProducer;
import faang.school.postservice.producer.KafkaPostViewProducer;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedHeater {

    private final UserServiceClient userServiceClient;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final LikeMapper likeMapper;
    private final KafkaPostProducer kafkaPostProducer;
    private final KafkaCommentProducer kafkaCommentProducer;
    private final KafkaLikeProducer kafkaLikeProducer;
    private final KafkaPostViewProducer kafkaPostViewProducer;


    @Value(value = "${feed.heat_batch_size}")
    private int batchSize;

    @Value(value = "${feed.count_post}")
    private int countPost;


    public void feedHeatProducer() {
        List<Long> usersId = userServiceClient.getActiveUsers();
        List<List<Long>> batchUsersId = ListUtils.partition(
                usersId, batchSize);

        batchUsersId.forEach(this::send);
    }

    @Async("executor")
    public void send(List<Long> batchUsersId) {
        batchUsersId.forEach(userId -> {
            List<Post> posts = postRepository.findByAuthorId(userId);
            posts.forEach(
                    post -> {
                        PostEventKafka postEventKafka = new PostEventKafka(post);
                        postEventKafka.addFollowersId(userServiceClient.getFollowersId(post.getAuthorId()));
                        kafkaPostProducer.sendMessage(postEventKafka);

                        getComments(post).forEach(kafkaCommentProducer::sendMessage);

                        post.getLikes().forEach(like -> {
                            LikeEventKafka likeEventKafka = new LikeEventKafka(
                                    getLike(post.getId(), like.getUserId()),
                                    userServiceClient.getUser(like.getUserId()));
                            kafkaLikeProducer.sendMessage(likeEventKafka);
                        });

                        getViewer(post).forEach(kafkaPostViewProducer::sendMessage);
                    }
            );
        });
    }

    private List<CommentEventKafka> getComments(Post post) {
        List<Comment> comments = new ArrayList<>(3);
        comments.addAll(commentRepository.findAllByPostId(post.getId()));
        return comments.stream().map(comment -> {
            return new CommentEventKafka(comment, userServiceClient.getUser(comment.getAuthorId()));
        }).toList();
    }

    private List<ViewEventKafka> getViewer(Post post) {
        List<UserDto> viewer = new ArrayList<>();
        viewer.addAll(userServiceClient.getUsersByIds(
                post.getComments().stream()
                        .map(Comment::getAuthorId)
                        .toList()));
        viewer.addAll(userServiceClient.getUsersByIds(
                post.getLikes().stream()
                        .map(Like::getUserId)
                        .toList()));

        return viewer.stream().map(view -> {
            return new ViewEventKafka(post.getId(), view);
        }).toList();
    }

    private LikeDto getLike(long postId, long userId) {
        return likeMapper.toDto(likeRepository.findByPostIdAndUserId(postId, userId).orElseThrow(
                () -> new DataValidationException("Like is not found")));
    }
}