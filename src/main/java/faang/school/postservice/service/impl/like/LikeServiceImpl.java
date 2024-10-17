package faang.school.postservice.service.impl.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.model.event.LikeEvent;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.model.entity.Comment;
import faang.school.postservice.model.entity.Like;
import faang.school.postservice.model.entity.Post;
import faang.school.postservice.model.dto.like.LikeDto;
import faang.school.postservice.model.dto.user.UserDto;
import faang.school.postservice.publisher.LikeEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.LikeService;
import faang.school.postservice.validator.like.LikeValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeServiceImpl implements LikeService {
    private final LikeRepository likeRepository;
    private final LikeMapper likeMapper;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final LikeValidator likeValidator;
    private final UserServiceClient userServiceClient;
    private final UserContext userContext;
    private final LikeEventPublisher likeEventPublisher;

    @Override
    @Transactional
    public LikeDto createLikeComment(Long commentId) {
        long userId = getUserId();
        Comment comment = likeValidator.validate(commentId, userId, commentRepository);
        log.info("Creating a like for a comment with ID {}", commentId);

        Like saveLike = saveLikeComment(comment, userId);

        log.info("Created a like with ID {} from a user with ID {} to a comment with ID {}",
                saveLike.getId(),
                userId,
                commentId);
        return likeMapper.toLikeDto(saveLike);
    }

    private Like saveLikeComment(Comment comment, long userId) {
        Like like = new Like();
        like.setUserId(userId);
        like.setComment(comment);
        likeRepository.save(like);
        return like;
    }

    @Override
    @Transactional
    public void deleteLikeComment(Long commentId) {
        likeValidator.validateCommentOrPost(commentId, commentRepository);
        likeRepository.deleteByCommentIdAndUserId(commentId, getUserId());
        log.info("The comment was deleted from the {}", commentId);
    }

    @Override
    @Transactional
    public LikeDto createLikePost(Long postId) {
        long userId = getUserId();
        Post post = likeValidator.validate(postId, userId, postRepository);
        log.info("Creating a like for a post with ID {}", postId);

        Like saveLike = saveLikePost(post, userId);
        LikeEvent likeEventDto = new LikeEvent(post.getAuthorId(),
                userId,
                postId,
                LocalDateTime.now());
        likeEventPublisher.publish(likeEventDto);

        log.info("Created a like with ID {} from a user with ID {} to a post with ID {}",
                saveLike.getId(),
                userId,
                postId);
        return likeMapper.toLikeDto(saveLike);
    }

    private Like saveLikePost(Post post, long userId) {
        Like like = new Like();
        like.setUserId(userId);
        like.setPost(post);
        likeRepository.save(like);
        return like;
    }

    @Override
    @Transactional
    public void deleteLikePost(Long postId) {
        likeValidator.validateCommentOrPost(postId, postRepository);
        likeRepository.deleteByPostIdAndUserId(postId, getUserId());
        log.info("The comment was deleted from the {}", postId);
    }

    private long getUserId() {
        long userId = userContext.getUserId();
        UserDto userDto = userServiceClient.getUser(userId);
        return userDto.id();
    }
}
