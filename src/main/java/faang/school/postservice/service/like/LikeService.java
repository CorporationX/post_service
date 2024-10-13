package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.publishable.LikeEvent;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.feed.FeedEventService;
import faang.school.postservice.service.publisher.LikeEventPublisher;
import faang.school.postservice.validator.like.LikeValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final LikeMapper likeMapper;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeValidator likeValidator;
    private final UserServiceClient userServiceClient;
    private final LikeEventPublisher eventPublisher;
    private final FeedEventService feedEventService;
    @Value("${like.userBatchSize}")
    private int userBatchSize;
  
    public void likePost(LikeDto likeDto) {
        likeValidator.validateUser(likeDto.getUserId());

        if (likeRepository.findByPostIdAndUserId(likeDto.getPostId(), likeDto.getUserId()).isPresent()) {
            unlikePost(likeDto);
        }

        Post post = postRepository.findById(likeDto.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("Пост не найден"));

        Like like = likeMapper.toEntity(likeDto);
        like.setPost(post);
        likeRepository.save(like);

        LikeEvent event = new LikeEvent(like.getUserId(), post.getAuthorId(), post.getId());
        eventPublisher.publish(event);

        feedEventService.createAndSendFeedLikeEvent(post.getId());
    }

    public void unlikePost(LikeDto likeDto) {
        likeValidator.validateUser(likeDto.getUserId());

        Like like = likeRepository.findByPostIdAndUserId(likeDto.getPostId(), likeDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Лайк не найден"));
        likeRepository.delete(like);
    }


    public void likeComment(LikeDto likeDto) {
        likeValidator.validateUser(likeDto.getUserId());

        if (likeRepository.findByCommentIdAndUserId(likeDto.getCommentId(), likeDto.getUserId()).isPresent()) {
            unlikeComment(likeDto);
        }

        Comment comment = commentRepository.findById(likeDto.getCommentId())
                .orElseThrow(() -> new IllegalArgumentException("Комментарий не найден"));

        Like like = likeMapper.toEntity(likeDto);
        like.setComment(comment);
        likeRepository.save(like);
    }

    public void unlikeComment(LikeDto likeDto) {
        likeValidator.validateUser(likeDto.getUserId());

        Like like = likeRepository.findByCommentIdAndUserId(likeDto.getCommentId(), likeDto.getUserId())
                .orElseThrow(()->new IllegalArgumentException("Лайк не найден"));
        likeRepository.delete(like);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getUsersThatLikedPost(Long postId) {
        List<Long> userIdsThatLikedPost = getLikesFromPost(postId);
        return getUsersFromUserService(userIdsThatLikedPost);
    }

    @Transactional(readOnly = true)
    public List<UserDto> getUsersThatLikedComment(Long commentId) {
        List<Long> userIdsThatLikedComment = getLikesFromComment(commentId);
        return getUsersFromUserService(userIdsThatLikedComment);
    }

    @Transactional
    public UserDto deleteLikeFromPost(Long postId, Long userId) {
        Like like = likeRepository.findByPostIdAndUserId(postId, userId).orElseThrow(() -> new EntityNotFoundException("Like was not found"));
        likeRepository.delete(like);
        return userServiceClient.getUser(userId);
    }

    @Transactional
    public UserDto deleteLikeFromComment(Long commentId, Long userId) {
        Like like = likeRepository.findByCommentIdAndUserId(commentId, userId).orElseThrow(() -> new EntityNotFoundException("Like was not found"));
        likeRepository.delete(like);
        return userServiceClient.getUser(userId);
    }

    private List<Long> getLikesFromPost(Long postId) {
        return likeRepository.findByPostId(postId).stream().map(Like::getUserId).toList();
    }

    private List<Long> getLikesFromComment(Long commentId) {
        return likeRepository.findByCommentId(commentId).stream().map(Like::getUserId).toList();
    }

    private List<UserDto> getUsersFromUserService(List<Long> userIds) {
        List<UserDto> users = new ArrayList<>();
        for (int i = 0; i < userIds.size(); i += userBatchSize) {
            int bound = Math.min(i + userBatchSize, userIds.size());
            List<UserDto> batchOfUsers = userServiceClient.getUsersByIds(userIds.subList(i, bound));
            users.addAll(batchOfUsers);
        }
        return users;
    }
}
