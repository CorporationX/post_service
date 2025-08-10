package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.dto.comment.CommentViewDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.ForbiddenException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper mapper;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final UserContext userContext;

    @Override
    public CommentViewDto create(Long postId, CommentCreateDto commentDto) {
        var authorId = userContext.getUserId();

        try {
            userServiceClient.getUser(authorId);
        } catch (FeignException.NotFound e) {
            throw new EntityNotFoundException("User with id " + authorId + " not found");
        }

        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException("Post with id " + postId + " not found");
        }

        var post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        var comment = mapper.toEntity(commentDto);

        comment.setAuthorId(authorId);
        comment.setPost(post);

        var savedComment = commentRepository.save(comment);

        return mapper.toViewDto(savedComment);
    }

    @Override
    public CommentViewDto update(Long postId, Long commentId, CommentUpdateDto commentDto) {
        var comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));

        var currentUserId = userContext.getUserId();

        if (!comment.getAuthorId().equals(currentUserId)) {
            throw new ForbiddenException("User is not the author of the comment");
        }

        comment.setContent(commentDto.content());

        var updatedComment = commentRepository.save(comment);
        return mapper.toViewDto(updatedComment);
    }

    @Override
    public List<CommentViewDto> getAllByPostId(Long postId) {
        var comments = commentRepository.findAllByPostIdOrderByCreatedAtDesc(postId);
        return comments.stream()
                .map(mapper::toViewDto)
                .toList();
    }

    @Override
    public void delete(Long commentId) {
        var comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));

        var currentUserId = userContext.getUserId();

        if (!comment.getAuthorId().equals(currentUserId)) {
            throw new ForbiddenException("You are not the author of this comment");
        }

        commentRepository.deleteById(commentId);
    }
}