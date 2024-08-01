package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.event.CommentEvent;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.producer.kafka.CommentProducer;
import faang.school.postservice.publisher.CommentEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validator.UserValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService{
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final PostRepository postRepository;
    private final UserValidator userValidator;
    private final CommentEventPublisher commentEventPublisher;
    private final PostService postService;
    private final CommentProducer commentProducer;

    @Transactional
    public CommentDto createComment(Long userId, Long postId, CommentDto commentDto) {
        userValidator.validateUserExist(userId);
        Comment comment = createCommentEntity(userId, postId, commentDto);
        commentRepository.save(comment);
        CommentEvent commentEvent = CommentEvent.builder()
                .commentAuthorId(comment.getAuthorId())
                .postAuthorId(comment.getPost().getAuthorId())
                .postId(comment.getPost().getId())
                .commentId(comment.getId())
                .build();
        commentEventPublisher.publish(commentEvent);
        CommentDto resultDto = commentMapper.toDto(comment);
        sendCreatingCommentEventToKafka(resultDto);
        return resultDto;
    }

    private void sendCreatingCommentEventToKafka(CommentDto commentDto) {
        CommentEvent commentEvent = commentMapper.toEvent(commentDto);
        commentProducer.sendEvent(commentEvent);
    }

    @Transactional
    public CommentDto updateComment(Long commentId, CommentDto commentDto) {
        Comment comment = commentRepository.findById(commentId).orElseThrow(()
                -> new EntityNotFoundException(String.format("Comment with id %d not found", commentId)));
        comment.setContent(commentDto.getContent());
        return commentMapper.toDto(comment);
    }

    @Transactional
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

    @Override
    public boolean existsById(long id) {
        return postRepository.existsById(id);
    }

    private Comment createCommentEntity(Long userId, Long postId, CommentDto commentDto) {
        Comment comment = commentMapper.toEntity(commentDto);
        comment.setAuthorId(userId);
        comment.setPost(postService.getPostById(postId));
        return comment;
    }
}
