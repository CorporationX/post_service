package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentEventDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.CommentEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.util.ModerationDictionary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private PostService postService;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private ModerationDictionary moderationDictionary;
    @Mock
    private CommentEventPublisher commentEventPublisher;
    @InjectMocks
    private CommentService commentService;
    private CommentDto commentDto;
    private Comment comment;
    private CommentEventDto commentEventDto;

    @BeforeEach
    void setUp() {
        long commentId = 1L, authorId = 1L, postId = 1L;
        commentDto = CommentDto.builder().id(commentId).authorId(authorId).content("content").postId(postId).build();
        comment = Comment.builder().id(commentId).authorId(authorId)
                .post(Post.builder().id(postId).build()).content("content").build();
        commentEventDto = CommentEventDto.builder()
                .commentId(1L)
                .authorId(1L)
                .postId(1L)
                .createdAt(LocalDateTime.now().withNano(0))
                .build();
    }

    @Test
    public void testCreateComment() {

        when(commentMapper.toEntity(commentDto)).thenReturn(comment);
        when(commentMapper.toDto(comment)).thenReturn(commentDto);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(userServiceClient.getUser(comment.getAuthorId())).thenReturn(new UserDto(comment.getAuthorId(), "adil", "asdasd@gmail.com"));
        when(postService.getPostById(comment.getPost().getId())).thenReturn(Post.builder().id(comment.getPost().getId()).build());

        CommentDto result = commentService.createComment(commentDto);

        assertEquals(commentDto, result);

        verify(commentRepository, times(1)).save(comment);
        verify(postService, times(1)).getPostById(comment.getPost().getId());
        verify(userServiceClient, times(1)).getUser(comment.getAuthorId());
        verify(commentMapper, times(1)).toEntity(commentDto);
        verify(commentMapper, times(1)).toDto(comment);
        verify(commentEventPublisher, times(1)).publish(commentEventDto);
    }

    @Test
    public void invalidPostId() {
        when(userServiceClient.getUser(comment.getAuthorId())).thenReturn(null);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> commentService.createComment(commentDto));
        assertEquals("Author with id: " + commentDto.getAuthorId() + " not found!", exception.getMessage());
    }

    @Test
    public void invalidUserId() {
        UserDto userDto = new UserDto(1L, "Adil", "adil@mail.ru");
        when(userServiceClient.getUser(comment.getAuthorId())).thenReturn(userDto);
        when(postService.getPostById(commentDto.getPostId())).thenReturn(null);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> commentService.createComment(commentDto));
        assertEquals("Post with id: " + commentDto.getAuthorId() + " not found!", exception.getMessage());
    }

    @Test
    public void testGetAllCommentsByPostId() {
        List<Comment> comments = List.of(
                Comment.builder().id(1L).createdAt(LocalDateTime.of(2023, 5, 25, 18, 50)).build(),
                Comment.builder().id(3L).createdAt(LocalDateTime.of(2023, 5, 25, 1, 20)).build(),
                Comment.builder().id(2L).createdAt(LocalDateTime.of(2023, 5, 25, 14, 30)).build()
        );
        when(commentRepository.findAllByPostId(anyLong())).thenReturn(comments);
        comments.forEach(commentEntity -> when(commentMapper.toDto(commentEntity))
                .thenReturn(CommentDto.builder().id(commentEntity.getId()).createdAt(commentEntity.getCreatedAt()).build()));

        List<CommentDto> sortedComments = commentService.getCommentsByPostId(1);
        List<CommentDto> expected = List.of(
                CommentDto.builder().id(3L).createdAt(LocalDateTime.of(2023, 5, 25, 1, 20)).build(),
                CommentDto.builder().id(2L).createdAt(LocalDateTime.of(2023, 5, 25, 14, 30)).build(),
                CommentDto.builder().id(1L).createdAt(LocalDateTime.of(2023, 5, 25, 18, 50)).build()
        );

        assertIterableEquals(expected, sortedComments);
    }

    @Test
    public void testDeleteCommentInvalidId() {
        long commentId = 1L;
        when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> commentService.deleteComment(commentId));
        verify(commentRepository, times(0)).deleteById(anyLong());
        assertEquals("Comment with id: " + commentId + " not found", exception.getMessage());
    }

    @Test
    public void testDeleteCommentValid() {
        long commentId = 1L;
        when(commentRepository.findById(anyLong())).thenReturn(Optional.ofNullable(comment));

        boolean result = commentService.deleteComment(commentId);
        verify(commentRepository, times(1)).findById(commentId);
        verify(commentRepository, times(1)).deleteById(commentId);

        assertTrue(result);
    }

    @Test
    public void testUpdateCommentInvalidId() {
        long commentId = 1L;
        when(commentRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> commentService.updateComment(commentDto));

        verify(commentRepository, times(1)).findById(commentId);
        verify(commentRepository, times(0)).save(any());
        assertEquals("Comment with id: " + commentId + " not found", exception.getMessage());
    }

    @Test
    public void testUpdateCommentValid() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        when(commentMapper.toDto(any())).thenReturn(commentDto);
        CommentDto result = commentService.updateComment(commentDto);
        verify(commentRepository, times(1)).findById(comment.getId());
        verify(commentRepository, times(1)).save(any());
        assertEquals(commentDto, result);
    }

    @Test
    void testModerateComment() {
        ReflectionTestUtils.setField(commentService, "batchSize", 100);
        List<Comment> comments = createCommentList();
        when(moderationDictionary.containsBadWord("valid")).thenReturn(false);
        when(moderationDictionary.containsBadWord("not valid")).thenReturn(true);
        when(commentRepository.findNotVerified()).thenReturn(comments);

        commentService.moderateComment();

        assertTrue(comments.get(0).isVerified());
        assertFalse(comments.get(1).isVerified());

        verify(commentRepository).saveAll(anyCollection());
    }

    private List<Comment> createCommentList() {
        return List.of(Comment.builder().id(1).content("valid").verified(false).build(),
                Comment.builder().id(2).content("not valid").verified(false).build());
    }
}