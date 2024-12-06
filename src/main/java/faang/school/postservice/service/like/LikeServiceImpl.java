package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.kafka.events.KafkaLikeEvent;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.like.LikeEvent;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.KafkaProducer;
import faang.school.postservice.properties.topics.KafkaTopics;
import faang.school.postservice.publisher.redis.LikeEventPublisher;
import faang.school.postservice.repository.comment.CommentRepository;
import faang.school.postservice.repository.like.LikeRepository;
import faang.school.postservice.repository.post.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final LikeMapper likeMapper;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final LikeEventPublisher likeEventPublisher;
    private final KafkaProducer kafkaProducer;
    private final KafkaTopics kafkaTopics;

    public LikeDto likePost(LikeDto likeDto) {
        Post post = postRepository.findById(likeDto.getPostId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("Post with %s id not found", likeDto.getPostId())));
        startValidationForPost(likeDto);
        log.info("The like was verified with the {} post id", likeDto.getPostId());

        Like like = likeMapper.toEntity(likeDto);
        like.setPost(post);
        like.setCreatedAt(LocalDateTime.now());

        likeRepository.save(like);
        post.getLikes().add(like);
        log.info("The like was added to the database to {} post", likeDto.getPostId());

        publishLikeEvent(likeDto, post);
        kafkaProducer.send(kafkaTopics.getLikes().getName(), KafkaLikeEvent.builder()
                        .likeId(likeDto.getPostId())
                        .authorId(likeDto.getUserId())
                        .commentId(likeDto.getCommentId())
                        .postId(likeDto.getPostId())
                .build());
        return likeMapper.toDto(like);
    }

    @Transactional
    public void unlikePost(LikeDto likeDto) {
        Post post = validateAndGetPost(likeDto);
        likeRepository.deleteByPostIdAndUserId(post.getId(), likeDto.getUserId());
    }

    public LikeDto likeComment(LikeDto likeDto) {
        Comment comment = commentRepository.findById(likeDto.getCommentId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("Comment with %s id not found", likeDto.getCommentId())));
        startValidationForComment(likeDto);
        log.info("The like was verified with the {}comment id", likeDto.getCommentId());

        Like like = likeMapper.toEntity(likeDto);
        like.setComment(comment);
        like.setPost(null);
        like.setCreatedAt(LocalDateTime.now());
        likeRepository.save(like);
        log.info("The like was added to the database to {} comment", likeDto.getCommentId());
        return likeMapper.toDto(like);
    }

    public void unlikeComment(LikeDto likeDto) {
        Comment comment = validateAndGetComment(likeDto);
        likeRepository.deleteByCommentIdAndUserId(comment.getId(), likeDto.getUserId());
    }

    private void startValidationForPost(LikeDto likeDto) {
        UserDto userDto = userServiceClient.getUser(likeDto.getUserId());
        List<Like> postLikes = likeRepository.findByPostId(likeDto.getPostId());
        validateUserReal(likeDto, userDto);
        validateDuplicateLikeForPost(likeDto, postLikes);
        validateLikeToPostAndCommentForPost(likeDto);
    }

    private void startValidationForComment(LikeDto likeDto) {
        UserDto userDto = userServiceClient.getUser(likeDto.getUserId());
        List<Like> commentLikes = likeRepository.findByCommentId(likeDto.getCommentId());
        validateUserReal(likeDto, userDto);
        validateDuplicateLikeForComment(likeDto, commentLikes);
        validateLikeToPostAndCommentForComment(likeDto);
    }

    private void validateLikeToPostAndCommentForComment(LikeDto likeDto) {
        List<Long> postLikes = postRepository
                .findById(likeDto.getPostId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("Post with %s id not found", likeDto.getPostId())))
                .getLikes()
                .stream()
                .map(Like::getUserId)
                .toList();

        if (postLikes.contains(likeDto.getUserId())) {
            throw new EntityNotFoundException("Like already exist on the post!");
        }
    }

    private void validateLikeToPostAndCommentForPost(LikeDto likeDto) {
        List<Long> commentsLikes = postRepository
                .findById(likeDto.getPostId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("Post with %s id not found", likeDto.getPostId())))
                .getComments()
                .stream()
                .flatMap(comment -> comment.getLikes().stream())
                .map(Like::getUserId)
                .toList();

        if (commentsLikes.contains(likeDto.getUserId())) {
            throw new EntityNotFoundException("Like already exist on the comment!");
        }
    }

    private Post validateAndGetPost(LikeDto likeDto) {
        List<Post> postsUser = postRepository.findByAuthorIdWithLikes(likeDto.getUserId());
        return postsUser
                .stream()
                .filter(filter -> filter.getId() == likeDto.getPostId())
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(String.format("Post with %s id not found", likeDto.getPostId())));
    }

    private Comment validateAndGetComment(LikeDto likeDto) {
        List<Comment> comments = commentRepository.findAllByPostId(likeDto.getPostId());
        return comments
                .stream()
                .filter(filter -> filter.getId() == likeDto.getCommentId())
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(String.format("Comment with %s id not found", likeDto.getCommentId())));
    }

    private void validateUserReal(LikeDto likeDto, UserDto userDto) {
        if (!userDto.getId().equals(likeDto.getUserId())) {
            throw new EntityNotFoundException(String.format("User with %s id not exist", userDto.getId()));
        }
    }

    private void validateDuplicateLikeForPost(LikeDto likeDto, List<Like> postLikes) {
        for (Like like : postLikes) {
            if (Objects.equals(like.getUserId(), likeDto.getUserId())) {
                throw new IllegalArgumentException("Post already liked!");
            }
        }
    }

    private void validateDuplicateLikeForComment(LikeDto likeDto, List<Like> commentLikes) {
        for (Like like : commentLikes) {
            if (Objects.equals(like.getUserId(), likeDto.getUserId())) {
                throw new IllegalArgumentException("Comment already liked!");
            }
        }
    }

    @Override
    public List<UserDto> getUsersLikedPost (long postId){
        List<Like> likes = likeRepository.findByPostId(postId);

        return dividingListIntoGroups(likes);
    }

    @Override
    public List<UserDto> getUsersLikedComment (long commentId){
        List<Like> likes = likeRepository.findByCommentId(commentId);

        return dividingListIntoGroups(likes);
    }

    private List<UserDto> dividingListIntoGroups (List<Like> likes) {
        List<Long> userIds = likes.stream()
                .map(Like::getUserId)
                .toList();

        List<List<UserDto>> results = new ArrayList<>();

        for (int i = 0; i < userIds.size(); i += 100) {
            List<Long> sublist = userIds.subList(i, Math.min(i + 100, userIds.size()));

            List<UserDto> result = userServiceClient.getUsersByIds(sublist);
            results.add(result);
        }

        return results.stream()
                .flatMap(List::stream)
                .toList();
    }

    private void publishLikeEvent(LikeDto likeDto, Post post) {
        LikeEvent likeEvent = new LikeEvent();
        likeEvent.setPostId(likeDto.getPostId());
        likeEvent.setUserId(likeDto.getUserId());
        likeEvent.setLikingUserId(likeDto.getUserId());
        likeEvent.setLikedUserId(post.getAuthorId());
        likeEvent.setCreatedAt(likeDto.getCreatedAt());

        likeEventPublisher.publish(likeEvent);
    }
}