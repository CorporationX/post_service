package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.exception.ExceptionMessages;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.messaging.redis.publisher.comment.CommentEventPublisher;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validator.comment.UserClientValidation;
import jakarta.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserClientValidation userClientValidation;
    private final PostService postService;
    private final CommentMapper commentMapper;
    private final PostMapper postMapper;
    private final CommentEventPublisher commentEventPublisher;

    public CommentDto addNewCommentInPost(CommentDto commentDto) {
        userClientValidation.checkUser(commentDto.getAuthorId());
        Comment comment = commentMapper.toEntity(commentDto);
        comment.setPost(postMapper.toEntity(postService.getById((commentDto.getPostId()))));
        comment.setLikes(new ArrayList<>());
        try {
            commentRepository.save(comment);
        } catch (Exception e) {
            log.error(ExceptionMessages.FAILED_PERSISTENCE, e);
            throw new PersistenceException(ExceptionMessages.FAILED_PERSISTENCE, e);
        }

        commentEventPublisher.publish(commentMapper.toEvent(comment));
        return commentMapper.toDto(comment);
    }

    public CommentDto updateExistingComment(CommentDto commentDto) {
        userClientValidation.checkUser(commentDto.getAuthorId());
        Comment comment = commentRepository.findById(commentDto.getId()).orElseThrow(() -> {
            log.error(ExceptionMessages.COMMENT_NOT_FOUND);
            return new NoSuchElementException(ExceptionMessages.COMMENT_NOT_FOUND);
        });
        comment.setContent(commentDto.getContent());
        commentRepository.save(comment);
        return commentMapper.toDto(comment);
    }

    public List<CommentDto> getCommentsForPost(Long postId) {
        return postService.getById(postId).getComments();
    }

    public CommentDto deleteExistingCommentInPost(CommentDto commentDto) {
        commentRepository.deleteById(commentDto.getId());
        return commentDto;
    }
}
