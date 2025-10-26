package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.exeption.ResourceNotFoundException;
import faang.school.postservice.exeption.ValidationException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.comment.CommentValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final CommentValidator commentValidator;

    @Transactional
    public Comment create(CommentCreateDto commentCreateDto, Long userId) {
        log.info("Creating comment for postId={} by userId={}", commentCreateDto.postId(), userId);
        commentValidator.validateCommentContent(commentCreateDto.content());
        commentValidator.validateUser(userId, userServiceClient);

        Post post = postRepository.findById(commentCreateDto.postId())
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        Comment comment = CommentMapper.toEntity(commentCreateDto, post, userId);
        commentRepository.save(comment);

        return comment;
    }

    @Transactional
    public Comment update(Long commentId, CommentUpdateDto dto, Long userId) {
        log.info("Updating comment id={} by userId={}", commentId, userId);

        Comment existing = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        if (!existing.getAuthorId().equals(userId)) {
            throw new ValidationException("You can't edit someone else's comment.");
        }
        commentValidator.validateCommentContent(dto.content());

        existing.setContent(dto.content());
        existing.setLargeImageFileKey(dto.largeImageFileKey());
        existing.setSmallImageFileKey(dto.smallImageFileKey());

        return commentRepository.save(existing);
    }

    public List<CommentDto> getByPostId(Long postId) {
        log.info("Fetching comments for postId={}", postId);

        postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        return commentRepository.findAllByPostId(postId).stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                .map(CommentMapper::toDto)
                .toList();
    }

    public void delete(Long commentId, Long userId) {
        log.info("Deleting comment id={} by userId={}", commentId, userId);

        Comment existing = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        if (!existing.getAuthorId().equals(userId)) {
            throw new ValidationException("You can't delete someone else's comment.");
        }

        commentRepository.delete(existing);
    }

    public Comment getById(Long commentId) {
        log.info("Fetching comment by id={}", commentId);

        return commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
    }
}
