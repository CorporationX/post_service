package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.post.PostService;
import feign.FeignException;
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
    private final UserServiceClient userServiceClient;
    private final PostService postService;


    @Override
    public CommentDto createComment(CommentDto request) {
        log.info("Create comment: %s".formatted(request.getContent()));
        validateUser(request);
        Comment comment = commentMapper.toEntity(request);
        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto updateCommentContent(long id, CommentDto request) {
        if (!request.getId().equals(id)) {
            throw new DataValidationException("The IDs in the path and in the request body do not match.");
        }
        log.info("Update comment with ID: %d".formatted(request.getPostId()));
        Comment comment = findCommentById(id);
        commentMapper.updateCommentContent(comment, request);
        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public List<CommentDto> getAllComments(CommentDto request) {
        log.info("Getting all comments by PostId: %d and AuthorId: %d"
                .formatted(request.getPostId(), request.getAuthorId()));

        validatePost(request);
        validateUser(request);

        return commentRepository.findAllByPostId(request.getPostId()).stream()
                .filter(comment -> request.getAuthorId() == null || comment.getAuthorId().equals(request.getAuthorId()))
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                .map(commentMapper::toCommentDto)
                .toList();
    }

    @Override
    public void deleteComment(long id) {
        log.info("Delete comment with ID=%d ".formatted(id));
        commentRepository.delete(findCommentById(id));
    }

    private Comment findCommentById(long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("There are no comment with ID=%d".formatted(id)));
    }

    private void validateUser(CommentDto commentDto) {
        Long authorId = commentDto.getAuthorId();
            try {
                log.info("Try to find user. Sending request to user_service. User ID: %d ".formatted(authorId));
                userServiceClient.getUser(authorId);
                log.info("User with ID:%d is present".formatted(authorId));
            } catch (FeignException e) {
                throw new DataValidationException("User with ID:%d is not present".formatted(authorId), e);
            }
    }

    private void validatePost(CommentDto commentDto) {
        Long postId = commentDto.getPostId();
        postService.getPost(postId)
                .orElseThrow(() -> new DataValidationException("There are no Post with ID:%d.".formatted(postId)));
    }
}
