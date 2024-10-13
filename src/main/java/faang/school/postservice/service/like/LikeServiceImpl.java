package faang.school.postservice.service.like;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.like.LikeEvent;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.LikeEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
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
    private final ObjectMapper objectMapper;
    private final LikeEventPublisher likeEventPublisher;

    public LikeDto likePost(LikeDto likeDto) {
        Post post = postRepository.findById(likeDto.getPostId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("Post with %s id not found", likeDto.getPostId())));
        startValidationForPost(likeDto);
        log.info("The like was verified with the {}post id", likeDto.getPostId());

        Like like = likeMapper.toEntity(likeDto);
        like.setPost(post);
        like.setCreatedAt(LocalDateTime.now());

        likeRepository.save(like);
        log.info("The like was added to the database to {} post", likeDto.getPostId());

        publishLikeEvent(likeDto, post);

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
        log.info("The like was added to the database to {}comment", likeDto.getCommentId());
        return likeMapper.toDto(like);
    }

    public void unlikeComment(LikeDto likeDto) {
        Comment comment = validateAndGetComment(likeDto);
        likeRepository.deleteByCommentIdAndUserId(comment.getId(), likeDto.getUserId());
    }

    private void pushMessage(LikeEvent likeEvent) {
        try {
            String json = objectMapper.writeValueAsString(likeEvent);
            likeEventPublisher.publish(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
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
        likeEvent.setLikingUserId(likeDto.getUserId());
        likeEvent.setLikedUserId(post.getAuthorId());
        likeEvent.setCreatedAt(likeDto.getCreatedAt());

        try {
            String json = objectMapper.writeValueAsString(likeEvent);
            likeEventPublisher.publish(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}