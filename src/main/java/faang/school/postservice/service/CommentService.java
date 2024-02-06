package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.CommentEditDto;
import faang.school.postservice.exceptions.DataValidationException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.validator.CommentValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final PostService postService;
    private final CommentValidator commentValidator;
    private final UserContext userContext;
    private final UserServiceClient userServiceClient;

    @Transactional
    public CommentDto createComment(Long postId, CommentDto commentDto) {
        userServiceClient.getUser(userContext.getUserId()); // проверка на существование юзера
        var comment = commentMapper.toEntity(commentDto);
        var post = postService.getPostById(postId);
        comment.setAuthorId(userContext.getUserId());// владелец запроса становится владельцем комментария
        comment.setPost(post); // как я понял, это и называется "проставляет этой сущности связь с сущностями Post"
        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Transactional
    public CommentDto updateComment(Long postId, Long commentId, CommentEditDto commentEditDto) {
        Comment commentToUpdate = getCommentFromPost(postId, commentId);
        commentValidator.checkOwnerComment(commentToUpdate.getAuthorId(), userContext.getUserId());
        commentToUpdate.setContent(commentEditDto.getContent());
        commentRepository.save(commentToUpdate);
        return commentMapper.toDto(commentToUpdate);
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId);
        return commentMapper.toDtoList(comments.stream()
                .sorted((x, y) -> y.getCreatedAt().compareTo(x.getCreatedAt()))
                .toList());
    }

    @Transactional //is it reading?
    public void deleteComment(Long postId, Long commentId) {
        var comment = getCommentFromPost(postId, commentId);
        commentValidator.checkOwnerComment(comment.getAuthorId(), userContext.getUserId());
        commentRepository.delete(comment);
    }

    private Comment getCommentFromPost(Long postId, Long commentId) {
        var post = postService.getPostById(postId);
        List<Comment> comments = post.getComments();
        return comments.stream()
                .filter(commentFromList -> commentId.equals(commentFromList.getId()))
                .findAny()
                .orElseThrow(() -> new DataValidationException("Comment has not been found"));
    }
}

