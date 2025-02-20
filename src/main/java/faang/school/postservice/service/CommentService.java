package faang.school.postservice.service;

import faang.school.postservice.dto.comment.CommentForListDto;
import faang.school.postservice.dto.comment.CreateCommentRequest;
import faang.school.postservice.dto.comment.CreateCommentResponse;
import faang.school.postservice.dto.comment.UpdateCommentRequest;
import faang.school.postservice.dto.comment.UpdatedCommentResponse;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.validator.CommentValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final PostService postService;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final CommentValidator commentValidator;

    @Transactional
    public CreateCommentResponse createComment(CreateCommentRequest createCommentRequest) {
        Post post = postService.getPost(createCommentRequest.getPostId());
        Comment comment = commentMapper.toEntity(createCommentRequest);
        commentValidator.verificationCreatingData(comment);

        comment.setPost(post);
        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toCreateResponse(savedComment);
    }

    @Transactional
    public UpdatedCommentResponse updateComment(UpdateCommentRequest updateCommentRequest) {
        Comment comment = getComment(updateCommentRequest.getId());
        commentValidator.validateForUpdate(comment, updateCommentRequest);

        commentMapper.updateComment(comment, updateCommentRequest);
        commentRepository.save(comment);
        return commentMapper.toUpdateResponse(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentForListDto> getListComment(Long postId) {
        return commentRepository.findAllByPostId(postId).stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                .map(commentMapper::toListDto)
                .toList();

    }

    @Transactional
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(getComment(commentId).getId());
    }

    public Comment getComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found with id: " + commentId));
    }
}