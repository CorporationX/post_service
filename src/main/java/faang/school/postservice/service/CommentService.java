package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.CommentValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentValidator commentValidator;
    private final CommentMapper commentMapper;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;


    public CommentDto createComment(long postId, CommentDto commentDto) {
        commentValidator.validateCommentDto(commentDto);
        commentValidator.checkGetCreatedAtForZero(commentDto);
        userServiceClient.getUser(commentDto.getAuthorId());
        log.info("[{}] Validation successful for postId: {}, CommentDto: {}", "createComment", postId, commentDto);

        Comment comment = commentMapper.toComment(commentDto);
        log.info(" [{}] Mapping of CommentDto to Comment entity successful for postId: {}, " +
                "commentDto'{}', comment: {}", "createComment", postId, commentDto, comment);

        Post post = postRepository.findById(postId).orElseThrow(() ->
                new EntityNotFoundException("Post with id " + postId + " not found"));
        comment.setPost(post);
        log.info("[{}] Post with id: {} successfully linked to Comment", "createComment", postId);

        comment = commentRepository.save(comment);
        log.info("[{}] Comment successfully saved to DB with ID: {}", "createComment", comment.getId());

        return commentMapper.toCommentDto(comment);
    }

    public CommentDto updateComment(long postId, CommentDto commentDto) {
        commentValidator.validateCommentDto(commentDto);
        Comment OldComment = commentRepository.findById(commentDto.getId()).orElseThrow(() ->
                new DataValidationException("Comment is null"));
        commentValidator.checkingForCompliance(OldComment, commentDto);
        log.info("[{}] Validation successful for postId: {}, CommentDto: {}", "updateComment", postId, commentDto);

        Comment comment = commentMapper.toComment(commentDto);
        log.info(" [{}] Mapping of CommentDto to Comment entity successful for postId: {}, " +
                "commentDto'{}', comment: {}", "updateComment", postId, commentDto, comment);

        Post post = postRepository.findById(postId).orElseThrow(() ->
                new EntityNotFoundException("Post with id " + postId + " not found"));
        comment.setPost(post);
        log.info("[{}] Post with id: {} successfully linked to Comment", "updateComment", postId);

        comment = commentRepository.save(comment);
        log.info("[{}] Comment successfully updated to DB with ID: {}", "createComment", comment.getId());

        return commentMapper.toCommentDto(comment);
    }

    public List<CommentDto> getAllComment(long postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId);
        comments.sort(Comparator.comparing(Comment::getCreatedAt).reversed());

        return comments.stream()
                .map(commentMapper::toCommentDto)
                .toList();
    }

    public void deleteComment(long commentId) {
        commentRepository.deleteById(commentId);
    }
}
