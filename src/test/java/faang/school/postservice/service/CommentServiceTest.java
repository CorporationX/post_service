package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.exception.CommentNotFoundException;
import faang.school.postservice.mapper.comment.CommentMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.impl.CommentEventProducer;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.util.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private Utils utils;

    @Spy
    private CommentMapperImpl commentMapper;

    @Mock
    private CommentEventProducer commentEventProducer;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostService postService;

    @InjectMocks
    private CommentService commentService;

    private CommentDto commentDto;
    private Comment commentEntity;
    private Post post;

    @BeforeEach
    void setUp() {
        commentDto = new CommentDto(null, "Test content", 1L, 100L);
        commentEntity = new Comment();
        commentEntity.setId(1L);
        commentEntity.setContent("Test content");
        commentEntity.setPost(new Post());
        commentEntity.setCreatedAt(LocalDateTime.now());

        post = new Post();
        post.setId(100L);
    }

    @Test
    void testFindCommentByIdFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(commentEntity));

        Comment result = commentService.findCommentById(1L);

        assertNotNull(result);
        assertEquals(commentEntity.getId(), result.getId());
        verify(commentRepository).findById(1L);
    }

    @Test
    void testFindCommentByIdNotFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());
        lenient().when(utils.format(anyString(), any())).thenReturn("Not found");

        assertThrows(CommentNotFoundException.class, () -> commentService.findCommentById(1L));

        verify(commentRepository).findById(1L);
    }

    @Test
    void testAddCommentSuccess() {
        when(postService.findPostById(100L)).thenReturn(post);
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> {
            Comment c = invocation.getArgument(0);
            c.setId(1L);
            return c;
        });

        CommentDto result = commentService.addComment(commentDto);

        assertNotNull(result);
        assertEquals(1L, result.id());

        verify(postService).findPostById(100L);
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void testUpdateCommentSuccess() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(commentEntity));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation ->
                invocation.getArgument(0));

        CommentDto updateDto = new CommentDto(1L, "Updated content", 1L, 100L);

        CommentDto result = commentService.updateComment(updateDto);

        assertNotNull(result);
        assertEquals("Updated content", result.content());

        verify(commentRepository).findById(1L);
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void testGetAllCommentsReturnsSortedList() {
        Comment c1 = new Comment();
        c1.setCreatedAt(LocalDateTime.of(2023, 10, 10, 10, 0));

        Comment c2 = new Comment();
        c2.setCreatedAt(LocalDateTime.of(2023, 10, 11, 10, 0));

        when(commentRepository.findAllByPostId(100L)).thenReturn(Arrays.asList(c1, c2));

        List<CommentDto> comments = commentService.getAllComments(100L);

        assertEquals(2, comments.size());

        verify(commentRepository).findAllByPostId(100L);
    }

    @Test
    void testDeleteCommentSuccess() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(commentEntity));

        doNothing().when(commentRepository).delete(any(Comment.class));

        assertDoesNotThrow(() -> commentService.deleteComment(1L));

        verify(commentRepository).delete(any(Comment.class));
        verify(commentRepository).findById(1L);
    }
}