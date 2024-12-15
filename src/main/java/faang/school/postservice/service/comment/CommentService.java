package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.event.CommentEvent;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.comment.CommentMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.CommentEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validator.comment.CommentValidator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@AllArgsConstructor
public class CommentService {
    private final CommentEventPublisher commentEventPublisher;
    private final CommentValidator commentValidator;
    private final PostService postService;
    private final CommentMapperImpl commentMapper;
    private final CommentRepository commentRepository;

    public Comment findEntityById(long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new DataValidationException(String.format("Comment with id '%s' not found", id)));
    }

    public CommentDto createComment(CommentDto commentDto) {
        commentValidator.validateCreation(commentDto);
        Post post = postService.findEntityById(commentDto.getPostId());

        commentDto.setCreatedAt(LocalDateTime.now());
        commentDto.setUpdatedAt(LocalDateTime.now());

        Comment comment = commentMapper.toEntity(commentDto);
        comment.setPost(post);

        comment = commentRepository.save(comment);

        CommentEvent event = CommentEvent.builder()
                .commentId(comment.getId())
                .authorId(commentDto.getAuthorId())
                .postId(commentDto.getPostId())
                .date(LocalDateTime.now())
                .build();

        commentEventPublisher.publish(event);

        return commentMapper.toDto(comment);
    }

    public CommentDto updateComment(CommentDto commentDto) {
        if (commentDto.getId() == null) {
            throw new DataValidationException("Comment id is required for update");
        }
        Comment comment = findEntityById(commentDto.getId());
        commentValidator.validateUpdate(comment, commentDto);
        commentDto.setUpdatedAt(LocalDateTime.now());

        commentMapper.update(comment, commentDto);
        commentRepository.save(comment);
        return commentMapper.toDto(comment);
    }

    public List<CommentDto> getComments(long postId) {
        return commentRepository.findAllByPostId(postId)
                .stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt))
                .map(commentMapper::toDto)
                .toList();
    }

    public void deleteComment(long commentId) {
        commentRepository.deleteById(commentId);
    }

}
