package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
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
    public CommentDto create(Long postId, CommentDto commentDto) {
        validateComment(postId, commentDto);
        Comment comment = commentRepository.save(mapper.toEntity(commentDto));
        return mapper.toDto(comment);
    }

    @Override
    public CommentDto update(Long commentId, CommentUpdateDto commentUpdateDto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment with id " + commentId + " does not exist"));
        comment.setContent(commentUpdateDto.getContent());
        return mapper.toDto(commentRepository.save(comment));
    }

    @Override
    public List<CommentDto> getByPostId(Long postId) {
        return commentRepository.findAllByPostId(postId)
                .stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public void delete(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    private void validateComment(Long postId, CommentDto commentDto) {
        if (!postRepository.existsById(postId)) {
            throw new EntityNotFoundException("Post with id " + postId + " does not exist");
        }
        UserDto user = userServiceClient.getUser(commentDto.getAuthorId());
        if (user == null) {
            throw new EntityNotFoundException("User with id " + commentDto.getAuthorId() + " does not exist");
        }
    }
}
