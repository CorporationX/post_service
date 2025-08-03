package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentForCreationDto;
import faang.school.postservice.dto.comment.CommentForUpdateDto;
import faang.school.postservice.dto.comment.CommentOutputDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.CommentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final UserContext userContext;

    @Override
    public CommentOutputDto createComment(CommentForCreationDto commentDto) {
        Long postId = commentDto.getPostId();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post with id %d doesn't exist".formatted(postId)));
        long userId = userContext.getUserId();
        UserDto commentAuthor = userServiceClient.getUser(userId);
        Comment commentEntity = commentMapper.toEntity(commentDto);
        commentEntity.setAuthorId(userId);
        commentEntity.setCreatedAt(LocalDateTime.now());
        commentEntity.setPost(post);
        Comment savedComment = commentRepository.save(commentEntity);
        log.info("Creating a comment by user {} for post with id {} - Finished"
                , userContext.getUserId(), commentDto.getPostId());
        putAuthorIntoCache(commentAuthor);
        return commentMapper.toDto(savedComment);
    }

    @Override
    public CommentOutputDto updateComment(CommentForUpdateDto commentDto) {
        Comment existingComment = findById(commentDto.getId());
        validateCommentAuthor(existingComment);
        Comment commentEntity = commentMapper.updateEntityFromDto(commentDto, existingComment);
        commentEntity.setUpdatedAt(LocalDateTime.now());
        Comment updatedComment = commentRepository.save(commentEntity);
        log.info("Update a comment with id {} by user {} - Finished"
                , commentDto.getId(), userContext.getUserId());
        return commentMapper.toDto(updatedComment);
    }

    @Override
    public CommentOutputDto findCommentById(long commentId) {
        return commentMapper.toDto(findById(commentId));
    }

    @Override
    public void deleteCommentById(long commentId) {
        commentRepository.deleteById(commentId);
        log.info("A comment with id {} has been deleted by user {}"
                , commentId, userContext.getUserId());
    }

    @Override
    public List<CommentOutputDto> findCommentByPostId(long postId) {
        List<Comment> commentList = commentRepository.findAllByPostId(postId).stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt))
                .toList();
        return commentMapper.toListDto(commentList);
    }

    private Comment findById(long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException
                        (String.format("There is no comment with id %d", commentId)));
    }

    private void validateCommentAuthor(Comment existingComment) {
        long userId = userContext.getUserId();
        if (existingComment.getAuthorId() != userId) {
            throw new DataValidationException("Comment can be changed only by their authors");
        }
        userServiceClient.getUser(userId);
    }

    @CachePut(value = "authors", key = "#userDto.id")
    public UserDto putAuthorIntoCache(UserDto userDto) {
        return userDto;
    }
}
