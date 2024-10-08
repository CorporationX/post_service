package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentMapper mapper;
    private final UserServiceClient userServiceClient;

    @Override
    public CommentDto createComment(Long postId, CommentDto commentDto) {
        Post post = postRepository.findById(postId).orElseThrow(
                () -> new EntityNotFoundException(String.format("Post with id %s does not exist", postId)));
        validateUser(commentDto);
        Comment comment = commentRepository.save(mapper.toEntity(commentDto, post));
        return mapper.toDto(comment);
    }

    @Override
    public CommentDto updateComment(Long commentId, CommentUpdateDto commentUpdateDto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Comment with id %s does not exist", commentId)));
        comment.setContent(commentUpdateDto.getContent());
        return mapper.toDto(commentRepository.save(comment));
    }

    @Override
    public List<CommentDto> getComments(Long postId) {
        return commentRepository.findAllByPostId(postId)
                .stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    private void validateUser(CommentDto commentDto) {
        UserDto user = userServiceClient.getUser(commentDto.getAuthorId());
        if (user == null) {
            throw new IllegalStateException(String.format("User with id %s could not be retrieved (null returned)", commentDto.getAuthorId()));
        }
    }
}
