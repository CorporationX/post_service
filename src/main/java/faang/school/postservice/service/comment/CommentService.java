package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static faang.school.postservice.util.GlobalValidator.validateOptional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final PostRepository postRepository;

    @Transactional
    public CommentDto createComment(Long userId, Long postId, CommentDto commentDto) {
        Comment comment = getComment(userId, postId, commentDto);
        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Transactional
    public CommentDto updateComment(Long commentId, CommentDto commentDto) {
        Comment comment = validateOptional(commentRepository.findById(commentId), "Comment not found");
        comment.setContent(commentDto.getContent());
        return commentMapper.toDto(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId).stream()
                .sorted((comm1, comm2) -> comm2.getUpdatedAt().compareTo(comm1.getUpdatedAt()))
                .collect(Collectors.toList());
        return commentMapper.toDto(comments);
    }

    @Transactional
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
