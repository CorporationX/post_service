package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CreateCommentRequest;
import faang.school.postservice.dto.comment.UpdateCommentRequest;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.CommentValidator;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    private final long postId = 1L;
    private final String content = "test";
    private final long authorId = 1L;
    private final LocalDateTime createdAt = LocalDateTime.of(2024, 9, 21, 11, 34, 54);
    private final LocalDateTime updatedAt = LocalDateTime.of(2024, 9, 24, 11, 30, 23);
    private final long commentId = 1L;

    private CreateCommentRequest createCommentRequest;
    private Comment commentForDto;
    private Post post;
    private Comment commentForDB;
    private CommentDto commentDto;
    private CommentDto commentDto1;
    private CommentDto commentDto2;
    private UserDto userDto;
    private Comment comment;
    private Comment comment1;
    private Comment comment2;
    private UpdateCommentRequest updateCommentRequest;
    private List<Comment> comments;


    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private CommentValidator commentValidator;


    @BeforeEach
    void init() {

        post = mock(Post.class);
        userDto = mock(UserDto.class);

        createCommentRequest = CreateCommentRequest.builder()
                .content(content)
                .authorId(authorId)
                .build();

        updateCommentRequest = UpdateCommentRequest.builder()
                .content(content)
                .authorId(authorId)
                .build();

        commentForDto = Comment.builder()
                .content(content)
                .authorId(authorId)
                .build();

        commentForDB = Comment.builder()
                .id(1L)
                .content(content)
                .authorId(authorId)
                .post(post)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        comment = Comment.builder()
                .id(1L)
                .content(content)
                .authorId(authorId)
                .post(post)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        comment1 = Comment.builder()
                .id(2L)
                .content(content)
                .authorId(2L)
                .post(post)
                .createdAt(LocalDateTime.of(2024, 3, 3, 5, 7, 23))
                .updatedAt(LocalDateTime.of(2025, 9, 24, 11, 30, 23))
                .build();

        comment2 = Comment.builder()
                .id(3L)
                .content(content)
                .authorId(3L)
                .post(post)
                .createdAt(LocalDateTime.of(2024, 10, 24, 11, 43, 43))
                .updatedAt(LocalDateTime.of(2024, 11, 13, 16, 12, 32))
                .build();

        comments = new ArrayList<>();
        comments.add(comment);
        comments.add(comment1);
        comments.add(comment2);

        commentDto = CommentDto.builder()
                .id(1L)
                .content(content)
                .authorId(authorId)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        commentDto1 = CommentDto.builder()
                .id(2L)
                .content(content)
                .authorId(2L)
                .createdAt(LocalDateTime.of(2024, 3, 3, 5, 7, 23))
                .updatedAt(LocalDateTime.of(2025, 9, 24, 11, 30, 23))
                .build();

        commentDto2 = CommentDto.builder()
                .id(3L)
                .content(content)
                .authorId(3L)
                .createdAt(LocalDateTime.of(2024, 10, 24, 11, 43, 43))
                .updatedAt(LocalDateTime.of(2024, 11, 13, 11, 12, 32))
                .build();
    }

    @Nested
    class PositiveTests {
        @Test
        @DisplayName("successful event creation")
        void testSuccessfulCompletionCreateComment() {
            when(userServiceClient.getUser(createCommentRequest.getAuthorId())).thenReturn(userDto);
            when(commentMapper.toComment(createCommentRequest)).thenReturn(commentForDto);
            when(postRepository.findById(postId)).thenReturn(Optional.of(post));
            when(commentRepository.save(commentForDto)).thenReturn(commentForDB);
            when(commentMapper.toCommentDto(commentForDB)).thenReturn(commentDto);

            CommentDto result = commentService.createComment(postId, createCommentRequest);

            verify(userServiceClient).getUser(createCommentRequest.getAuthorId());
            verify(commentMapper).toComment(createCommentRequest);
            verify(postRepository).findById(postId);
            assertNotNull(commentForDto.getPost());
            verify(commentRepository).save(commentForDto);
            verify(commentMapper).toCommentDto(commentForDB);

            assertEquals(result.getId(), commentForDB.getId());
            assertEquals(result.getContent(), commentForDB.getContent());
            assertEquals(result.getAuthorId(), commentForDB.getAuthorId());
            assertEquals(result.getContent(), createCommentRequest.getContent());
            assertEquals(result.getAuthorId(), createCommentRequest.getAuthorId());
        }

        @Test
        @DisplayName("successful comment update")
        void testSuccessfulCompletionUpdateComment() {
            when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
            doNothing().when(commentValidator).checkingForCompliance(comment, updateCommentRequest);
            when(commentMapper.toComment(updateCommentRequest)).thenReturn(commentForDto);
            when(postRepository.findById(postId)).thenReturn(Optional.of(post));
            when(commentRepository.save(commentForDto)).thenReturn(commentForDB);
            when(commentMapper.toCommentDto(commentForDB)).thenReturn(commentDto);

            CommentDto result = commentService.updateComment(postId, 1L, updateCommentRequest);

            verify(commentRepository).findById(commentId);
            verify(commentValidator).checkingForCompliance(comment, updateCommentRequest);
            verify(commentMapper).toComment(updateCommentRequest);
            verify(postRepository).findById(postId);
            assertNotNull(commentForDto.getPost());
            verify(commentRepository).save(commentForDto);
            verify(commentMapper).toCommentDto(commentForDB);

            assertEquals(result.getId(), commentForDB.getId());
            assertEquals(result.getContent(), commentForDB.getContent());
            assertEquals(result.getAuthorId(), commentForDB.getAuthorId());
            assertEquals(result.getContent(), createCommentRequest.getContent());
            assertEquals(result.getAuthorId(), createCommentRequest.getAuthorId());
        }

        @Test
        @DisplayName("successful receipt of the list of comments")
        void testSuccessfulCompletionGetAllComments() {
            when(commentRepository.findAllByPostId(postId)).thenReturn(comments);
            when(commentMapper.toCommentDto(comment)).thenReturn(commentDto);
            when(commentMapper.toCommentDto(comment1)).thenReturn(commentDto1);
            when(commentMapper.toCommentDto(comment2)).thenReturn(commentDto2);

            List<CommentDto> result = commentService.getAllComments(postId);

            CommentDto commentDtoWithMinDateTime = result.stream()
                    .min((dto1, dto2) -> dto1.getCreatedAt().compareTo(dto2.getCreatedAt()))
                    .orElse(null);

            CommentDto commentDtoWithMaxDateTime = result.stream()
                    .max((dto1, dto2) -> dto1.getCreatedAt().compareTo(dto2.getCreatedAt()))
                    .orElse(null);

            assertEquals(result.get(0), commentDtoWithMaxDateTime);
            assertEquals(result.get(result.size() - 1), commentDtoWithMinDateTime);
        }

        @Test
        @DisplayName("successful deletion of a comment")
        void testSuccessfulCompletionDeleteComment() {
            doNothing().when(commentRepository).deleteById(commentId);

            commentService.deleteComment(commentId);
        }

    }

    @Nested
    class NegativeTests {
        @Test
        @DisplayName("successfully throwing an exception when userServiceClient return exception")
        void testCreateCommentWhenUserServiceClientReturnException() {
            when(userServiceClient.getUser(createCommentRequest.getAuthorId())).thenThrow(FeignException.class);

            assertThrows(FeignException.class, () -> commentService.createComment(postId, createCommentRequest));
        }

        @Test
        @DisplayName("successfully throwing an exception when userServiceClient return exception")
        void testCreateCommentWhenPostRepositoryReturnException() {
            when(postRepository.findById(postId)).thenThrow(EntityNotFoundException.class);

            assertThrows(EntityNotFoundException.class, () -> commentService.createComment(postId, createCommentRequest));
        }

        @Test
        @DisplayName("successfully throwing an exception when CommentRepository return exception")
        void testUpdateCommentWhenCommentRepositoryReturnException() {
            when(commentRepository.findById(1L)).thenThrow(DataValidationException.class);

            assertThrows(DataValidationException.class, () -> commentService.updateComment(postId, 1L, updateCommentRequest));
        }

        @Test
        @DisplayName("successfully throwing an exception when userServiceClient return exception")
        void testUpdateCommentWhenPostRepositoryReturnException() {
            when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
            when(postRepository.findById(postId)).thenThrow(EntityNotFoundException.class);

            assertThrows(EntityNotFoundException.class, () -> commentService.updateComment(postId, 1L, updateCommentRequest));
        }

        @Test
        @DisplayName("successfully throwing an exception when List of comments is Empty")
        void testGetAllCommentsWhenListIsEmpty() {
            when(commentRepository.findAllByPostId(postId)).thenReturn(Collections.emptyList());

            assertThrows(NoSuchElementException.class, () -> commentService.getAllComments(postId));
        }
    }
}