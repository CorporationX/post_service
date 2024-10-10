package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.model.dto.like.LikeDto;
import faang.school.postservice.model.dto.like.LikeEventDto;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.LikeEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
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
        var like = Like.builder()
                .userId(userId)
                .post(post)
                .build();

        var likeEventDto = LikeEventDto.builder()
                .postId(postId)
                .userId(userId)
                .likedTime(LocalDateTime.now())
                .build();

        var likeDto = likeMapper.toLikeDto(likeRepository.save(like));
        likeEventPublisher.sendEvent(likeEventDto);
        return likeDto;
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
        return userServiceClient.getUser(userId).id();
    }
}
