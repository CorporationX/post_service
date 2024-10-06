package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentRequestDto;
import faang.school.postservice.dto.comment.CommentResponseDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.comment.async.CommentServiceAsync;
import faang.school.postservice.validator.comment.CommentValidator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final CommentValidator commentValidator;
    private final CommentServiceAsync commentServiceAsync;

    @Value("${comments.batch-size}")
    private int batchSize;

    @Override
    @Transactional
    public CommentResponseDto create(long userId, CommentRequestDto dto) {
        commentValidator.validateUser(userId);
        var post = commentValidator.findPostById(dto.postId());
        var comment = commentMapper.toEntity(dto);
        comment.setAuthorId(userId);
        comment.setPost(post);
        return commentMapper.toResponseDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public CommentResponseDto update(CommentRequestDto dto) {
        var comment = commentValidator.findCommentById(dto.id());
        comment.setContent(dto.content());
        return commentMapper.toResponseDto(commentRepository.save(comment));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponseDto> findAll(Long postId) {
        var comments = commentRepository.findAllByPostId(postId).stream()
                .sorted((c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt()))
                .toList();
        return commentMapper.toResponseDto(comments);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        commentRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void moderateComments() {
        List<Comment> unverifiedPosts = commentRepository.findAllByVerifiedDateIsNull();
        List<List<Comment>> batches = ListUtils.partition(unverifiedPosts, batchSize);

        batches.forEach(commentServiceAsync::moderateCommentsByBatches);
    }
}