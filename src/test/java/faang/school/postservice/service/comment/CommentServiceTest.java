package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.SaveCommentDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.exception.ForbiddenException;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validation.comment.CommentValidator;
import feign.FeignException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @InjectMocks
    private CommentServiceImpl commentService;

    @Mock
    private CommentRepository commentRepository;

    @Spy
    private CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private PostService postService;

    @Mock
    private CommentValidator commentValidator;

    private static final long POST_ID = 1L;
    private static final long AUTHOR_ID = 11L;
    private static final long ANOTHER_AUTHOR_ID = 999L;
    private static final long COMMENT_ID = 1L;
    private static final String COMMENT_TEXT = "text";
    private static final String UPDATED_TEXT = "new text";

    @Test
    @DisplayName("Should create a new comment")
    public void shouldSaveCommentAndReturnDto() {
        SaveCommentDto saveDto = buildSaveCommentDto();
        Post post = buildPost(POST_ID);
        Comment savedComment = buildComment(AUTHOR_ID, post, COMMENT_TEXT);

        when(postService.getPostById(POST_ID)).thenReturn(post);
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        CommentDto result = commentService.create(POST_ID, AUTHOR_ID, saveDto);

        assertNotNull(result);
        assertEquals(COMMENT_ID, result.id());
        assertEquals(COMMENT_TEXT, result.content());
        assertEquals(AUTHOR_ID, result.authorId());
        assertEquals(POST_ID, result.postId());

        verify(userServiceClient).getUser(AUTHOR_ID);
        verify(postService).getPostById(POST_ID);
        verify(commentMapper).toComment(saveDto);
        verify(commentRepository).save(any(Comment.class));
        verify(commentMapper).toCommentDto(savedComment);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException if user does not exist when creating comment")
    void createThrowsIfUserNotFound() {
        SaveCommentDto saveDto = buildSaveCommentDto();

        doThrow(FeignException.NotFound.class).when(userServiceClient).getUser(AUTHOR_ID);

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () ->
                commentService.create(POST_ID, AUTHOR_ID, saveDto));

        assertEquals("User not found with id: " + AUTHOR_ID, ex.getMessage());
        verify(userServiceClient).getUser(AUTHOR_ID);
        verifyNoInteractions(postService, commentRepository);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException if post does not exist when creating comment")
    void createThrowsIfPostNotFound() {
        SaveCommentDto saveDto = buildSaveCommentDto();

        when(postService.getPostById(POST_ID))
                .thenThrow(new EntityNotFoundException("Post not found"));

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () ->
                commentService.create(POST_ID, AUTHOR_ID, saveDto));

        assertEquals("Post not found", ex.getMessage());

        verify(userServiceClient).getUser(AUTHOR_ID);
        verify(postService).getPostById(POST_ID);
        verifyNoInteractions(commentRepository);
    }

    @Test
    @DisplayName("Should update comment if all validations pass")
    void updateCommentSuccessfully() {
        SaveCommentDto saveDto = buildSaveCommentDto();
        Post post = buildPost(POST_ID);
        Comment comment = buildComment(AUTHOR_ID, post, COMMENT_TEXT);
        Comment updatedComment = buildComment(AUTHOR_ID, post, UPDATED_TEXT);

        when(postService.existsById(POST_ID)).thenReturn(true);
        doNothing().when(commentValidator).ensurePostExists(true, POST_ID);
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));
        doNothing().when(commentValidator).ensureCommentBelongsToPost(comment, POST_ID);
        doNothing().when(commentValidator).ensureUserIsAuthor(AUTHOR_ID, comment.getAuthorId());
        when(commentRepository.save(comment)).thenReturn(updatedComment);

        CommentDto result = commentService.update(POST_ID, COMMENT_ID, AUTHOR_ID, saveDto);

        assertNotNull(result);
        assertEquals(COMMENT_ID, result.id());
        assertEquals(UPDATED_TEXT, result.content());
        assertEquals(AUTHOR_ID, result.authorId());
        assertEquals(POST_ID, result.postId());

        verify(userServiceClient).getUser(AUTHOR_ID);
        verify(postService).existsById(POST_ID);
        verify(commentRepository).findById(COMMENT_ID);
        verify(commentValidator).ensurePostExists(true, POST_ID);
        verify(commentValidator).ensureCommentBelongsToPost(comment, POST_ID);
        verify(commentValidator).ensureUserIsAuthor(AUTHOR_ID, comment.getAuthorId());
        verify(commentMapper).update(saveDto, comment);
        verify(commentRepository).save(comment);
        verify(commentMapper).toCommentDto(updatedComment);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException if user does not exist when updating comment")
    void updateThrowsIfUserNotFound() {
        SaveCommentDto dto = buildSaveCommentDto();

        doThrow(FeignException.NotFound.class)
                .when(userServiceClient).getUser(AUTHOR_ID);

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () ->
                commentService.update(POST_ID, COMMENT_ID, AUTHOR_ID, dto));

        assertEquals("User not found with id: " + AUTHOR_ID, ex.getMessage());

        verify(userServiceClient).getUser(AUTHOR_ID);
        verifyNoInteractions(commentRepository);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException if post does not exist when updating comment")
    void updateThrowsIfPostNotFound() {
        SaveCommentDto dto = buildSaveCommentDto();

        when(postService.existsById(POST_ID)).thenReturn(false);
        doThrow(new EntityNotFoundException("Post not found"))
                .when(commentValidator).ensurePostExists(false, POST_ID);

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () ->
                commentService.update(POST_ID, COMMENT_ID, AUTHOR_ID, dto));

        assertEquals("Post not found", ex.getMessage());

        verify(userServiceClient).getUser(AUTHOR_ID);
        verify(postService).existsById(POST_ID);
        verify(commentValidator).ensurePostExists(false, POST_ID);
        verifyNoInteractions(commentRepository);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException if comment does not exist when updating")
    void updateThrowsIfCommentNotFound() {
        SaveCommentDto dto = buildSaveCommentDto();

        when(userServiceClient.getUser(AUTHOR_ID)).thenReturn(null);
        when(postService.existsById(POST_ID)).thenReturn(true);
        doNothing().when(commentValidator).ensurePostExists(true, POST_ID);
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () ->
                commentService.update(POST_ID, COMMENT_ID, AUTHOR_ID, dto));

        assertEquals("Comment not found with id: " + COMMENT_ID, ex.getMessage());

        verify(userServiceClient).getUser(AUTHOR_ID);
        verify(postService).existsById(POST_ID);
        verify(commentValidator).ensurePostExists(true, POST_ID);
        verify(commentRepository).findById(COMMENT_ID);
    }

    @Test
    @DisplayName("Should throw DataValidationException if comment does not belong to post when updating")
    void updateThrowsIfCommentNotBelongsToPost() {
        SaveCommentDto dto = buildSaveCommentDto();
        Post otherPost = buildPost(999L);
        Comment comment = buildComment(AUTHOR_ID, otherPost, COMMENT_TEXT);

        when(userServiceClient.getUser(AUTHOR_ID)).thenReturn(null);
        when(postService.existsById(POST_ID)).thenReturn(true);
        doNothing().when(commentValidator).ensurePostExists(true, POST_ID);
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));
        doThrow(new DataValidationException("Comment does not belong to post with id: " + POST_ID))
                .when(commentValidator).ensureCommentBelongsToPost(comment, POST_ID);

        DataValidationException ex = assertThrows(DataValidationException.class, () ->
                commentService.update(POST_ID, COMMENT_ID, AUTHOR_ID, dto));

        assertTrue(ex.getMessage().contains("Comment does not belong to post"));

        verify(userServiceClient).getUser(AUTHOR_ID);
        verify(postService).existsById(POST_ID);
        verify(commentValidator).ensurePostExists(true, POST_ID);
        verify(commentRepository).findById(COMMENT_ID);
        verify(commentValidator).ensureCommentBelongsToPost(comment, POST_ID);

        verifyNoInteractions(commentMapper);
        verify(commentValidator, never()).ensureUserIsAuthor(any(), any());
        verify(commentRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw ForbiddenException if user is not the author when updating comment")
    void updateThrowsIfUserIsNotAuthor() {
        SaveCommentDto dto = buildSaveCommentDto();
        Post post = buildPost(POST_ID);
        Comment comment = buildComment(ANOTHER_AUTHOR_ID, post, COMMENT_TEXT);

        when(userServiceClient.getUser(AUTHOR_ID)).thenReturn(null);
        when(postService.existsById(POST_ID)).thenReturn(true);
        doNothing().when(commentValidator).ensurePostExists(true, POST_ID);
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));
        doNothing().when(commentValidator).ensureCommentBelongsToPost(comment, POST_ID);
        doThrow(new ForbiddenException("User is not the author of this comment"))
                .when(commentValidator).ensureUserIsAuthor(AUTHOR_ID, comment.getAuthorId());

        ForbiddenException ex = assertThrows(ForbiddenException.class, () ->
                commentService.update(POST_ID, COMMENT_ID, AUTHOR_ID, dto));

        assertTrue(ex.getMessage().contains("User is not the author"));

        verify(userServiceClient).getUser(AUTHOR_ID);
        verify(postService).existsById(POST_ID);
        verify(commentValidator).ensurePostExists(true, POST_ID);
        verify(commentRepository).findById(COMMENT_ID);
        verify(commentValidator).ensureCommentBelongsToPost(comment, POST_ID);
        verify(commentValidator).ensureUserIsAuthor(AUTHOR_ID, comment.getAuthorId());

        verify(commentMapper, never()).update(any(), any());
        verify(commentRepository, never()).save(any());
        verify(commentMapper, never()).toCommentDto(any());
    }

    @Test
    @DisplayName("Should return list of comments for existing post")
    void getByPostIdReturnsComments() {
        Comment comment1 = buildCommentWithIdAndContent(COMMENT_ID, "first");
        Comment comment2 = buildCommentWithIdAndContent(2L, "second");
        List<Comment> comments = List.of(comment2, comment1);

        when(postService.existsById(POST_ID)).thenReturn(true);
        doNothing().when(commentValidator).ensurePostExists(true, POST_ID);
        when(commentRepository.findAllByPostIdOrderByCreatedAtDesc(POST_ID)).thenReturn(comments);

        List<CommentDto> result = commentService.getByPostId(POST_ID);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(2L, result.get(0).id());
        assertEquals(COMMENT_ID, result.get(1).id());

        verify(postService).existsById(POST_ID);
        verify(commentValidator).ensurePostExists(true, POST_ID);
        verify(commentRepository).findAllByPostIdOrderByCreatedAtDesc(POST_ID);
        verify(commentMapper).toCommentDtos(comments);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException if post does not exist when retrieving comments")
    void getByPostIdThrowsIfPostNotFound() {
        when(postService.existsById(POST_ID)).thenReturn(false);
        doThrow(new EntityNotFoundException("Post not found"))
                .when(commentValidator).ensurePostExists(false, POST_ID);

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () ->
                commentService.getByPostId(POST_ID));

        assertEquals("Post not found", ex.getMessage());

        verify(postService).existsById(POST_ID);
        verify(commentValidator).ensurePostExists(false, POST_ID);
        verifyNoInteractions(commentRepository);
    }

    @Test
    @DisplayName("Should return empty list if no comments found for post")
    void getByPostIdReturnsEmptyListIfNoComments() {
        when(postService.existsById(POST_ID)).thenReturn(true);
        doNothing().when(commentValidator).ensurePostExists(true, POST_ID);
        when(commentRepository.findAllByPostIdOrderByCreatedAtDesc(POST_ID)).thenReturn(List.of());

        List<CommentDto> result = commentService.getByPostId(POST_ID);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(postService).existsById(POST_ID);
        verify(commentValidator).ensurePostExists(true, POST_ID);
        verify(commentRepository).findAllByPostIdOrderByCreatedAtDesc(POST_ID);
        verify(commentMapper).toCommentDtos(List.of());
    }

    @Test
    @DisplayName("Should delete comment when all checks pass")
    void deleteCommentSuccessfully() {
        Post post = buildPost(POST_ID);
        Comment comment = buildComment(AUTHOR_ID, post, COMMENT_TEXT);

        when(userServiceClient.getUser(AUTHOR_ID)).thenReturn(null);
        when(postService.getPostById(POST_ID)).thenReturn(post);
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));

        commentService.delete(POST_ID, COMMENT_ID, AUTHOR_ID);

        verify(userServiceClient).getUser(AUTHOR_ID);
        verify(postService).getPostById(POST_ID);
        verify(commentRepository).findById(COMMENT_ID);
        verify(commentValidator).ensureCommentBelongsToPost(comment, POST_ID);
        verify(commentValidator).ensureUserCanDeleteComment(comment, post, AUTHOR_ID);
        verify(commentRepository).deleteById(COMMENT_ID);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException if user does not exist when deleting comment")
    void deleteThrowsIfUserNotFound() {
        doThrow(FeignException.NotFound.class)
                .when(userServiceClient).getUser(AUTHOR_ID);

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () ->
                commentService.delete(POST_ID, COMMENT_ID, AUTHOR_ID));

        assertEquals("User not found with id: " + AUTHOR_ID, ex.getMessage());

        verify(userServiceClient).getUser(AUTHOR_ID);
        verifyNoInteractions(postService, commentRepository);
    }


    @Test
    @DisplayName("Should throw EntityNotFoundException if comment does not exist when deleting")
    void deleteThrowsIfCommentNotFound() {
        Post post = buildPost(POST_ID);

        when(userServiceClient.getUser(AUTHOR_ID)).thenReturn(null);
        when(postService.getPostById(POST_ID)).thenReturn(post);
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () ->
                commentService.delete(POST_ID, COMMENT_ID, AUTHOR_ID));

        assertEquals("Comment not found with id: " + COMMENT_ID, ex.getMessage());

        verify(commentRepository).findById(COMMENT_ID);
        verify(userServiceClient).getUser(AUTHOR_ID);
        verify(postService).getPostById(POST_ID);
        verify(commentValidator, never()).ensureCommentBelongsToPost(any(), anyLong());
        verify(commentValidator, never()).ensureUserCanDeleteComment(any(), any(), anyLong());
        verify(commentRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should throw DataValidationException if comment does not belong to post when deleting")
    void deleteThrowsIfCommentNotBelongsToPost() {
        Post actualPost = buildPost(POST_ID);
        Post anotherPost = buildPost(999L);
        Comment comment = buildComment(AUTHOR_ID, anotherPost, COMMENT_TEXT);

        when(userServiceClient.getUser(AUTHOR_ID)).thenReturn(null);
        when(postService.getPostById(POST_ID)).thenReturn(actualPost);
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));
        doThrow(new DataValidationException("Comment does not belong to post"))
                .when(commentValidator).ensureCommentBelongsToPost(comment, POST_ID);

        DataValidationException ex = assertThrows(DataValidationException.class, () ->
                commentService.delete(POST_ID, COMMENT_ID, AUTHOR_ID));

        assertTrue(ex.getMessage().contains("Comment does not belong"));

        verify(userServiceClient).getUser(AUTHOR_ID);
        verify(postService).getPostById(POST_ID);
        verify(commentRepository).findById(COMMENT_ID);
        verify(commentValidator).ensureCommentBelongsToPost(comment, POST_ID);
        verify(commentValidator, never()).ensureUserCanDeleteComment(any(), any(), anyLong());
        verify(commentRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should throw ForbiddenException if user cannot delete comment")
    void deleteThrowsIfUserCannotDeleteComment() {
        Post post = buildPost(POST_ID);
        Comment comment = buildComment(ANOTHER_AUTHOR_ID, post, COMMENT_TEXT);

        when(userServiceClient.getUser(AUTHOR_ID)).thenReturn(null);
        when(postService.getPostById(POST_ID)).thenReturn(post);
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(comment));
        doNothing().when(commentValidator).ensureCommentBelongsToPost(comment, POST_ID);
        doThrow(new ForbiddenException("User cannot delete comment"))
                .when(commentValidator).ensureUserCanDeleteComment(comment, post, AUTHOR_ID);

        ForbiddenException ex = assertThrows(ForbiddenException.class, () ->
                commentService.delete(POST_ID, COMMENT_ID, AUTHOR_ID));

        assertTrue(ex.getMessage().contains("User cannot delete"));

        verify(userServiceClient).getUser(AUTHOR_ID);
        verify(postService).getPostById(POST_ID);
        verify(commentRepository).findById(COMMENT_ID);
        verify(commentValidator).ensureCommentBelongsToPost(comment, POST_ID);
        verify(commentValidator).ensureUserCanDeleteComment(comment, post, AUTHOR_ID);
        verify(commentRepository, never()).deleteById(anyLong());
    }

    private SaveCommentDto buildSaveCommentDto() {
        return new SaveCommentDto(COMMENT_TEXT);
    }

    private Post buildPost(long postId) {
        return Post.builder()
                .id(postId)
                .build();
    }

    private Comment buildComment(long authorId, Post post, String content) {
        return Comment.builder()
                .id(COMMENT_ID)
                .content(content)
                .authorId(authorId)
                .post(post)
                .build();
    }

    private Comment buildCommentWithIdAndContent(long id, String content) {
        return Comment.builder()
                .id(id)
                .content(content)
                .build();
    }
}
