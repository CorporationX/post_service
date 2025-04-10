package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.contants.ErrorMessage;
import faang.school.postservice.dto.comment.CommentRequestDto;
import faang.school.postservice.dto.comment.CommentResponseDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.InvalidCommentContentException;
import faang.school.postservice.exception.NotAuthorException;
import faang.school.postservice.exception.NullEntityException;
import faang.school.postservice.mapper.CommentRequestMapper;
import faang.school.postservice.mapper.CommentResponseMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static faang.school.postservice.contants.ErrorMessage.ERROR_NOT_AUTHOR_COMMENT;
import static faang.school.postservice.contants.ErrorMessage.ERROR_NULL_AUTHOR_ID;
import static faang.school.postservice.contants.ErrorMessage.ERROR_NULL_COMMENT_ID;
import static faang.school.postservice.contants.ErrorMessage.ERROR_NULL_CONTENT;
import static faang.school.postservice.contants.ErrorMessage.ERROR_NULL_POST_ID;
import static faang.school.postservice.contants.InfoMessage.INFO_CREATE_COMMENT;
import static faang.school.postservice.contants.InfoMessage.INFO_DELETE_COMMENT;
import static faang.school.postservice.contants.InfoMessage.INFO_GET_COMMENTS;
import static faang.school.postservice.contants.InfoMessage.INFO_UPDATE_COMMENT;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private static final int MAX_LENGTH_CHARACTER = 4096;

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentRequestMapper commentRequestMapper;
    private final CommentResponseMapper commentResponseMapper;
    private final UserServiceClient userServiceClient;

    public void createComment(CommentRequestDto commentRequestDto) {
        validateCreateComment(commentRequestDto);
        Post post = getPost(commentRequestDto.getPostId());
        Comment comment = commentRequestMapper.toComment(commentRequestDto);
        comment.setPost(post);
        comment.setAuthorId(commentRequestDto.getAuthorId());
        commentRepository.save(comment);
        log.info(INFO_CREATE_COMMENT, comment.getId(), commentRequestDto.getAuthorId(), commentRequestDto.getPostId());
    }

    public void updateComment(Long id, CommentUpdateDto commentUpdateDto) {
        validateContent(commentUpdateDto.getContent());
        validateId(commentUpdateDto.getAuthorId(), ERROR_NULL_AUTHOR_ID);
        Comment comment = getComment(id);
        isAuthorComment(comment, commentUpdateDto);
        comment.setContent(commentUpdateDto.getContent());
        commentRepository.save(comment);
        log.info(INFO_UPDATE_COMMENT, id, commentUpdateDto.getAuthorId());
    }

    public List<CommentResponseDto> getCommentsByPostId(Long postId) {
        getPost(postId);
        List<CommentResponseDto> commentResponseDto = commentRepository.findAllByPostId(postId).stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                .map(commentResponseMapper::toCommentDto)
                .toList();
        log.info(INFO_GET_COMMENTS, commentResponseDto.size(), postId);
        return commentResponseDto;
    }

    public void deleteComment(Long id) {
        getComment(id);
        commentRepository.deleteById(id);
        log.info(INFO_DELETE_COMMENT, id);
    }

    private Comment getComment(Long id) {
        validateId(id, ERROR_NULL_COMMENT_ID);
        return getEntity(() -> commentRepository.findById(id), ErrorMessage.getErrorNotFoundComment(id));
    }

    private Post getPost(Long id) {
        validateId(id, ERROR_NULL_POST_ID);
        return getEntity(() -> postRepository.findById(id), ErrorMessage.getErrorNotFoundPost(id));
    }

    private void validateCreateComment(CommentRequestDto commentDto) {
        validateContent(commentDto.getContent());
        validateId(commentDto.getAuthorId(), ERROR_NULL_AUTHOR_ID);
        validateId(commentDto.getPostId(), ERROR_NULL_POST_ID);
        validateUserFromClient(commentDto.getAuthorId());
    }

    private void validateContent(String content) {
        if (content == null) {
            log.error(ERROR_NULL_CONTENT);
            throw new NullEntityException(ERROR_NULL_CONTENT);
        }
        if (content.isBlank() || content.length() > MAX_LENGTH_CHARACTER) {
            String errorMessage = ErrorMessage.getErrorWrongFormatContent(MAX_LENGTH_CHARACTER);
            log.error(errorMessage);
            throw new InvalidCommentContentException(errorMessage);
        }
    }

    private void validateId(Long id, String errorMessage) {
        if (id == null) {
            log.error(errorMessage);
            throw new NullEntityException(errorMessage);
        }
    }

    private void validateUserFromClient(Long id) {
        try {
            userServiceClient.getUser(id);
        } catch (FeignException.NotFound e) {
            log.error(ErrorMessage.getErrorNotFoundUser(id), e);
            throw new IllegalArgumentException(ErrorMessage.getErrorNotFoundUser(id), e);
        } catch (FeignException e) {
            log.error(ErrorMessage.getErrorOccurredValidatingUser(id), e);
            throw new RuntimeException(ErrorMessage.getErrorOccurredValidatingUser(id), e);
        }
    }

    private void isAuthorComment(Comment comment, CommentUpdateDto dto) {
        if (!comment.getAuthorId().equals(dto.getAuthorId())) {
            log.error(ERROR_NOT_AUTHOR_COMMENT);
            throw new NotAuthorException(ERROR_NOT_AUTHOR_COMMENT);
        }
    }

    private <T> T getEntity(Supplier<Optional<T>> finder, String errorMessage) {
        return finder.get().orElseThrow(() -> {
            log.error(errorMessage);
            return new EntityNotFoundException(errorMessage);
        });
    }
}
