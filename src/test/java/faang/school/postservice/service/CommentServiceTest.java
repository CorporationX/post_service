package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentEditDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.CommentMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.validator.CommentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;
    @Spy
    private CommentMapperImpl commentMapper;
    @Mock
    private PostService postService;
    @Mock
    private CommentValidator commentValidator;
    @Mock
    private UserContext userContext;
    @Mock
    private UserServiceClient userServiceClient;
    @InjectMocks
    private CommentService commentService;
    @Captor
    private ArgumentCaptor<Comment> commentCaptor;
    private Long postId = 1L;
    private Long commentId = 1L;
    private Comment comment;
    private Comment commentForTestException;
    private List<Comment> comments;
    private List<Comment> someCommentsForException;
    private Post post;
    private Post postForTestException;

    @BeforeEach
    void setUp() {
        postId = 1L;
        commentId = 1L;
        comment = Comment.builder()
                .id(commentId)
                .content("afsd").build();
        commentForTestException = Comment.builder()
                .id(2).build();
        comments = List.of(comment);
        someCommentsForException = List.of(commentForTestException);
        post = Post.builder()
                .id(postId)
                .comments(comments).build();
        postForTestException = Post.builder()
                .id(postId)
                .comments(someCommentsForException).build();
    }

    @Test
    void testCreateCommentSavesComment() {
        var commentDto = CommentDto.builder()
                .content("afsd").build();

        when(userServiceClient.isUserExists(userContext.getUserId())).thenReturn(true);
        commentService.createComment(postId, commentDto);

        verify(commentRepository, times(1)).save(commentCaptor.capture());
        Comment capturedComment = commentCaptor.getValue();
        assertEquals(commentDto.getContent(), capturedComment.getContent());
    }

    @Test
    void testCreateCommentReturnsCommentDto() {
        var commentDto = CommentDto.builder()
                .id(commentId)
                .content("afsd").build();
        var returnedcommentDto = CommentDto.builder()
                .id(commentId)
                .authorId(0L)
                .content("afsd").build();
        when(commentRepository.save(comment)).thenReturn(comment);
        when(userServiceClient.isUserExists(userContext.getUserId())).thenReturn(true);
        assertEquals(returnedcommentDto, commentService.createComment(postId, commentDto));
    }

    @Test
    void testUpdateComment() {
        var commentEditDto = CommentEditDto.builder()
                .content("qwerty").build();
        var returnedcommentDto = CommentDto.builder()
                .id(commentId)
                .authorId(0L)
                .content("qwerty").build();
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        assertEquals(returnedcommentDto, commentService.updateComment(commentId, commentEditDto));
    }

    @Test
    void testSortAndGetCommentsByPostId() {
        var dtoId = 0L;
        LocalDateTime createdAt = LocalDateTime.now();
        List<CommentDto> returnedComments = List.of(CommentDto.builder()
                        .id(dtoId)
                        .authorId(dtoId).build(),
                CommentDto.builder()
                        .id(dtoId)
                        .authorId(dtoId).build());
        List<Comment> comments = List.of(Comment.builder()
                        .createdAt(createdAt).build(),
                Comment.builder()
                        .createdAt(createdAt).build());
        when(commentRepository.findAllByPostId(postId)).thenReturn(comments);
        assertEquals(returnedComments, commentService.getCommentsByPostId(postId));
    }

    @Test
    void testDeleteComment() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        commentService.deleteComment(commentId);
        verify(commentRepository, times(1)).delete(comment);
    }

    @Test
    void testDeleteCommentThrowsDataValidationException() {
        assertThrows(DataValidationException.class, () -> {
            commentService.deleteComment(commentId);
        });
    }
}