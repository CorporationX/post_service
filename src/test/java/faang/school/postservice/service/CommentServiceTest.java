package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.*;
import faang.school.postservice.exception.CommentValidationException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.CommentEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostService postService;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private CommentValidator commentValidator;

    @Mock
    private CommentEventPublisher commentEventPublisher;

    private CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @InjectMocks
    private CommentService commentService;

    private Comment comment;
    private Post post;

    @BeforeEach
    void setUp() {
        commentService = new CommentService(commentRepository, postService,
                userServiceClient, commentMapper, commentValidator, commentEventPublisher);

        post = Post.builder().id(1L).build();
        comment = Comment.builder()
                .id(10L)
                .content("Hello")
                .authorId(99L)
                .post(post)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void getCommentsByPostId_shouldReturnSortedComments() {
        Comment older = Comment.builder().createdAt(LocalDateTime.now()
                .minusDays(1)).build();
        Comment newer = Comment.builder().createdAt(LocalDateTime.now())
                .build();

        when(postService.getPostById(post.getId()))
                .thenReturn(post);
        when(commentRepository.findAllByPostId(post.getId()))
                .thenReturn(List.of(older, newer));

        List<CommentDto> result = commentService.getCommentsByPostId(post.getId());

        assertEquals(2, result.size());
        assertTrue(result.get(0).createdAt().isAfter(result.get(1).createdAt()));
    }

    @Test
    void createComment_shouldReturnSavedCommentDto() {
        CommentCreateDto createDto = CommentCreateDto.builder()
                .postId(post.getId())
                .content("Test")
                .build();

        when(userServiceClient.getUser(comment.getAuthorId()))
                .thenReturn(null);
        when(postService.getPostById(post.getId())).thenReturn(post);
        when(commentRepository.save(any())).thenReturn(comment);

        CommentDto result = commentService.createComment(createDto, comment.getAuthorId());

        assertEquals(comment.getContent(), result.content());
        assertEquals(comment.getAuthorId(), result.authorId());
    }


    @Test
    void createComment_shouldThrowIfUserNotFound() {
        CommentCreateDto createDto = CommentCreateDto.builder()
                .postId(post.getId())
                .content("Test")
                .build();
        Long authorId = 123L;

        doThrow(FeignException.NotFound.class)
                .when(userServiceClient)
                .getUser(eq(authorId));

        assertThrows(CommentValidationException.class, () -> {
            commentService.createComment(createDto, authorId);
        });
    }


    @Test
    void updateComment_shouldUpdateSuccessfully() {
        CommentUpdateDto updateDto = CommentUpdateDto.builder()
                .commentId(comment.getId())
                .content("Updated")
                .build();

        when(commentRepository.findById(comment.getId()))
                .thenReturn(Optional.of(comment));
        when(commentRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CommentDto updated = commentService.updateComment(updateDto, comment.getAuthorId());

        assertEquals("Updated", updated.content());
    }


    @Test
    void updateComment_shouldThrowIfNotAuthor() {
        CommentUpdateDto updateDto = CommentUpdateDto.builder()
                .commentId(comment.getId())
                .content("Updated")
                .build();

        when(commentRepository.findById(comment.getId()))
                .thenReturn(Optional.of(comment));

        doThrow(new CommentValidationException("Forbidden"))
                .when(commentValidator)
                .validateAuthor(any(), eq(111L));

        assertThrows(CommentValidationException.class, () ->
                commentService.updateComment(updateDto, 111L)
        );
    }


    @Test
    void deleteComment_shouldDeleteSuccessfully() {
        when(commentRepository.findById(comment.getId()))
                .thenReturn(Optional.of(comment));

        commentService.deleteComment(comment.getId(), comment.getAuthorId());

        verify(commentRepository).delete(comment);
    }

    @Test
    void deleteComment_shouldThrowIfNotAuthor() {
        when(commentRepository.findById(comment.getId()))
                .thenReturn(Optional.of(comment));
        doThrow(new CommentValidationException("Forbidden"))
                .when(commentValidator).validateAuthor(any(), eq(1000L));

        assertThrows(CommentValidationException.class, () ->
                commentService.deleteComment(comment.getId(), 1000L)
        );

        verify(commentRepository, never()).delete(any());
    }

    @Test
    void getCommentById_shouldReturnComment() {
        when(commentRepository.findById(comment.getId()))
                .thenReturn(Optional.of(comment));

        Comment result = commentService.getCommentById(comment.getId());

        assertEquals(comment, result);
    }

    @Test
    void getCommentById_shouldThrowIfNotFound() {
        when(commentRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(CommentValidationException.class,
                () -> commentService.getCommentById(999L));
    }
}

