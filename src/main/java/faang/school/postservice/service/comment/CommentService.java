package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validation.user.UserValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final PostRepository postRepository;
    private final UserValidator userValidator;

    @Transactional
    public CommentDto createComment(Long userId, Long postId, CommentDto commentDto) {
        userValidator.validateUserExist(userId);
        Comment comment = getComment(userId, postId, commentDto);
        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Transactional
    public CommentDto updateComment(Long commentId, CommentDto commentDto) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(()
                -> new EntityNotFoundException(String.format("Comment with id %d not found", commentId)));
        comment.setContent(commentDto.getContent());
        return commentMapper.toDto(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId).stream()
                .sorted((comm1, comm2) -> comm2.getCreatedAt().compareTo(comm1.getCreatedAt()))
                .collect(Collectors.toList());
        return commentMapper.toDto(comments);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    @Transactional(readOnly = true)
    public List<Comment> findCommentsByVerified(boolean verified) {
        return commentRepository.findByVerified(verified);
    }

    private Comment getComment(Long userId, Long postId, CommentDto commentDto) {
        Comment comment = commentMapper.toEntity(commentDto);
        comment.setAuthorId(userId);
        comment.setPost(getPost(postId));
        return comment;
    }

    private Post getPost(Long postId) {
        return postRepository.findById(postId).orElseThrow(()
                -> new EntityNotFoundException(String.format("Post with id %d not found", postId)));
    }
}
