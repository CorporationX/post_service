package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.kafka.KafkaProducerService;
import faang.school.postservice.dto.likesystem.LikeDto;
import faang.school.postservice.dto.likesystem.LikeEventDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class LikeService {

    @Value("${kafka.topics.like-post}")
    private String topicName;

    private static final int BATCH_SIZE = 100;


    private final LikeRepository likeRepository;
    private final CommentService commentService;
    private final PostService postService;
    private final UserServiceClient userServiceClient;
    private final KafkaProducerService kafkaService;

    private final LikeMapper likeMapper;


    @Transactional
    public LikeDto addLikePost(Long postId, Long userId) {
        Post post = postService.getPostById(postId);    //Проверка на наличие поста в системе

        userServiceClient.getUser(userId);  //Проверка на наличие юзера в системе

        if (post.getLikes().stream()
                .anyMatch(like -> Objects.equals(like.getUserId(), userId))) {
            throw new IllegalArgumentException("Post already has like on it");
        }   //Проверка на "стоит уже лайк на посте или нет"

        Like like = createPostLike(post, userId);
        post.getLikes().add(like);
        likeRepository.save(like);

        kafkaService.sendMessage(new LikeEventDto(post.getAuthorId(), userId, LocalDateTime.now()), topicName);

        return likeMapper.toDto(like);
    }

    @Transactional
    public LikeDto deleteLikePost(Long id) {
        Like like = likeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("The like with id = " + id + " does not exist"));

        likeRepository.delete(like);
        like.getPost().getLikes().remove(like);

        return likeMapper.toDto(like);
    }

    @Transactional
    public LikeDto addLikeComment(Long commentId, Long userId) {
        Comment comment = commentService.getCommentById(commentId);
        //Проверка на наличие комментария в системе

        userServiceClient.getUser(userId);  //Проверка на наличие юзера в системе

        if (comment.getLikes().stream()
                .anyMatch(like -> Objects.equals(like.getUserId(), userId))) {
            throw new IllegalArgumentException("Comment already has like on it");
        }   //Стоит ли лайк на комменте или нет

        Like like = createCommentLike(comment, userId);
        comment.getLikes().add(like);
        likeRepository.save(like);

        return likeMapper.toDto(like);
    }

    @Transactional
    public LikeDto deleteLikeComment(Long id) {
        Like like = likeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("The like with id = " + id + " does not exist"));

        likeRepository.delete(like);
        like.getComment().getLikes().remove(like);

        return likeMapper.toDto(like);
    }

    @Transactional
    public List<UserDto> getUsersWhoLikedPost(Long postId) {
        List<Long> userIds = likeRepository.findByPostId(postId)
                .stream()
                .map(Like::getUserId)
                .toList();

        return fetchUsersInBatches(userIds);
    }

    @Transactional
    public List<UserDto> getUsersWhoLikedComment(Long commentId) {
        List<Long> userIds = likeRepository.findByCommentId(commentId)
                .stream()
                .map(Like::getUserId)
                .toList();

        return fetchUsersInBatches(userIds);
    }

    private List<UserDto> fetchUsersInBatches(List<Long> userIds) {
        if (userIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<UserDto> result = new ArrayList<>();
        for (int i = 0; i < userIds.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, userIds.size());
            List<Long> batch = userIds.subList(i, end);
            result.addAll(userServiceClient.getUsersByIds(batch));
        }
        return result;
    }


    private Like createPostLike(Post post, Long userId) {
        return Like.builder()
                .userId(userId)
                .post(post)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private Like createCommentLike(Comment comment, Long userId) {
        return Like.builder()
                .userId(userId)
                .comment(comment)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
