package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.EntityWrongParameterException;
import faang.school.postservice.exception.NoAccessException;
import faang.school.postservice.mapper.post.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;
    private final UserServiceClient userServiceClient;
    private final CommentMapper commentMapper;

    public CommentDto createComment(CommentDto commentDto) {
        validateCommentDto(commentDto);
        Comment comment = commentMapper.fromDto(commentDto);
        comment.setCreatedAt(LocalDateTime.now());
        comment = commentRepository.save(comment);
        return commentMapper.toDto(comment);
    }

    public CommentDto updateComment(long id, CommentDto commentDto) {
        validateCommentDto(commentDto);
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Комментарий не найден"));
        if (comment.getAuthorId() != commentDto.getAuthorId()) {
            throw new EntityWrongParameterException("Пользователь может исправлять только свои собственные комментарии");
        }
        comment.setContent(commentDto.getContent());
        comment = commentRepository.save(comment);
        return commentMapper.toDto(comment);
    }

    public List<CommentDto> getAllCommentsForPost(long postId) {
        postService.getPostById(postId);
        List<Comment> allComments = commentRepository.findAllByPostId(postId);
        List<Comment> sortedComments = allComments.stream()
                .filter(comment -> comment.getPost().getId() == postId)
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                .toList();
        return sortedComments.stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }

    public void deleteComment(long commentId, long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Комментарий не найден"));
        if (comment.getAuthorId() != userId) {
            throw new NoAccessException("Пользователь не имеет права удалять этот комментарий");
        }
        commentRepository.deleteById(commentId);
    }

    private void validateCommentDto(CommentDto commentDto) {
        if (commentDto.getContent() == null || commentDto.getContent().isEmpty() || commentDto.getContent().length() > 4096) {
            throw new EntityWrongParameterException("Содержание комментария должно быть предоставлено и не может быть пустым или содержать более 4096 символов");
        }
        if (commentDto.getAuthorId() == null) {
            throw new EntityWrongParameterException("Необходимо указать id автора");
        }
        if (commentDto.getPostId() == null) {
            throw new EntityWrongParameterException("Необходимо указать id сообщения");
        }
        userServiceClient.getUser(commentDto.getAuthorId());
    }
}
