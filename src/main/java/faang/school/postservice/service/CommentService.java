package faang.school.postservice.service;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validation.CommentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static faang.school.postservice.utils.GlobalValidator.validateOptional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentValidator commentValidator;
    private final CommentMapper commentMapper;
    private final PostRepository postRepository;

    @Transactional
    public CommentDto createComment(Long userId, Long postId, CommentDto commentDto) {
        commentValidator.validateCommentFields(commentDto);
        Comment comment = getComment(userId, postId, commentDto);
        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Transactional
    public CommentDto updateComment(Long commentId, CommentDto commentDto) {
        commentValidator.validateCommentUpdate(commentId, commentDto);
        Comment comment = commentMapper.toEntity(commentDto);
        comment.setId(commentId);
        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId).stream()
                .sorted(Comparator.comparing(Comment::getUpdatedAt))
                .collect(Collectors.toList());
        return commentMapper.toDto(comments);
    }

    @Transactional(readOnly = true)
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    private Comment getComment(Long userId, Long postId, CommentDto commentDto) {
        Comment comment = commentMapper.toEntity(commentDto);
        comment.setAuthorId(userId);
        comment.setPost(getPost(postId));
        return comment;
    }

    private Post getPost(Long postId) {
        return validateOptional(postRepository.findById(postId), "Post not found");
    }
}
