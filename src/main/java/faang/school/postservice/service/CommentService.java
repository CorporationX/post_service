package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.exception.CommentNotFoundException;
import faang.school.postservice.exception.UserNotFoundException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostInternalService postInternalService;
    private final UserServiceClient userServiceClient;
    private final CommentMapper commentMapper;
    private final CommentValidator commentValidator;

    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByPostId(Long postId) {
        postInternalService.findPostById(postId);

        return commentRepository.findAllByPostId(postId).stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                .map(commentMapper::toDto)
                .toList();
    }

    @Transactional
    public CommentDto createComment(CommentDto dto) {
        commentValidator.validateCommentCreate(dto);

        Post post = postInternalService.findPostById(dto.getPostId());

        try {
            userServiceClient.getUser(dto.getAuthorId());
        } catch (FeignException e) {
            throw new UserNotFoundException("User with id = " + dto.getAuthorId() + " was not found");
        }

        Comment comment = commentMapper.toEntity(dto);
        comment.setPost(post);
        comment.setAuthorId(dto.getAuthorId());

        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Transactional
    public CommentDto updateComment(Long commentId, CommentDto dto, Long userId) {
        Comment existing = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("There is no comment with id: " + commentId));

        commentValidator.validateAuthor(existing, userId);
        commentValidator.validateCommentUpdate(dto);

        existing.setContent(dto.getContent());

        return commentMapper.toDto(commentRepository.save(existing));
    }

    @Transactional
    public CommentDto deleteComment(Long commentId, Long userId) {
        Comment existing = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("There is no comment with id: " + commentId));

        commentValidator.validateAuthor(existing, userId);

        commentRepository.delete(existing);

        return commentMapper.toDto(existing);
    }

    @Transactional(readOnly = true)
    public Comment getCommentById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new CommentNotFoundException("There is no comment with id: " + id));
    }
}