package faang.school.postservice.service;

import faang.school.postservice.dto.comment.CommentResponse;
import faang.school.postservice.dto.comment.CommentUpdateRequest;
import faang.school.postservice.dto.comment.CreateCommentRequest;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final ValidateService validateService;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;

    @Transactional
    public CommentResponse create(@Valid CreateCommentRequest createCommentRequest) {
        validateService.validateUser(createCommentRequest.userId());
        validateService.validatePost(createCommentRequest.postId());

        Comment comment = commentMapper.toEntity(createCommentRequest);
        return commentMapper.toCommentResponse(commentRepository.save(comment));
    }

    @Transactional
    public CommentResponse update(@Valid CommentUpdateRequest updateRequest) {
        Comment comment = getComment(updateRequest.id());
        comment.setContent(updateRequest.content());
        return commentMapper.toCommentResponse(comment);
    }

    private Comment getComment(Long commentId) {
        if (commentId == null) {
            throw new IllegalArgumentException("commentId cannot be null");
        }

        return commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment with id " + commentId + " not found"));
    }

    @Transactional(readOnly = true)
    public List<CommentResponse> getAllByPostId(@Valid @NotNull @Positive Long postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId);

        return comments.stream()
                .sorted((c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt()))
                .map(commentMapper::toCommentResponse)
                .toList();
    }

    @Transactional
    public void delete(@Valid @NotNull @Positive Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new EntityNotFoundException("Comment with id " + commentId + " not found");
        }
        commentRepository.deleteById(commentId);
    }
}
