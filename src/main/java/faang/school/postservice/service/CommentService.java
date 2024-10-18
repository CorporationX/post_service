package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.event.CommentEvent;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.producer.EventProducer;
import faang.school.postservice.service.publisher.PublicationService;
import faang.school.postservice.service.publisher.messagePublishers.CommentEventPublisher;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
  private static final String MESSAGE_POST_NOT_IN_DB = "Post is not in the database";
  private static final int MAX_LEN_CONTENT = 4096;
  private static final String MESSAGE_INVALID_TEXT_OF_COMMENT = "Invalid content of comment";
  private static final String MESSAGE_COMMENT_NOT_EXIST = "This comment does not exist";
  private static final String MESSAGE_POST_ID_AND_COMMENT_POST_ID_NOT_EQUAL = "postId and commentPostId not equal";
  private static final String MESSAGE_USER_LEFT_COMMENT_NOT_EXIST = "The user who left the comment does not exist";

  private final CommentMapper mapper;
  private final PostRepository postRepository;
  private final CommentRepository commentRepository;
  private final UserServiceClient userServiceClient;
  private final PublicationService<CommentEventPublisher, CommentEvent> publishService;
  private final EventProducer<CommentEvent> commentEventProducer;

  public CommentDto addComment(Long postId, CommentDto dto) {
    Post post = getPost(postId);
    try {
      userServiceClient.getUser(dto.getAuthorId());
    } catch (FeignException e) {
      throw new RuntimeException(MESSAGE_USER_LEFT_COMMENT_NOT_EXIST);
    }
    validateCommentContent(dto);
    Comment comment = mapper.toEntity(dto);
    comment.setPost(post);
    Comment savedComment = commentRepository.save(comment);
    log.info("comment with id:{} created.", savedComment.getId());
    publishService.publishEvent(mapper.toCommentEvent(savedComment));
    return mapper.toDto(savedComment);
  }

  public CommentDto changeComment(Long postId, CommentDto dto) {
    String content = validateCommentContent(dto);
    Comment comment = getCommentFromDatabase(dto);
    validatePostId(postId, comment);
    comment.setContent(content);
    return mapper.toDto(commentRepository.save(comment));
  }

  public List<CommentDto> getAllCommentsOfPost(Long postId) {
    List<Comment> comments = commentRepository.findAllByPostId(postId);
    return getListCommentDto(comments);
  }

  public CommentDto deleteComment(Long postId, Long commentId) {
    Post post = getPost(postId);
    Comment commentForDelete = getCommentForDelete(commentId, post.getComments());
    commentRepository.delete(commentForDelete);
    return mapper.toDto(commentForDelete);
  }

  private List<CommentDto> getListCommentDto(List<Comment> comments) {
    return comments.stream()
            .sorted(Comparator.comparing(Comment::getCreatedAt))
            .map(mapper::toDto).toList();
  }

  private Comment getCommentFromDatabase(CommentDto dto) {
    return commentRepository
            .findById(dto.getId())
            .orElseThrow(() -> new RuntimeException(MESSAGE_COMMENT_NOT_EXIST));
  }

  private Post getPost(Long postId) {
    return postRepository.findById(postId).orElseThrow(() -> new RuntimeException(MESSAGE_POST_NOT_IN_DB));
  }

  private void validatePostId(Long postId, Comment comment) {
    if (!Objects.equals(comment.getPost().getId(), postId)) {
      throw new RuntimeException(MESSAGE_POST_ID_AND_COMMENT_POST_ID_NOT_EQUAL);
    }
  }

  private String validateCommentContent(CommentDto dto) {
    String content = dto.getContent();
    if (content.isEmpty() || content.isBlank() || content.length() > MAX_LEN_CONTENT) {
      throw new RuntimeException(MESSAGE_INVALID_TEXT_OF_COMMENT);
    }
    return content;
  }

  private Comment getCommentForDelete(Long commentId, List<Comment> comments) {
    return comments.stream()
            .filter(comment -> commentId.equals(comment.getId()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException(MESSAGE_COMMENT_NOT_EXIST));
  }
}
