package faang.school.postservice.service.comment;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.Request.RequestCreateComment;
import faang.school.postservice.dto.comment.Request.RequestUpdateComment;
import faang.school.postservice.dto.comment.Response.ResponseComment;
import faang.school.postservice.dto.event.CommentEvent;
import faang.school.postservice.exception.ResourceNotFoundException;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.CommentEventPublisher;
import faang.school.postservice.publisher.EventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.CommentValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final PostRepository postRepository;

    private final CommentMapper commentMapper;

    private final UserContext userContext;

    private final CommentValidator commentValidator;

    private final CommentEventPublisher commentEventPublisher;

    private final EventPublisher eventPublisher;

    @Override
    public ResponseComment createComment(RequestCreateComment requestCreateComment,
                                         Long postId) {
        Long authorId = userContext.getUserId();
        Post post = getPostById(postId);
        Comment comment = commentMapper.toEntity(requestCreateComment);
        comment.setAuthorId(authorId);
        comment.setPost(post);
        Comment savedComment = commentRepository.save(comment);

        commentEventPublisher.publish(postId,
                authorId,
                savedComment.getId(),
                LocalDateTime.now());

        CommentEvent event = new CommentEvent(
                authorId,
                postId,
                savedComment.getId(),
                savedComment.getContent()
        );
        eventPublisher.publish(event);

        return commentMapper.toDto(savedComment);
    }

    @Override
    public ResponseComment updateComment(Long postId,
                                         Long idComment,
                                         RequestUpdateComment requestCreateComment) {
        Comment existingComment = getCommentById(idComment);
        Long authorId = userContext.getUserId();
        commentValidator.validateCommentToPost(existingComment, getPostById(postId));
        existingComment.setContent(requestCreateComment.content());
        existingComment.setAuthorId(authorId);
        return commentMapper.toDto(commentRepository.save(existingComment));
    }

    @Override
    public void deleteComment(Long postId, Long idComment) {
        Comment existingComment = getCommentById(idComment);
        commentValidator.validateCommentToPost(existingComment, getPostById(postId));
        commentRepository.deleteById(existingComment.getId());
    }

    @Override
    public List<ResponseComment> getAllCommentsByPostId(Long postId) {
        Post post = getPostById(postId);
        return commentRepository.findAllByPostId(post.getId()).stream()
            .sorted((c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt()))
            .map(commentMapper::toDto)
            .collect(Collectors.toList());
    }

    private Comment getCommentById(Long id) throws ResourceNotFoundException {
        return commentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + id));
    }

    private Post getPostById(Long postId) throws ResourceNotFoundException {
        return postRepository.findById(postId)
            .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
    }
}