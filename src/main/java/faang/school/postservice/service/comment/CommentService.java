package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.ExceptionMessages;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.post.PostService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserServiceClient userServiceClient;
    private final PostService postService;
    private final CommentMapper commentMapper;

    public CommentDto addNewCommentInPost(CommentDto commentDto) {
        validateAuthorExists(commentDto.getAuthorId());
        Comment comment = commentMapper.toEntity(commentDto);
        comment.setPost(postService.getPost(commentDto.getPostId()));
        comment.setLikes(new ArrayList<>());
        try {
            commentRepository.save(comment);
        } catch (Exception e) {
            log.error(ExceptionMessages.FAILED_PERSISTENCE, e);
            throw new PersistenceException(ExceptionMessages.FAILED_PERSISTENCE, e);
        }
        return commentMapper.toDto(comment);
    }

    public CommentDto updateExistingComment(CommentDto commentDto) {
        validateAuthorExists(commentDto.getAuthorId());
        Comment comment = commentRepository.findById(commentDto.getId()).orElseThrow(() -> {
                    log.error(ExceptionMessages.COMMENT_NOT_FOUND);
                    return new NoSuchElementException(ExceptionMessages.COMMENT_NOT_FOUND);
                });
        comment.setContent(commentDto.getContent());
        commentRepository.save(comment);
        return commentMapper.toDto(comment);
    }

    public List<CommentDto> getCommentsForPost(Long postId) {
        return postService.getPost(postId).getComments().stream()
                .map(commentMapper::toDto)
                .toList();
    }

    public CommentDto deleteExistingCommentInPost(CommentDto commentDto) {
        commentRepository.deleteById(commentDto.getId());
        return commentDto;
    }

    private void validateAuthorExists(long authorId) {
        UserDto user = userServiceClient.getUser(authorId);
        if (user == null) {
            log.error(ExceptionMessages.COMMENT_NOT_FOUND);
            throw new EntityNotFoundException(ExceptionMessages.COMMENT_NOT_FOUND);
        }
    }
}
