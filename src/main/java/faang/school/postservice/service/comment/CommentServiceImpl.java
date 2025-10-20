package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.Request.RequestCommentDto;
import faang.school.postservice.dto.comment.Response.ResponseCommentDto;
import faang.school.postservice.exception.ResourceNotFoundException;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final PostRepository postRepository;

    private final CommentMapper commentMapper;

    private final UserContext userContext;

    @Override
    public ResponseCommentDto createComment(RequestCommentDto commentDto,
                                            Long postId) throws ResourceNotFoundException {
        validateAuthorComment(commentDto);
        Post post = getPostById(postId);
        Comment comment = commentMapper.toEntity(commentDto);
        comment.setPost(post);
        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    public ResponseCommentDto updateComment(Long postId,
                                            Long idComment,
                                            RequestCommentDto commentDto) throws ResourceNotFoundException {
        Comment existingComment = getCommentById(idComment);
        validateAuthorComment(commentDto);
        validateCommentToPost(existingComment, getPostById(postId));
        existingComment.setContent(commentDto.getContent());
        return commentMapper.toDto(commentRepository.save(existingComment));
    }

    @Override
    public void deleteComment(Long postId, Long idComment) throws ResourceNotFoundException {
        Comment existingComment = getCommentById(idComment);
        validateCommentToPost(existingComment, getPostById(postId));
        commentRepository.deleteById(existingComment.getId());
    }

    @Override
    public List<ResponseCommentDto> getAllCommentsByPostId(Long postId) throws ResourceNotFoundException {
        Post post = getPostById(postId);
        return commentRepository.findAllByPostId(post.getId()).stream()
            .sorted((c1, c2) -> c2.getCreatedAt().compareTo(c1.getCreatedAt()))
            .map(commentMapper::toDto)
            .collect(Collectors.toList());
    }

    private void validateAuthorComment(RequestCommentDto commentDto) {
        long contextIdUser = userContext.getUserId();
        if (!Objects.equals(contextIdUser, commentDto.getAuthorId())) {
            log.warn("The user is trying to change someone else`s data ,{} - the user, {} - the Original user",
                contextIdUser, commentDto.getAuthorId());
            throw new IllegalArgumentException("You cannot changesomeone else's data");
        }
    }

    private void validateCommentToPost(Comment existingComment, Post postById) {
        if (!existingComment.getPost().getId().equals(postById.getId())) {
            throw new IllegalArgumentException("Comment does not belong to the specified post");
        }
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