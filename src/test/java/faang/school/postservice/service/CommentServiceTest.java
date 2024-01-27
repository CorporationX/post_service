package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.CommentEditDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exceptions.DataValidationException;
import faang.school.postservice.mapper.CommentMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.validator.CommentValidator;
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

    @Test
    void testCreateCommentSavesComment() {
        var postId = 1L;
        var userId = 1L;
        var commentDto = CommentDto.builder()
                .content("afsd").build();
        var userDto = UserDto.builder()
                .id(userId).build();

        when(userServiceClient.getUser(userContext.getUserId())).thenReturn(userDto);
        commentService.createComment(postId, commentDto);

        verify(commentRepository, times(1)).save(commentCaptor.capture());
        Comment capturedComment = commentCaptor.getValue();
        assertEquals(commentDto.getContent(), capturedComment.getContent());
    }

    @Test
    void testCreateCommentReturnsCommentDto() {
        var comment = Comment.builder()
                .content("afsd").build();
        var commentDto = CommentDto.builder()
                .content("afsd").build();
        var returnedcommentDto = CommentDto.builder()
                .id(0L)
                .authorId(0L)
                .content("afsd").build();

        when(commentRepository.save(comment)).thenReturn(comment);

        assertEquals(returnedcommentDto, commentService.createComment(1L, commentDto));
    }

    @Test
    void testUpdateComment() {
        var postId = 1L;
        var commentId = 1L;
        var comment = Comment.builder()
                .id(commentId)
                .content("afsd").build();
        List<Comment> comments = List.of(comment);
        var post = Post.builder()
                .id(postId)
                .comments(comments).build();
        var commentEditDto = CommentEditDto.builder()
                .content("qwerty").build();
        var returnedcommentDto = CommentDto.builder()
                .id(commentId)
                .authorId(0L)
                .content("qwerty").build();

        when(postService.getPostById(postId)).thenReturn(post);

        assertEquals(returnedcommentDto, commentService.updateComment(postId, commentId, commentEditDto));
    }

    @Test
    void testUpdateCommentThrowsDataValidationException() {
        var postId = 1L;
        var commentId = 1L;
        var commentEditDto = CommentEditDto.builder().build();
        var comment = Comment.builder()
                .id(2).build();
        List<Comment> comments = List.of(comment);
        var post = Post.builder()
                .id(postId)
                .comments(comments).build();

        when(postService.getPostById(postId)).thenReturn(post);

        assertThrows(DataValidationException.class, () -> {
            commentService.updateComment(postId, commentId, commentEditDto);
        });
    }

    @Test
    void testSortAndGetCommentsByPostId() {
        var dtoId = 0L;
        var postId = 1L;
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
        var postId = 1L;
        var commentId = 1L;
        var comment = Comment.builder()
                .id(commentId)
                .content("afsd").build();
        List<Comment> comments = List.of(comment);
        var post = Post.builder()
                .id(postId)
                .comments(comments).build();

        when(postService.getPostById(postId)).thenReturn(post);
        commentService.deleteComment(postId, commentId);

        verify(commentRepository, times(1)).delete(comment);
    }

    @Test
    void testDeleteCommentThrowsDataValidationException() {
        var postId = 1L;
        var commentId = 1L;
        var comment = Comment.builder()
                .id(2)
                .build();
        List<Comment> comments = List.of(comment);
        var post = Post.builder()
                .id(postId)
                .comments(comments).build();

        when(postService.getPostById(postId)).thenReturn(post);

        assertThrows(DataValidationException.class, () -> {
            commentService.deleteComment(postId, commentId);
        });
    }
}