package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.event.CommentEvent;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.CommentEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.comment.CommentValidator;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentMapper commentMapper;
    private final CommentValidator commentValidator;
    private final CommentEventPublisher commentEventPublisher;

    public CommentDto createComment(long postId, CommentDto commentDto) {
        commentValidator.checkCommentAuthor(commentDto);
        Optional<Post> optionalPost = postRepository.findById(postId);
        Post post = optionalPost.orElseThrow(() -> new EntityNotFoundException("Post with Id " + postId + " not found."));
        Comment commentEntity = commentMapper.toEntity(commentDto);
        commentEntity.setPost(post);
        Comment savedComment = commentRepository.save(commentEntity);
        CommentEvent commentEvent = CommentEvent.builder()
                .authorCommentId(savedComment.getAuthorId())
                .authorPostId(post.getAuthorId())
                .commentId(savedComment.getId())
                .commentText(savedComment.getContent())
                .postId(post.getId())
                .build();
        commentEventPublisher.publish(commentEvent);
        return commentMapper.toDto(savedComment);
    }

    public CommentDto updateComment(CommentDto commentDto) {
        Long commentDtoId = commentDto.getId();
        Comment commentEntity = commentRepository.findById(commentDtoId)
                .orElseThrow(() -> new EntityNotFoundException("Comment with Id " + commentDtoId + " not found"));
        commentEntity.setContent(commentDto.getContent());

        return commentMapper.toDto(commentRepository.save(commentEntity));
    }

    public void deleteComment(long commentId) {
        commentRepository.deleteById(commentId);
    }

    public List<CommentDto> getAllComments(long postId) {
        List<Comment> commentByPostId = commentRepository.findAllByPostId(postId);
        return commentMapper.toDtoList(commentByPostId);
    }
}
