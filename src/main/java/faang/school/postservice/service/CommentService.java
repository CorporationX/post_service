package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentEditDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.validator.CommentValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
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
        if(!userServiceClient.isUserExists(userContext.getUserId())){
            throw new DataValidationException("User does not exist");// проверка на существование юзера
        }
        var comment = commentMapper.toEntity(commentDto);
        var post = postService.getPostById(postId);
        comment.setAuthorId(userContext.getUserId());// владелец запроса становится владельцем комментария
        comment.setPost(post); // как я понял, это и называется "проставляет этой сущности связь с сущностями Post"
        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Transactional
    public CommentDto updateComment(Long commentId, CommentEditDto commentEditDto) {
        Comment commentToUpdate = getComment(commentId);
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

    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = getComment(commentId);
        commentValidator.checkOwnerComment(comment.getAuthorId(), userContext.getUserId());
        commentRepository.delete(comment);
    }

    public Comment getComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new DataValidationException("Comment has not been found"));
    }
}

