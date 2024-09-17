package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.validator.comment.CommentServiceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentServiceValidator validator;
    private final CommentMapper mapper;
    private final UserServiceClient userServiceClient;
    private final UserContext userContext;

    public CommentDto create(CommentDto commentDto) {
        // TODO create tests
        validator.validatePostExist(commentDto.getPostId());
        validator.validateCommentContent(commentDto.getContent());
        userContext.setUserId(commentDto.getAuthorId());
        userServiceClient.getUser(commentDto.getAuthorId());
        Comment comment = mapper.mapToComment(commentDto);
        return mapper.mapToCommentDto(commentRepository.save(comment));
    }

    public List<CommentDto> get(Long postId) {
        List<Comment> comments = commentRepository.findAllByPostId(postId);
        List<Comment> commentsSorted = comments.stream()
                .sorted(Comparator.comparing(Comment::getUpdatedAt).reversed())
                .toList();
        return mapper.mapToCommentDto(commentsSorted);
    }

    public void delete(Long commentId) {
        validator.validateCommentExist(commentId);
        commentRepository.deleteById(commentId);
    }

    public CommentDto update(Long commentId, CommentDto commentDto) {
        // TODO create tests
        validator.validateCommentExist(commentId);
        validator.validateCommentContent(commentDto.getContent());
        userContext.setUserId(commentDto.getAuthorId());
        userServiceClient.getUser(commentDto.getAuthorId());
        Comment comment = commentRepository.findById(commentId).get();
        comment.setContent(commentDto.getContent());
        return mapper.mapToCommentDto(commentRepository.save(comment));
    }
}
