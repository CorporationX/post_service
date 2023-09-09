package faang.school.postservice.service;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.redis.CommentEventDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.CommentEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.validator.CommentValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentValidator commentValidator;
    private final CommentMapper commentMapper;
    private final PostService postService;
    private final CommentEventPublisher commentEventPublisher;


    @Transactional
    public CommentDto createComment(Long postId, CommentDto commentDto) {
        commentValidator.validateUserBeforeCreate(commentDto);
        Comment comment = commentMapper.toEntity(commentDto, postId);
        CommentDto createdComment = commentMapper.toDto(commentRepository.save(comment));
        sendCommentEvent(createdComment);
        return createdComment;
    }

    @Transactional
    public CommentDto updateComment(Long commentId, CommentDto commentDto) {
        Comment comment = checkCommentExists(commentId);
        commentValidator.validateBeforeUpdate(comment, commentDto);
        commentMapper.partialUpdate(commentDto, comment);
        comment.setUpdatedAt(LocalDateTime.now());
        return commentMapper.toDto(comment);
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByPostId(Long postId) {
        return commentRepository.findAllByPostIdOrderByCreatedAtDesc(postId)
                .stream()
                .map(commentMapper::toDto)
                .toList();
    }

    @Transactional
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    private Comment checkCommentExists(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment with id " + commentId + " not found"));
    }

    public void sendCommentEvent(CommentDto commentDto) {
        Post post = postService.getPostById(commentDto.getPostId());

        CommentEventDto commentEventDto = CommentEventDto.builder()
                .idComment(commentDto.getId())
                .authorIdComment(commentDto.getAuthorId())
                .postId(commentDto.getPostId())
                .postAuthorId(post.getAuthorId())
                .contentComment(commentDto.getContent())
                .createdAt(LocalDateTime.now().withNano(0))
                .build();
        commentEventPublisher.publish(commentEventDto);
    }
}
