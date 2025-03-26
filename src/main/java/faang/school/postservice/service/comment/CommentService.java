package faang.school.postservice.service.comment;

import static faang.school.postservice.contants.ErrorMessage.*;
import static faang.school.postservice.contants.InfoMessage.*;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.mapper.CommentMapper;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private static final int MAX_LENGTH_CHARACTER = 4096;

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentMapper commentMapper;
    private final UserServiceClient userServiceClient;

    public void createComment(CommentDto commentDto) {
        validateCreateComment(commentDto);
        Post post = getPost(commentDto.getPostId());
        Comment comment = commentMapper.toEntity(commentDto);
        comment.setPost(post);
        comment.setAuthorId(commentDto.getAuthorId());
        commentRepository.save(comment);
        log.info(INFO_CREATE_COMMENT, comment.getId(), commentDto.getAuthorId(), commentDto.getPostId());
    }

    public void updateComment(Long id, CommentUpdateDto commentUpdateDto) {
        validateDto(commentUpdateDto, ERROR_NULL_DTO_UPDATE_COMMENT);
        validateContent(commentUpdateDto.getContent());
        validateId(commentUpdateDto.getAuthorId(), ERROR_NULL_AUTHOR_ID);
        Comment comment = getComment(id);
        checkAuthorComment(comment, commentUpdateDto);
        comment.setContent(commentUpdateDto.getContent());
        commentRepository.save(comment);
        log.info(INFO_UPDATE_COMMENT, id, commentUpdateDto.getAuthorId());
    }

    public List<CommentDto> getCommentsByPostId(Long postId) {
        getPost(postId);
        List<CommentDto> commentDto = commentRepository.findAllByPostId(postId).stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                .map(commentMapper::toDto)
                .toList();
        log.info(INFO_GET_COMMENTS, commentDto.size(), postId);
        return commentDto;
    }

    public void deleteComment(Long id) {
        getComment(id);
        commentRepository.deleteById(id);
        log.info(INFO_DELETE_COMMENT, id);
    }

    private Comment getComment(Long id) {
        validateId(id, ERROR_NULL_COMMENT_ID);
        return getEntity(() -> commentRepository.findById(id), getErrorNotFoundComment(id));
    }

    private Post getPost(Long id) {
        validateId(id, ERROR_NULL_POST_ID);
        return getEntity(() -> postRepository.findById(id), getErrorNotFoundPost(id));
    }

    private <T> void validateDto(T dto, String errorMessage) {
        if (dto == null) {
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private void validateCreateComment(CommentDto commentDto) {
        validateDto(commentDto, ERROR_NULL_DTO_COMMENT);
        validateContent(commentDto.getContent());
        validateId(commentDto.getAuthorId(), ERROR_NULL_AUTHOR_ID);
        validateId(commentDto.getPostId(), ERROR_NULL_POST_ID);
        validateUserService(commentDto.getAuthorId());
    }

    private void validateContent(String content) {
        if (content == null) {
            log.error(ERROR_NULL_CONTENT);
            throw new IllegalArgumentException(ERROR_NULL_CONTENT);
        }
        if (content.isBlank() || content.length() > MAX_LENGTH_CHARACTER) {
            String errorMessage = getErrorWrongFormatContent(MAX_LENGTH_CHARACTER);
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private void validateId(Long id, String errorMessage) {
        if (id == null) {
            log.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private void validateUserService(Long id) {
        validateId(id, ERROR_NULL_AUTHOR_ID);
        try {
            userServiceClient.getUser(id);
        } catch (FeignException.NotFound e) {
            log.error(getErrorNotFoundUser(id), e);
            throw new IllegalArgumentException(getErrorNotFoundUser(id), e);
        } catch (FeignException e) {
            log.error(getErrorOccurredValidatingUser(id), e);
            throw new RuntimeException(getErrorOccurredValidatingUser(id), e);
        }
    }

    private void checkAuthorComment(Comment comment, CommentUpdateDto dto) {
        if (!comment.getAuthorId().equals(dto.getAuthorId())) {
            log.error(ERROR_NOT_AUTHOR_COMMENT);
            throw new IllegalArgumentException(ERROR_NOT_AUTHOR_COMMENT);
        }
    }

    private <T> T getEntity(Supplier<Optional<T>> finder, String errorMessage) {
        return finder.get().orElseThrow(() -> {
            log.error(errorMessage);
            return new IllegalArgumentException(errorMessage);
        });
    }
}
