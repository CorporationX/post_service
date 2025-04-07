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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostService postService;

    @Mock
    private UserServiceClient userServiceClient;

    @Spy
    private CommentCreateMapper commentCreateMapper = Mockito.mock(CommentCreateMapper.class);

    @Spy
    private CommentResponseMapper commentResponseMapper = Mockito.mock(CommentResponseMapper.class);

    @InjectMocks
    private CommentServiceImpl commentService;

    @Test
    void testCreateCommentSavesCommentToRepository() {
        CommentCreateDto commentCreateDto = new CommentCreateDto();
        commentCreateDto.setPostId(1L);
        commentCreateDto.setAuthorId(1L);
        commentCreateDto.setContent("test");

        List<Comment> comments = new ArrayList<>();
        Post post = Post.builder()
                .id(1L)
                .comments(comments)
                .build();

        UserDto userDto = new UserDto(1L, "Leo", "no@null.net");
        Comment comment = Comment.builder()
                .id(100L)
                .post(post)
                .content("test")
                .build();

        when(userServiceClient.getUser(commentCreateDto.getAuthorId())).thenReturn(userDto);
        when(postService.getPostEntryById(commentCreateDto.getPostId())).thenReturn(post);
        when(commentCreateMapper.toEntity(commentCreateDto)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);

        long result = commentService.createComment(commentCreateDto);

        verify(commentRepository, times(1)).save(comment);

        assertEquals(1, post.getComments().size());
        assertTrue(post.getComments().contains(comment));

        assertEquals(100L, result);

        assertEquals(post, comment.getPost());
    }

    @Test //todo Включить когда заработает валидация пользователя
    @DisplayName("ID Code check in userDto Null")
    void testAddCommentIfUserIdIsNull() {
        CommentCreateDto commentCreateDto = new CommentCreateDto();
        commentCreateDto.setAuthorId(1L);
        UserDto userDto = new UserDto(null, "Leo", "no@null.net");

        when(userServiceClient.getUser(commentCreateDto.getAuthorId())).thenReturn(userDto);

        Exception exception = assertThrows(
                NotFoundException.class, () -> commentService.createComment(commentCreateDto));
        assertEquals("UserDto ID is null for authorId", exception.getMessage());
    }

    @Test
    @DisplayName("Check when ID in userDto is less than one")
    void testAddCommentIfUserIdLessThanOne() {
        CommentCreateDto commentCreateDto = new CommentCreateDto();
        commentCreateDto.setAuthorId(1L);
        UserDto userDto = new UserDto(0L, "Leo", "no@null.net");

        when(userServiceClient.getUser(commentCreateDto.getAuthorId())).thenReturn(userDto);

        Exception exception = assertThrows(
                NotFoundException.class, () -> commentService.createComment(commentCreateDto));
        assertEquals("UserDto ID is invalid (< 1) for authorId", exception.getMessage());
    }

    @Test
    void testAddCommentWhenUserDtoIsNull() {
        CommentCreateDto commentCreateDto = new CommentCreateDto();
        commentCreateDto.setAuthorId(1L);

        when(userServiceClient.getUser(commentCreateDto.getAuthorId())).thenReturn(null);

        Exception exception = assertThrows(
                NotFoundException.class, () -> commentService.createComment(commentCreateDto));
        assertEquals("UserDto is null", exception.getMessage());
    }

    @Test
    void testUpdateCommentContentCommentNotFound() {
        CommentUpdateDto commentUpdateDto = new CommentUpdateDto();
        commentUpdateDto.setAuthorId(1L);
        commentUpdateDto.setContent("test");

        Exception exception = assertThrows(
                EntityNotFoundException.class, () -> commentService.updateCommentContent(1, commentUpdateDto));
        assertEquals("Comment not found", exception.getMessage());
    }

    @Test
    @DisplayName("Id of the author of the comment does not coincide with id of the author update")
    void testUpdateCommentContentIdDoesNotMatch() {
        CommentUpdateDto commentUpdateDto = new CommentUpdateDto();
        commentUpdateDto.setAuthorId(1L);
        commentUpdateDto.setContent("test");

        Comment comment = Comment.builder().authorId(2L).build();

        when(commentRepository.findById(5L)).thenReturn(Optional.of(comment));

        Exception exception = assertThrows(
                DataValidationException.class, () -> commentService.updateCommentContent(5, commentUpdateDto));
        assertEquals(
                "Id of the author of the comment does not coincide with id of the author update",
                exception.getMessage());

    }

    @Test
    void testUpdateCommentContentSaveComment() {
        CommentUpdateDto commentUpdateDto = new CommentUpdateDto();

        commentUpdateDto.setAuthorId(1L);
        commentUpdateDto.setContent("test");

        Comment comment = Comment.builder().authorId(1L).content(commentUpdateDto.getContent()).build();

        when(commentRepository.findById(5L)).thenReturn(Optional.of(comment));

        commentService.updateCommentContent(5, commentUpdateDto);

        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    void testGetAllCommentsReturnsListOfComments() {
        List<Comment> comments = List.of(
                Comment.builder()
                        .id(1L)
                        .createdAt(LocalDateTime.of(2023, 10, 1, 10, 0))
                        .build(),
                Comment.builder()
                        .id(2L)
                        .createdAt(LocalDateTime.of(2023, 9, 15, 15, 30))
                        .build(),
                Comment.builder()
                        .id(3L)
                        .createdAt(LocalDateTime.of(2023, 11, 5, 9, 0))
                        .build()
        );

        Post post = Post.builder()
                .id(1L)
                .comments(comments)
                .build();

        when(postService.getPostEntryById(1L)).thenReturn(post);

        List<CommentResponseDto> expectedDtos = List.of(
                CommentResponseDto.builder()
                        .id(3)
                        .createdAt(LocalDateTime.of(2023, 11, 5, 9, 0))
                        .build(),
                CommentResponseDto.builder()
                        .id(1)
                        .createdAt(LocalDateTime.of(2023, 10, 1, 10, 0))
                        .build(),
                CommentResponseDto.builder()
                        .id(2)
                        .createdAt(LocalDateTime.of(2023, 9, 15, 15, 30))
                        .build()
        );

        when(commentResponseMapper.toDto(comments.get(0))).thenReturn(expectedDtos.get(1));
        when(commentResponseMapper.toDto(comments.get(1))).thenReturn(expectedDtos.get(0));
        when(commentResponseMapper.toDto(comments.get(2))).thenReturn(expectedDtos.get(2));

        ResponseEntity<List<CommentResponseDto>> response = commentService.getAllComments(1L);

        assertEquals(expectedDtos, response.getBody());
        verify(postService).getPostEntryById(1L);
        verify(commentResponseMapper, times(3)).toDto(any(Comment.class));
    }

    @Test
    void testGetAllCommentsEmptyComments() {
        Post post = Post.builder()
                .id(1L)
                .comments(Collections.emptyList())
                .build();

        when(postService.getPostEntryById(1L)).thenReturn(post);

        ResponseEntity<List<CommentResponseDto>> response = commentService.getAllComments(1L);

        assertEquals(Collections.emptyList(), response.getBody());
        verify(postService).getPostEntryById(1L);
        verify(commentResponseMapper, never()).toDto(any(Comment.class));
    }

    @Test
    void testGetAllCommentsNullCreatedAt() {
        // Arrange
        List<Comment> comments = List.of(
                Comment.builder()
                        .id(1L)
                        .createdAt(LocalDateTime.of(2023, 10, 1, 10, 0))
                        .build(),
                Comment.builder()
                        .id(2L)
                        .createdAt(null)
                        .build(),
                Comment.builder()
                        .id(3L)
                        .createdAt(LocalDateTime.of(2023, 11, 5, 9, 0))
                        .build()
        );

        Post post = Post.builder()
                .id(1L)
                .comments(comments)
                .build();

        when(postService.getPostEntryById(1L)).thenReturn(post);

        List<CommentResponseDto> expectedDtos = List.of(
                CommentResponseDto.builder()
                        .id(3L)
                        .createdAt(LocalDateTime.of(2023, 11, 5, 9, 0))
                        .build(),
                CommentResponseDto.builder()
                        .id(1L)
                        .createdAt(LocalDateTime.of(2023, 10, 1, 10, 0))
                        .build(),
                CommentResponseDto.builder()
                        .id(2L)
                        .createdAt(null)
                        .build()
        );

        when(commentResponseMapper.toDto(comments.get(0))).thenReturn(expectedDtos.get(1));
        when(commentResponseMapper.toDto(comments.get(1))).thenReturn(expectedDtos.get(2));
        when(commentResponseMapper.toDto(comments.get(2))).thenReturn(expectedDtos.get(0));

        ResponseEntity<List<CommentResponseDto>> response = commentService.getAllComments(1L);

        assertEquals(expectedDtos, response.getBody());
        verify(postService).getPostEntryById(1L);
        verify(commentResponseMapper, times(3)).toDto(any(Comment.class));
    }

    @Test
    void testDeleteCommentSuccessfulDeletion() {
        long commentId = 1L;

        when(commentRepository.existsById(commentId)).thenReturn(true);

        commentService.deleteComment(commentId);

        verify(commentRepository, times(1)).existsById(commentId);
        verify(commentRepository, times(1)).deleteById(commentId);
    }

    @Test
    void testDeleteCommentCommentDoesNotExist() {
        long commentId = 2L;

        when(commentRepository.existsById(commentId)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> commentService.deleteComment(commentId)
        );

        assertEquals("Comment not found", exception.getMessage());

        verify(commentRepository, never()).deleteById(commentId);
    }
}