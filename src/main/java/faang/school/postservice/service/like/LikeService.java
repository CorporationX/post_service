package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeRequestDto;
import faang.school.postservice.dto.like.LikeResponseDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.event_sender.LikeEventSender;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.comment.CommentValidator;
import faang.school.postservice.validator.like.LikeValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeMapper likeMapper;
    private final LikeValidator validator;
    private final UserServiceClient userServiceClient;
    private final CommentValidator commentValidator;
    private final LikeEventSender likeEventSender;

    private static final int BATCH_SIZE = 100;

    public LikeResponseDto postLike(LikeRequestDto acceptanceLikeDto, long postId) {
        Long userId = acceptanceLikeDto.getUserId();
        validator.validateUserId(userId);
        Post post = getPost(postId);
        boolean result = validator.validatePostHasLike(post.getId(), userId);
        if (!result) {
            throw new DataValidationException("Post already liked with id " + userId);
        }
        Like like = likeMapper.toLike(acceptanceLikeDto);
        like.setPost(post);
        likeRepository.save(like);
        log.info("The like {} was successfully saved in DB", like.getId());
        post.getLikes().add(like);
        postRepository.save(post);
        log.info("The post {} was successfully saved in DB", post.getId());

        likeEventSender.sendEvent(like);

        return likeMapper.toResponseLikeDto(like);
    }

    public void deleteLikeFromPost(@RequestBody LikeRequestDto acceptanceLikeDto, @PathVariable long postId) {
        Long userId = acceptanceLikeDto.getUserId();
        validator.validateUserId(userId);
        Post post = getPost(postId);
        boolean result = validator.validatePostHasLike(post.getId(), userId);
        if (result) {
            throw new DataValidationException("Post hat not liked with id " + userId);
        }
        likeRepository.deleteByPostIdAndUserId(postId, userId);
        log.info("The like {} was successfully deleted from DB", acceptanceLikeDto.getId());
        deleteLike(post.getLikes(), userId);
        postRepository.save(post);
        log.info("The post {} was successfully saved in DB", post.getId());

    }

    public LikeResponseDto commentLike(LikeRequestDto acceptanceLikeDto, long commentId) {
        Long userId = acceptanceLikeDto.getUserId();
        validator.validateUserId(userId);
        Comment comment = getComment(commentId);
        boolean result = validator.validateCommentHasLike(comment.getId(), userId);
        if (!result) {
            throw new DataValidationException("Comment already liked with id " + commentId);
        }
        Like like = likeMapper.toLike(acceptanceLikeDto);
        like.setComment(comment);
        likeRepository.save(like);
        log.info("The like {} was successfully saved in DB", like.getId());
        comment.getLikes().add(like);
        commentRepository.save(comment);
        log.info("The comment {} was successfully saved in DB", comment.getId());
        return likeMapper.toResponseLikeDto(like);
    }

    public void deleteLikeFromComment(LikeRequestDto acceptanceLikeDto, long commentId) {
        Long userId = acceptanceLikeDto.getUserId();
        validator.validateUserId(userId);
        Comment comment = getComment(commentId);
        boolean result = validator.validateCommentHasLike(comment.getId(), userId);
        if (result) {
            throw new DataValidationException("Comment not liked with id " + commentId);
        }
        likeRepository.deleteByCommentIdAndUserId(commentId, userId);
        log.info("The like {} was successfully deleted from DB", acceptanceLikeDto.getId());
        deleteLike(comment.getLikes(), userId);
        commentRepository.save(comment);
        log.info("The comment {} was successfully saved in DB", comment.getId());
    }

    private Post getPost(long postId) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isEmpty()) {
            throw new DataValidationException("Not found post with id " + postId);
        }
        return post.get();
    }

    private Comment getComment(long commentId) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        if (comment.isEmpty()) {
            throw new DataValidationException("Not found comment with id " + commentId);
        }
        return comment.get();
    }

    private void deleteLike(List<Like> likes, long userId) {
        likes.removeIf(like -> like.getUserId().equals(userId));
    }


    public List<UserDto> getUsersByPostId(long postId) {
        List<Long> userIds = likeRepository.findByPostId(postId).stream()
                .map(Like::getUserId)
                .toList();

        if (userIds.isEmpty()) {
            return List.of();
        }

        return fetchUsersInBatches(userIds);
    }

    public List<UserDto> getUsersByCommentId(long commentId) {
        commentValidator.validateCommentExists(commentId);

        List<Long> userIds = likeRepository.findByCommentId(commentId).stream()
                .map(Like::getUserId)
                .toList();

        if (userIds.isEmpty()) {
            return List.of();
        }

        return fetchUsersInBatches(userIds);
    }

    private List<List<Long>> splitIntoBatches(List<Long> userIds, int batchSize) {
        List<List<Long>> batches = new ArrayList<>();
        for (int i = 0; i < userIds.size(); i += batchSize) {
            batches.add(userIds.subList(i, Math.min(i + batchSize, userIds.size())));
        }
        return batches;
    }

    private List<UserDto> fetchUsersInBatches(List<Long> userIds) {
        List<List<Long>> batches = splitIntoBatches(userIds, BATCH_SIZE);
        List<UserDto> allUsers = new ArrayList<>();

        for (List<Long> batch : batches) {
            try {
                allUsers.addAll(userServiceClient.getUsersByIds(batch));
            } catch (Exception e) {
                log.error("Error fetching users for batch: {}. {}", batch, e.getMessage());
            }
        }
        return allUsers;
    }

}