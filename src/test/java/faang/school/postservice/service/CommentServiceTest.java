package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentResponseDto;
import faang.school.postservice.dto.comment.UpdateCommentDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.CommentEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.OutboxEventRepository;
import faang.school.postservice.utils.Helper;
import faang.school.postservice.validator.CommentValidator;
import faang.school.postservice.validator.PostValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    CommentRepository commentRepository;

    @Mock
    CommentEventPublisher commentEventPublisher;

    @Mock
    PostService postService;

    @Mock
    CommentMapper commentMapper;

    @Mock
    PostValidator postValidator;

    @Mock
    CommentValidator commentValidator;

    @Mock
    UserServiceClient userServiceClient;

    @Mock
    private Helper helper;

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @Mock
    private CommentAuthorCacheService commentAuthorCacheService;

    @InjectMocks
    CommentService commentService;

    CreateCommentDto dto;
    UpdateCommentDto updateDto;
    CommentResponseDto responseDto;
    Post post;
    Comment comment;
    long postId = 1L;

    @BeforeEach
    void setUp() {
        dto = createTestCreateCommentDto();
        updateDto = createTestUpdateCommentDto();
        responseDto = createCommentTestCommentDto();
        post = createCommentTestPost();
        comment = createCommentTestComment();
    }

    @Test
    @DisplayName("Create comment success")
    void testCreateCommentCommentSuccess() {

        when(commentMapper.toEntity(dto)).thenReturn(comment);
        when(postService.getPostById(postId)).thenReturn(post);
        when(commentMapper.toDto(comment)).thenReturn(responseDto);

        CommentResponseDto result = commentService.createComment(postId, dto);

        verify(postValidator, times(1)).validatePostExistsById(postId);
        verify(userServiceClient, times(1)).getUser(dto.getAuthorId());
        verify(commentRepository, times(1)).save(any(Comment.class));

        assertNotNull(result);
        assertEquals(responseDto, result);
        assertEquals(1L, result.getPostId());
    }

    @Test
    @DisplayName("Create comment fail: invalid post id")
    void testCreateCommentCommentFailInvalidPostId() {
        doThrow(EntityNotFoundException.class).when(postValidator).validatePostExistsById(postId);


        assertThrows(EntityNotFoundException.class, () -> commentService.createComment(postId, dto));

        verify(postValidator, times(1)).validatePostExistsById(postId);
        verify(userServiceClient, never()).getUser(dto.getAuthorId());
        verify(commentMapper, never()).toEntity(dto);
        verify(postService, never()).getPostById(postId);
        verify(commentRepository, never()).save(any(Comment.class));
        verify(commentMapper, never()).toDto(any(Comment.class));
    }

    @Test
    @DisplayName("Create comment fail: invalid author id")
    void testCreateCommentCommentFailInvalidAuthorId() {
        doThrow(EntityNotFoundException.class).when(userServiceClient).getUser(dto.getAuthorId());

        assertThrows(EntityNotFoundException.class, () -> commentService.createComment(postId, dto));

        verify(postValidator, times(1)).validatePostExistsById(postId);
        verify(userServiceClient, times(1)).getUser(dto.getAuthorId());
        verify(commentMapper, never()).toEntity(dto);
        verify(commentRepository, never()).save(any(Comment.class));
        verify(commentMapper, never()).toDto(any(Comment.class));
    }

    @Test
    @DisplayName("Update comment success")
    void testUpdateCommentSuccess() {
        comment.setPost(post);
        when(commentRepository.findById(updateDto.getId())).thenReturn(Optional.of(comment));
        when(commentMapper.toDto(comment)).thenReturn(responseDto);

        CommentResponseDto result = commentService.updateComment(postId, updateDto);

        verify(postValidator, times(1)).validatePostExistsById(postId);
        verify(commentValidator, times(1)).validateCommentExistsById(updateDto.getId());
        verify(userServiceClient, times(1)).getUser(updateDto.getAuthorId());
        verify(commentValidator, times(1)).validateCommentAuthorId(comment, updateDto.getAuthorId());
        verify(commentRepository, times(1)).save(comment);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test content", result.getContent());
    }

    @Test
    @DisplayName("Update comment fail: invalid post id")
    void testUpdateCommentFailInvalidPostId() {
        doThrow(EntityNotFoundException.class).when(postValidator).validatePostExistsById(postId);

        assertThrows(EntityNotFoundException.class, () -> commentService.updateComment(postId, updateDto));

        verify(postValidator, times(1)).validatePostExistsById(postId);
        verify(commentValidator, never()).validateCommentExistsById(updateDto.getId());
        verify(userServiceClient, never()).getUser(updateDto.getAuthorId());
        verify(commentValidator, never()).validateCommentAuthorId(comment, updateDto.getAuthorId());
        verify(commentRepository, never()).save(comment);
    }

    @Test
    @DisplayName("Update comment fail: invalid comment id")
    void testUpdateCommentFailInvalidCommentId() {
        when(commentRepository.findById(updateDto.getId())).thenReturn(Optional.empty());

        Exception ex = assertThrows(EntityNotFoundException.class, () -> commentService.updateComment(postId, updateDto));
        assertEquals("Comment with id: 3 doesn't exist", ex.getMessage());

        verify(postValidator, times(1)).validatePostExistsById(postId);
        verify(commentValidator, times(1)).validateCommentExistsById(updateDto.getId());
        verify(userServiceClient, times(1)).getUser(updateDto.getAuthorId());
        verify(commentValidator, never()).validateCommentAuthorId(comment, updateDto.getAuthorId());
        verify(commentRepository, never()).save(comment);
    }

    @Test
    @DisplayName("Get all comments success")
    void testGetAllCommentsSuccess() {
        List<Comment> comments = createTestComments();
        List<CommentResponseDto> commentResponseDtos = createTestCommentResponseDtos(comments);
        when(postService.getPostById(postId)).thenReturn(post);
        post.setComments(comments);
        when(commentMapper.toListDto(anyList())).thenReturn(commentResponseDtos);

        List<CommentResponseDto> result = commentService.getAllComments(postId);

        verify(postValidator, times(1)).validatePostExistsById(postId);
        verify(postService, times(1)).getPostById(postId);
        verify(commentMapper, times(1)).toListDto(anyList());

        assertEquals(3, result.size());
        assertEquals(2L, result.get(0).getId());
        assertEquals(1L, result.get(1).getId());
        assertEquals(3L, result.get(2).getId());
    }

    @Test
    @DisplayName("Get all comments success with one null date")
    void testGetAllCommentsSuccessWithNullDate() {
        List<Comment> comments = createTestComments();
        comments.get(1).setUpdatedAt(null);
        List<CommentResponseDto> commentResponseDtos = createTestCommentResponseDtos(comments);
        when(postService.getPostById(postId)).thenReturn(post);
        post.setComments(comments);
        when(commentMapper.toListDto(anyList())).thenReturn(commentResponseDtos);

        List<CommentResponseDto> result = commentService.getAllComments(postId);

        verify(postValidator, times(1)).validatePostExistsById(postId);
        verify(postService, times(1)).getPostById(postId);
        verify(commentMapper, times(1)).toListDto(anyList());

        assertEquals(3, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(3L, result.get(1).getId());
        assertEquals(2L, result.get(2).getId());
    }

    @Test
    @DisplayName("Get all comments fail: invalid postId")
    void testGetAllCommentsFailInvalidPostId() {
        when(postService.getPostById(postId)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> commentService.getAllComments(postId));

        verify(postValidator, times(1)).validatePostExistsById(postId);
        verify(postService, times(1)).getPostById(postId);
        verify(commentMapper, never()).toListDto(anyList());
    }

    @Test
    @DisplayName("Delete comment success")
    void testDeleteCommentSuccess() {

        assertDoesNotThrow(() -> commentService.deleteComment(postId, comment.getId()));

        verify(postValidator, times(1)).validatePostExistsById(postId);
        verify(commentValidator, times(1)).validateCommentExistsById(comment.getId());
        verify(commentRepository, times(1)).deleteById(comment.getId());
    }

    @Test
    @DisplayName("Delete comment fail: invalid post id")
    void testDeleteCommentFailInvalidPostId() {
        doThrow(EntityNotFoundException.class).when(postValidator).validatePostExistsById(postId);

        assertThrows(EntityNotFoundException.class, () -> commentService.deleteComment(postId, comment.getId()));

        verify(commentValidator, never()).validateCommentExistsById(comment.getId());
        verify(commentRepository, never()).deleteById(comment.getId());
    }

    @Test
    @DisplayName("Delete comment fail: invalid comment id")
    void testDeleteCommentFailInvalidCommentId() {
        doThrow(EntityNotFoundException.class).when(commentValidator).validateCommentExistsById(comment.getId());

        assertThrows(EntityNotFoundException.class, () -> commentService.deleteComment(postId, comment.getId()));

        verify(postValidator, times(1)).validatePostExistsById(postId);
        verify(commentRepository, never()).deleteById(comment.getId());
    }


    private CreateCommentDto createTestCreateCommentDto() {
        return CreateCommentDto.builder()
                .authorId(1L)
                .content("Test content")
                .build();
    }

    private CommentResponseDto createCommentTestCommentDto() {
        return CommentResponseDto.builder()
                .id(1L)
                .content("Test content")
                .authorId(1L)
                .likeIds(null)
                .postId(1L)
                .createdAt(null)
                .updatedAt(null)
                .build();
    }

    private UpdateCommentDto createTestUpdateCommentDto() {
        return UpdateCommentDto.builder()
                .id(3L)
                .authorId(1L)
                .content("Test update content")
                .build();
    }

    private Post createCommentTestPost() {
        return Post.builder()
                .id(1L)
                .content("Test content")
                .comments(null)
                .build();
    }

    private Comment createCommentTestComment() {
        return Comment.builder()
                .id(1L)
                .content("Test content")
                .authorId(1L)
                .likes(null)
                .post(null)
                .createdAt(null)
                .updatedAt(null)
                .build();
    }

    private List<Comment> createTestComments() {
        return List.of(
                Comment.builder().id(1L).updatedAt(LocalDateTime.of(2023, 10, 1, 10, 0)).build(),
                Comment.builder().id(2L).updatedAt(LocalDateTime.of(2023, 11, 2, 9, 30)).build(),
                Comment.builder().id(3L).updatedAt(LocalDateTime.of(2023, 9, 30, 15, 45)).build());
    }

    private List<CommentResponseDto> createTestCommentResponseDtos(List<Comment> comments) {
        return comments.stream()
                .sorted(Comparator.comparing(Comment::getUpdatedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(c -> CommentResponseDto.builder().id(c.getId()).updatedAt(c.getUpdatedAt()).build())
                .toList();
    }


}