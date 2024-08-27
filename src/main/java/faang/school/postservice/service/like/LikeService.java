package faang.school.postservice.service.like;

import static faang.school.postservice.exception.ExceptionMessages.DELETION_ERROR_MESSAGE;
import static faang.school.postservice.exception.ExceptionMessages.FAILED_PERSISTENCE;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.ExceptionMessages;
import faang.school.postservice.mapper.comment.like.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.service.publisher.EventPublisherService;
import faang.school.postservice.validator.comment.UserClientValidation;
import faang.school.postservice.validator.like.CommentLikeValidation;
import faang.school.postservice.validator.like.PostLikeValidation;
import jakarta.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.AopInvocationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class LikeService {

  private static final String POST_ID = "Post with id: ";
  private static final String COMMENT_ID = "Comment with id: ";

  private final LikeRepository likeRepository;
  private final UserServiceClient userServiceClient;
  private final UserClientValidation userClientValidation;
  private final PostLikeValidation postLikeValidation;
  private final CommentLikeValidation commentLikeValidation;
  private final LikeMapper likeMapper;
  private final CommentService commentService;
  private final PostService postService;
  private final EventPublisherService eventPublisherService;

  @Value("${user.batch.size:100}")
  private int batchSize;

  //TODO: Add return type, use mapper
  public void likePost(LikeDto likeDto) {
    Like like = new Like();
    like.setPost(Post.builder().id(likeDto.getPostId()).build());
    like.setComment(Comment.builder().id(likeDto.getCommentId()).build());
    like.setUserId(likeDto.getUserId());
    likeRepository.save(like);

    eventPublisherService.submitEvent(likeDto);
  }

  public List<UserDto> getUsersByPostId(Long postId) {
    List<Like> likes = likeRepository.findByPostId(postId);
    if (likes.isEmpty()) {
      log.error(ExceptionMessages.LIKE_NOT_FOUND_FOR_POST + ": " + postId);
      throw new NoSuchElementException(ExceptionMessages.LIKE_NOT_FOUND_FOR_POST + ": " + postId);
    }

    List<Long> usersIds = likes.stream()
        .map(Like::getUserId)
        .toList();

    return getUserDtoByBatches(usersIds);
  }

  public List<UserDto> getUsersByCommentId(Long commentId) {
    List<Like> likes = likeRepository.findByCommentId(commentId);
    if (likes.isEmpty()) {
      log.error(ExceptionMessages.LIKE_NOT_FOUND_FOR_COMMENT + ": " + commentId);
      throw new NoSuchElementException(
          ExceptionMessages.LIKE_NOT_FOUND_FOR_COMMENT + ": " + commentId);
    }

    List<Long> usersIds = likes.stream()
        .map(Like::getUserId)
        .toList();
    return getUserDtoByBatches(usersIds);
  }

  private List<UserDto> getUserDtoByBatches(List<Long> usersIds) {
    List<UserDto> result = new ArrayList<>();
    int batchSize = this.batchSize;
    for (int i = 0; i < usersIds.size(); i += batchSize) {
      int end = Math.min(usersIds.size(), i + batchSize);
      List<Long> batch = usersIds.subList(i, end);
      result.addAll(userServiceClient.getUsersByIds(batch));
    }
    return result;
  }

  @Transactional
  public LikeDto addLikeToPost(long postId, LikeDto likeDto) {
    long userId = likeDto.getUserId();
    userClientValidation.checkUser(userId);
    postLikeValidation.validateLikes(postId, userId);

    Like like = Like.builder()
        .post(postService.getPost(postId))
        .userId(userId)
        .build();

    return likeMapper.toDto(saveLike(like));
  }

  @Transactional
  public LikeDto addLikeToComment(long commentId, LikeDto likeDto) {
    long userId = likeDto.getUserId();
    userClientValidation.checkUser(userId);
    commentLikeValidation.validateLikes(commentId, userId);

    Like like = Like.builder()
        .comment(commentService.getComment(commentId))
        .userId(userId)
        .build();

    return likeMapper.toDto(saveLike(like));
  }

  @Transactional
  public LikeDto removeLikeFromPost(long postId, LikeDto likeDto) {
    try {
      postLikeValidation.checkExistRecord(postId);
      likeRepository.deleteByPostIdAndUserId(postId, likeDto.getUserId());
    } catch (AopInvocationException e) {
      log.error(String.format(DELETION_ERROR_MESSAGE, POST_ID, postId), e);
    }
    return likeDto;
  }

  @Transactional
  public LikeDto removeLikeFromComment(long commentId, LikeDto likeDto) {
    try {
      commentLikeValidation.checkExistRecord(commentId);
      likeRepository.deleteByCommentIdAndUserId(commentId, likeDto.getUserId());
    } catch (AopInvocationException e) {
      log.error(String.format(DELETION_ERROR_MESSAGE, COMMENT_ID, commentId), e);
    }
    return likeDto;
  }

  private Like saveLike(Like like) {
    try {
      return likeRepository.save(like);
    } catch (RuntimeException e) {
      log.error(FAILED_PERSISTENCE, e);
      throw new PersistenceException(FAILED_PERSISTENCE, e);
    }
  }

}
