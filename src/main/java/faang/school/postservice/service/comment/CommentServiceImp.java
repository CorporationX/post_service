package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.service.validation.UserValidationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentServiceImp implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final PostService postService;
    private final UserValidationService userValidationService;


    @Override
    public CommentDto createComment(CommentDto request) {
        log.info("Create comment: %s".formatted(request.getContent()));
        Post post = validateRequestAndGetPost(request);
        Comment comment = commentMapper.toEntity(request);
        comment.setPost(post);
        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto updateCommentContent(CommentDto request) {
        Long commentId = request.getId();
        Long postId = request.getPostId();
        log.info("Update comment with ID: %d".formatted(postId));
        Comment comment = findCommentById(commentId);
        commentMapper.updateCommentContent(comment, request);
        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public List<CommentDto> getAllComments(Long postId) {
        log.info("Getting all comments by PostId: %d.".formatted(postId));
        Post post = getPost(postId);
        log.info("PostId with id: %d is present.".formatted(post.getId()));
        return commentRepository.findAllByPostId(postId).stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                .map(commentMapper::toCommentDto)
                .toList();
    }

    @Override
    public void deleteComment(long id) {
        log.info("Delete comment with ID=%d. ".formatted(id));
        commentRepository.deleteById(id);
        log.info("Comment with ID=%d deleted successfully.".formatted(id));
    }

    private Comment findCommentById(long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("There are no comment with ID=%d".formatted(id)));
    }

    private Post validateRequestAndGetPost(CommentDto commentDto) {
        Long authorId = commentDto.getAuthorId();
        if (authorId != null) {
            userValidationService.validateUserExists(authorId);
        }
        return getPost(commentDto.getPostId());
    }

    private Post getPost(Long postId) {
        return postService.getPost(postId)
                .orElseThrow(() -> new EntityNotFoundException("There are no Post with ID:%d.".formatted(postId)));
    }
}
