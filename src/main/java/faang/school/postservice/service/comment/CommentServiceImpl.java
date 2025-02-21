package faang.school.postservice.service.comment;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.events.CommentEvent;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.comment.CommentProducer;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.PostService;
import faang.school.postservice.validator.comment.CommentServiceValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final CommentServiceValidator validator;
    private final PostService postService;
    private final CommentProducer commentProducer;

    @Override
    public CommentDto createComment(CommentDto commentDto) {
        validator.validateCreateComment(commentDto);

        Post post = postService.findPostById(commentDto.getPostId())
                .orElseThrow(() -> new EntityNotFoundException("Post with id %s not found".formatted(commentDto.getPostId())));

        Comment comment = commentMapper.toEntity(commentDto);
        comment.setPost(post);

        CommentEvent commentEvent = new CommentEvent(
                commentDto.getId(),
                commentDto.getPostId(),
                commentDto.getAuthorId()
        );
        produceCommentCreationMessage(commentEvent);
        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto updateComment(CommentDto commentDto) {
        Comment comment = commentRepository.findById(commentDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Comment with id %s not found".formatted(commentDto.getId())));
        commentMapper.update(commentDto, comment);

        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    public List<CommentDto> getCommentsByPostId(Long postId) {
        validator.validatePostId(postId);
        return commentMapper.toDto(commentRepository.findAllByPostId(postId).stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                .toList()
        );
    }

    @Override
    public void deleteComment(Long commentId) {
        validator.validateCommentId(commentId);
        commentRepository.deleteById(commentId);
    }

    private void produceCommentCreationMessage(CommentEvent commentEvent) {
        log.info("Produce comment: {}", commentEvent);
        commentProducer.sendMessage(commentEvent);
    }
}
