package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentResponseDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.mapper.CommentCreateMapper;
import faang.school.postservice.mapper.CommentResponseMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final CommentCreateMapper commentCreateMapper;
    private final CommentResponseMapper commentResponseMapper;
    private final PostService postService;
    private final UserServiceClient userServiceClient;

    @Override
    public long createComment(CommentCreateDto commentCreateDto) {
        log.debug("Adding {}", commentCreateDto);

        //todo Будет работать только после появления endPoint в userService
        UserDto userDto = userServiceClient.getUser(commentCreateDto.getAuthorId());
        log.debug("Fetched UserDto for authorId {}: {}", commentCreateDto.getAuthorId(), userDto);
        validateUserDto(userDto);

        log.debug("Receipt post with ID: {}", commentCreateDto.getPostId());
        Post post = postService.getPostEntryById(commentCreateDto.getPostId());
        log.debug("Post with ID {} fetched successfully", commentCreateDto.getPostId());

        Comment comment = commentCreateMapper.toEntity(commentCreateDto);
        log.debug("Mapped comment entity: {}", comment);

        log.debug("The {} is set to the {}", post, comment);
        comment.setPost(post);
        log.debug("Adding a {} to {}", comment, post);
        log.debug("Adding {} to post with ID: {}", comment, commentCreateDto.getPostId());
        post.getComments().add(comment);
        log.debug("{} successfully added to post with ID: {}", comment, commentCreateDto.getPostId());

        log.debug("Saving {} to the database", comment);
        Comment returnedComment = commentRepository.save(comment);
        log.debug("{} successfully saved to the database", comment);

        return returnedComment.getId();
    }

    @Override
    public void updateCommentContent(long commentId, CommentUpdateDto commentUpdateDto) {
        log.debug("Updating comment: {}", commentUpdateDto);

        Comment comment = commentRepository
                .findById(commentId).orElseThrow(() -> new EntityNotFoundException("Comment not found"));
        log.debug("{} has been successfully obtained", comment);


        if (!comment.getAuthorId().equals(commentUpdateDto.getAuthorId())) {
            log.error("Id of {} of the comment does not coincide with id of {} update",
                    commentUpdateDto.getAuthorId(), commentId);
            throw new DataValidationException(
                    "Id of the author of the comment does not coincide with id of the author update");
        }
        log.debug("{} author of the commentary and {} of the author of the update are the same",
                comment.getAuthorId(), commentUpdateDto.getAuthorId());

        log.debug("Updating comment entity: {}", comment);
        comment.setContent(commentUpdateDto.getContent());
        log.debug("Successful update of {} {}", comment, commentUpdateDto.getContent());

        log.debug("Commenting the {} in the database", comment);
        commentRepository.save(comment);
        log.debug("Comment saved to the database");
    }

    @Override
    public ResponseEntity<List<CommentResponseDto>> getAllComments(long postId) { //todo
        log.debug("Fetching all comments for post with ID: {}", postId);

        log.debug("Receipt post with ID: {}", postId);
        Post post = postService.getPostEntryById(postId);
        log.debug("Post with ID {} fetched successfully", postId);

        log.debug("Converting comments into DTO and sorting");
        List<CommentResponseDto> commentResponseDtos = post.getComments().stream()
                .map(commentResponseMapper::toDto)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(
                        CommentResponseDto::getCreatedAt,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .toList();
        log.debug("Fetched and sorted {} comments for post with ID {}", commentResponseDtos.size(), postId);

        log.debug("{} is interrupted by ResponseEntity and returned", commentResponseDtos);
        return ResponseEntity.ok(commentResponseDtos);
    }

    @Override
    public void deleteComment(long commentId) {
        log.debug("Starting to delete comment with ID: {}", commentId);

        //todo Пока без проверок, что его может удалить только сам автор. Позднее мы добавим все проверки,
        // когда познакомимся с web компонентами, а в частности с сессией пользователя.

        log.debug("Checking the existence of comment on ID: {}", commentId);
        if (!commentRepository.existsById(commentId)) {
            log.error("Comment with ID {} does not exist", commentId);
            throw new EntityNotFoundException("Comment not found");
        }
        log.debug("Comment with ID {} exists and will be deleted", commentId);

        log.debug("Deleting comment with ID: {}", commentId);
        commentRepository.deleteById(commentId);
        log.debug("Comment with ID {} successfully deleted", commentId);
    }

    private void validateUserDto(UserDto userDto) {
        if (userDto == null) {
            log.error("UserDto is null");
            throw new NotFoundException("UserDto is null");
        }

        if (userDto.id() == null) {
            log.error("UserDto ID is null for authorId");
            throw new NotFoundException("UserDto ID is null for authorId");
        }

        if (userDto.id() < 1) {
            log.error("UserDto ID is invalid (< 1) for authorId");
            throw new NotFoundException("UserDto ID is invalid (< 1) for authorId");
        }
    }
}
