package faang.school.postservice.util.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CreateCommentDto;
import faang.school.postservice.dto.comment.ResponseCommentDto;
import faang.school.postservice.dto.comment.UpdateCommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.EventsPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.CommentServiceImpl;
import faang.school.postservice.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    private static final long POST_ID = 1L;
    private static final long COMMENT_ID = 20L;
    private static final long USER_ID = 123L;
    private static final long POST_AUTHOR_ID = 456L;
    private static final long ANOTHER_POST_ID = 2L;

    private static final long NON_EXISTENT_POST_ID = 999L;
    private static final long NON_EXISTENT_COMMENT_ID = 777L;

    private static final String CONTENT = "Test comment content";
    private static final String UPDATED_CONTENT = "Updated comment content";
    private static final String EMPTY_CONTENT = "";
    private static final String BLANK_CONTENT = "   ";
    private static final String LONG_CONTENT = "а".repeat(4097);

    // deterministic timestamps (avoid LocalDateTime.now() in fixtures)
    private static final LocalDateTime T1 = LocalDateTime.of(2025, 1, 1, 10, 0);
    private static final LocalDateTime T2 = LocalDateTime.of(2025, 1, 1, 11, 0);

    @Spy
    private final CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @Mock
    private EventsPublisher eventsPublisher;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostService postService;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Test
    @DisplayName("getAllComments should return mapped comments for existing post")
    void getAllComments_shouldReturnCommentsForExistingPost() {
        // given
        Post post = post(POST_ID, POST_AUTHOR_ID);

        Comment older = comment(2L, post, USER_ID, "older", T1.minusHours(2), T1.minusHours(2));
        Comment base = comment(COMMENT_ID, post, USER_ID, CONTENT, T1, T1);
        Comment newer = comment(3L, post, USER_ID, "newer", T2, T2);

        List<Comment> comments = new ArrayList<>(List.of(base, older, newer));

        when(postService.getPostEntityById(POST_ID)).thenReturn(post);
        when(commentRepository.findAllByPostId(POST_ID)).thenReturn(comments);

        // when
        List<ResponseCommentDto> result = commentService.getAllComments(POST_ID);

        // then
        assertNotNull(result);
        assertEquals(3, result.size());

        verify(postService).getPostEntityById(POST_ID);
        verify(commentRepository).findAllByPostId(POST_ID);
        verify(commentMapper, times(3)).toResponseDto(any(Comment.class));
    }

    @Test
    @DisplayName("createComment should create comment, set timestamps and publish event")
    void createComment_shouldCreateCommentAndPublishEvent() {
        // given
        Post post = post(POST_ID, POST_AUTHOR_ID);
        CreateCommentDto createDto = new CreateCommentDto(CONTENT);

        when(postService.getPostEntityById(POST_ID)).thenReturn(post);
        when(userServiceClient.getUser(USER_ID)).thenReturn(ResponseEntity.ok(user(USER_ID)));

        Comment saved = comment(COMMENT_ID, post, USER_ID, CONTENT, T1, T1);
        when(commentRepository.save(any(Comment.class))).thenReturn(saved);

        // when
        ResponseCommentDto result = commentService.createComment(POST_ID, createDto, USER_ID);

        // then
        assertNotNull(result);
        assertNotNull(result.createdAt());
        assertNotNull(result.updatedAt());
        assertTrue(
                Duration.between(result.createdAt(), result.updatedAt()).abs().toMillis() < 1000,
                "CreatedAt and UpdatedAt should be nearly identical"
        );

        verify(postService).getPostEntityById(POST_ID);
        verify(userServiceClient).getUser(USER_ID);
        verify(commentMapper).toEntity(createDto);
        verify(commentRepository).save(any(Comment.class));
        verify(eventsPublisher).publishCommentCreate(
                eq(POST_ID),
                eq(USER_ID),
                eq(saved.getId()),
                eq(post.getAuthorId()),
                any(LocalDateTime.class)
        );
    }

    @Test
    @DisplayName("createComment should throw when post does not exist")
    void createComment_shouldThrow_whenPostDoesNotExist() {
        // given
        CreateCommentDto createDto = new CreateCommentDto(CONTENT);

        when(postService.getPostEntityById(NON_EXISTENT_POST_ID))
                .thenThrow(new IllegalArgumentException("Post not found"));

        // when + then
        assertThrows(
                IllegalArgumentException.class,
                () -> commentService.createComment(NON_EXISTENT_POST_ID, createDto, USER_ID)
        );

        verify(postService).getPostEntityById(NON_EXISTENT_POST_ID);
        verifyNoInteractions(userServiceClient, commentMapper, commentRepository, eventsPublisher);
    }

    @Test
    @DisplayName("createComment should throw when content is empty")
    void createComment_shouldThrow_whenContentEmpty() {
        // given
        CreateCommentDto createDto = new CreateCommentDto(EMPTY_CONTENT);

        // when + then
        assertThrows(
                IllegalArgumentException.class,
                () -> commentService.createComment(POST_ID, createDto, USER_ID)
        );

        verifyNoInteractions(postService, userServiceClient, commentMapper, commentRepository, eventsPublisher);
    }

    @Test
    @DisplayName("createComment should throw when content is blank")
    void createComment_shouldThrow_whenContentBlank() {
        // given
        CreateCommentDto createDto = new CreateCommentDto(BLANK_CONTENT);

        // when + then
        assertThrows(
                IllegalArgumentException.class,
                () -> commentService.createComment(POST_ID, createDto, USER_ID)
        );

        verifyNoInteractions(postService, userServiceClient, commentMapper, commentRepository, eventsPublisher);
    }

    @Test
    @DisplayName("createComment should throw when content exceeds max length")
    void createComment_shouldThrow_whenContentTooLong() {
        // given
        CreateCommentDto createDto = new CreateCommentDto(LONG_CONTENT);

        // when + then
        assertThrows(
                IllegalArgumentException.class,
                () -> commentService.createComment(POST_ID, createDto, USER_ID)
        );

        verifyNoInteractions(postService, userServiceClient, commentMapper, commentRepository, eventsPublisher);
    }

    @Test
    @DisplayName("createComment should set createdAt and updatedAt to the same value")
    void createComment_shouldSetBothTimesEqual() {
        // given
        Post post = post(POST_ID, POST_AUTHOR_ID);
        CreateCommentDto createDto = new CreateCommentDto(CONTENT);

        when(postService.getPostEntityById(POST_ID)).thenReturn(post);
        when(userServiceClient.getUser(USER_ID)).thenReturn(ResponseEntity.ok(user(USER_ID)));

        Comment saved = comment(COMMENT_ID, post, USER_ID, CONTENT,
                LocalDateTime.of(2024, 1, 15, 10, 30),
                LocalDateTime.of(2024, 1, 15, 10, 30)
        );
        when(commentRepository.save(any(Comment.class))).thenReturn(saved);

        // when
        ResponseCommentDto result = commentService.createComment(POST_ID, createDto, USER_ID);

        // then
        assertNotNull(result.createdAt());
        assertNotNull(result.updatedAt());
        assertEquals(result.createdAt(), result.updatedAt());
    }

    @Test
    @DisplayName("updateComment should update only updatedAt and keep createdAt")
    void updateComment_shouldUpdateOnlyUpdatedAt() {
        // given
        Post post = post(POST_ID, POST_AUTHOR_ID);
        UpdateCommentDto updateDto = new UpdateCommentDto(UPDATED_CONTENT);

        LocalDateTime oldCreatedAt = T1.minusHours(2);
        LocalDateTime oldUpdatedAt = T1.minusHours(1);

        Comment existing = comment(COMMENT_ID, post, USER_ID, CONTENT, oldCreatedAt, oldUpdatedAt);
        Comment updated = comment(COMMENT_ID, post, USER_ID, UPDATED_CONTENT, oldCreatedAt, T2);

        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(existing));
        when(commentRepository.save(any(Comment.class))).thenReturn(updated);

        // when
        ResponseCommentDto result = commentService.updateComment(POST_ID, COMMENT_ID, updateDto);

        // then
        assertNotNull(result.createdAt());
        assertNotNull(result.updatedAt());
        assertEquals(oldCreatedAt, result.createdAt());
        assertTrue(result.updatedAt().isAfter(oldUpdatedAt));
    }

    @Test
    @DisplayName("updateComment should update comment when comment exists and postId matches")
    void updateComment_shouldUpdate_whenValid() {
        // given
        Post post = post(POST_ID, POST_AUTHOR_ID);
        Comment existing = comment(COMMENT_ID, post, USER_ID, CONTENT, T1, T1);

        UpdateCommentDto updateDto = new UpdateCommentDto(UPDATED_CONTENT);

        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(existing));
        when(commentRepository.save(existing)).thenReturn(existing);

        // when
        ResponseCommentDto result = commentService.updateComment(POST_ID, COMMENT_ID, updateDto);

        // then
        assertNotNull(result);
        verify(commentRepository).findById(COMMENT_ID);
        verify(commentMapper).updateEntity(updateDto, existing);
        verify(commentRepository).save(existing);
        verify(commentMapper).toResponseDto(existing);
    }

    @Test
    @DisplayName("updateComment should throw when comment does not exist")
    void updateComment_shouldThrow_whenCommentNotFound() {
        // given
        UpdateCommentDto updateDto = new UpdateCommentDto(UPDATED_CONTENT);
        when(commentRepository.findById(NON_EXISTENT_COMMENT_ID)).thenReturn(Optional.empty());

        // when + then
        assertThrows(
                IllegalArgumentException.class,
                () -> commentService.updateComment(POST_ID, NON_EXISTENT_COMMENT_ID, updateDto)
        );

        verify(commentRepository).findById(NON_EXISTENT_COMMENT_ID);
        verifyNoMoreInteractions(commentRepository);
        verifyNoInteractions(commentMapper);
    }

    @Test
    @DisplayName("updateComment should throw when comment belongs to another post")
    void updateComment_shouldThrow_whenPostIdMismatch() {
        // given
        UpdateCommentDto updateDto = new UpdateCommentDto(UPDATED_CONTENT);
        Comment otherPostComment = comment(COMMENT_ID, post(ANOTHER_POST_ID, POST_AUTHOR_ID), USER_ID, CONTENT, T1, T1);

        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(otherPostComment));

        // when + then
        assertThrows(
                IllegalArgumentException.class,
                () -> commentService.updateComment(POST_ID, COMMENT_ID, updateDto)
        );

        verify(commentRepository).findById(COMMENT_ID);
        verifyNoMoreInteractions(commentRepository);
        verifyNoInteractions(commentMapper);
    }

    @Test
    @DisplayName("deleteComment should delete comment when comment exists and postId matches")
    void deleteComment_shouldDelete_whenValid() {
        // given
        Comment existing = comment(COMMENT_ID, post(POST_ID, POST_AUTHOR_ID), USER_ID, CONTENT, T1, T1);
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(existing));

        // when
        commentService.deleteComment(POST_ID, COMMENT_ID);

        // then
        verify(commentRepository).findById(COMMENT_ID);
        verify(commentRepository).deleteById(COMMENT_ID);
    }

    @Test
    @DisplayName("deleteComment should throw when comment does not exist")
    void deleteComment_shouldThrow_whenCommentNotFound() {
        // given
        when(commentRepository.findById(NON_EXISTENT_COMMENT_ID)).thenReturn(Optional.empty());

        // when + then
        assertThrows(
                IllegalArgumentException.class,
                () -> commentService.deleteComment(POST_ID, NON_EXISTENT_COMMENT_ID)
        );

        verify(commentRepository).findById(NON_EXISTENT_COMMENT_ID);
        verifyNoMoreInteractions(commentRepository);
    }

    @Test
    @DisplayName("deleteComment should throw when comment belongs to another post")
    void deleteComment_shouldThrow_whenPostIdMismatch() {
        // given
        Comment otherPostComment = comment(COMMENT_ID, post(ANOTHER_POST_ID, POST_AUTHOR_ID), USER_ID, CONTENT, T1, T1);
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(otherPostComment));

        // when + then
        assertThrows(
                IllegalArgumentException.class,
                () -> commentService.deleteComment(POST_ID, COMMENT_ID)
        );

        verify(commentRepository).findById(COMMENT_ID);
        verifyNoMoreInteractions(commentRepository);
    }

    // ----------------- test data helpers -----------------

    private static Post post(long id, long authorId) {
        return Post.builder()
                .id(id)
                .authorId(authorId)
                .build();
    }

    private static Comment comment(long id,
                                   Post post,
                                   long authorId,
                                   String content,
                                   LocalDateTime createdAt,
                                   LocalDateTime updatedAt) {
        return Comment.builder()
                .id(id)
                .content(content)
                .authorId(authorId)
                .post(post)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    private static UserDto user(long id) {
        return UserDto.builder()
                .id(id)
                .username("Test User")
                .email("test@example.com")
                .build();
    }
}
