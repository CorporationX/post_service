package faang.school.postservice.service;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.messaging.commentevent.CommentEventPublisher;
import faang.school.postservice.util.exception.NotFoundException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.util.exceptionhandler.ErrorCommentMessage;
import faang.school.postservice.util.validator.CommentServiceValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentServiceValidator validator;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private CommentEventPublisher redisCommentEventPublisher;

    @InjectMocks
    private CommentService commentService;

    private CommentDto commentDto;
    private Comment comment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        long commentId = 1L, authorId = 1L, postId = 1L;
        commentDto = CommentDto.builder().id(commentId).authorId(authorId).content("content").postId(postId).build();
        comment = Comment.builder().id(commentId).authorId(authorId).post(new Post()).content("content").build();
    }

    @Test
    public void testCreateComment() {
        commentService.createComment(commentDto);
        Mockito.verify(commentRepository, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(commentMapper, Mockito.times(1)).toEntity(commentDto);
        Mockito.verify(redisCommentEventPublisher, Mockito.times(1)).publish(Mockito.any());
        Mockito.verify(commentMapper, Mockito.times(1)).toDto(Mockito.any());
    }

    @Test
    public void testGetAllCommentsByPostId() {
        List<Comment> comments = List.of(
                Comment.builder().id(1L).createdAt(LocalDateTime.of(2023, 7, 28, 18, 50)).build(),
                Comment.builder().id(2L).createdAt(LocalDateTime.of(2023, 7, 28, 14, 30)).build(),
                Comment.builder().id(3L).createdAt(LocalDateTime.of(2023, 7, 26, 1, 20)).build()
        );
        Mockito.when(commentRepository.findAllByPostIdSortedByCreated(Mockito.anyLong()))
                .thenReturn(comments);
        comments.forEach(commentEntity -> Mockito.when(commentMapper.toDto(commentEntity))
                .thenReturn(CommentDto.builder().id(commentEntity.getId()).createdAt(commentEntity.getCreatedAt()).build()));

        List<CommentDto> sortedComments = commentService.getCommentsByPostId(1);
        Mockito.verify(commentRepository, Mockito.times(1)).findAllByPostIdSortedByCreated(Mockito.anyLong());
        Mockito.verify(commentMapper, Mockito.times(comments.size())).toDto(Mockito.any());

        List<CommentDto> expected = List.of(
                CommentDto.builder().id(1L).createdAt(LocalDateTime.of(2023, 7, 28, 18, 50)).build(),
                CommentDto.builder().id(2L).createdAt(LocalDateTime.of(2023, 7, 28, 14, 30)).build(),
                CommentDto.builder().id(3L).createdAt(LocalDateTime.of(2023, 7, 26, 1, 20)).build()
        );

        assertIterableEquals(expected, sortedComments);
    }

    @Test
    public void testDeleteCommentInvalidId() {
        long commentId = 1L;
        Mockito.when(commentRepository.existsById(Mockito.anyLong()))
                .thenReturn(false);

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> commentService.deleteCommentById(commentId));
        Mockito.verify(commentRepository, Mockito.times(0)).deleteById(Mockito.anyLong());
        assertEquals(ErrorCommentMessage.getCommentWasNotFound(commentId), exception.getMessage());
    }

    @Test
    public void testDeleteCommentValid() {
        long commentId = 1L;
        Mockito.when(commentRepository.existsById(Mockito.anyLong()))
                .thenReturn(true);

        commentService.deleteCommentById(commentId);
        Mockito.verify(commentRepository, Mockito.times(1)).existsById(commentId);
        Mockito.verify(commentRepository, Mockito.times(1)).deleteById(commentId);
    }

    @Test
    public void testUpdateCommentInvalidId() {
        long commentId = 1L;
        Mockito.when(commentRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> commentService.updateComment(commentId, commentDto));

        Mockito.verify(commentRepository, Mockito.times(1)).findById(commentId);
        Mockito.verify(commentRepository, Mockito.times(0)).save(Mockito.any());
        assertEquals(ErrorCommentMessage.getCommentWasNotFound(commentId), exception.getMessage());
    }

    @Test
    public void testUpdateCommentValid() {
        long commentId = 1L;
        comment.setId(commentId);
        Mockito.when(commentRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(comment));
        Mockito.when(commentMapper.toDto(Mockito.any()))
                .thenReturn(commentDto);

        commentService.updateComment(commentId, commentDto);

        Mockito.verify(commentRepository, Mockito.times(1)).findById(commentId);
        Mockito.verify(commentRepository, Mockito.times(1)).save(Mockito.any());
    }
}