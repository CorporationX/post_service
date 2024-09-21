package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final CommentMapper commentMapper;

    @Override
    public CommentDto addComment(CommentDto commentDto) {
        userServiceClient.getUser(commentDto.getAuthorId());

        Post post = postRepository
                .findById(commentDto.getPostId())
                .orElseThrow(() -> new EntityNotFoundException(String.format("Post with ID %s not found", commentDto.getPostId())));

        Comment comment = commentMapper.toComment(commentDto);
        comment.setPost(post);

        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto updateComment(Long id, String content) {
        return null;
    }

    @Override
    public List<CommentDto> getCommentsByPostId(Long postId) {
        return List.of();
    }

    @Override
    public void deleteComment(Long id) {

    }
}
