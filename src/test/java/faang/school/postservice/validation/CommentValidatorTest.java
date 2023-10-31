package faang.school.postservice.validation;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.PostService;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.Assert.assertThrows;

@ExtendWith(MockitoExtension.class)
public class CommentValidatorTest {
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostService postService;
    @Mock
    private UserServiceClient userServiceClient;
    @InjectMocks
    private CommentValidator commentValidator;

    @Test
    public void testValidateAuthorExist_ValidAuthor() {
        CommentDto commentDto = new CommentDto();
        commentDto.setAuthorId(1L);

        Mockito.when(userServiceClient.getUser(1L)).thenReturn(null);

        commentValidator.validateAuthorExist(commentDto);
    }

    @Test
    public void testValidateAuthorExist_InvalidAuthor() {
        CommentDto commentDto = new CommentDto();
        commentDto.setAuthorId(2L);

        Mockito.when(userServiceClient.getUser(2L)).thenThrow(FeignException.class);

        assertThrows(DataValidationException.class, () -> commentValidator.validateAuthorExist(commentDto));
    }

    @Test
    public void testValidateCommentBeforeCreate_ValidComment() {
        CommentDto commentDto = new CommentDto();
        commentDto.setPostId(1L);

        commentValidator.validateCommentBeforeCreate(commentDto);
    }

    @Test
    public void testValidateCommentBeforeCreate_InvalidComment() {
        CommentDto commentDto = new CommentDto();

        assertThrows(DataValidationException.class, () -> commentValidator.validateCommentBeforeCreate(commentDto));
    }

    @Test
    public void testValidateCommentBeforeUpdate_ValidComment() {
        Long commentId = 1L;
        CommentDto commentDto = new CommentDto();
        commentDto.setAuthorId(1L);
        commentDto.setPostId(1L);

        Comment existingComment = new Comment();
        existingComment.setAuthorId(1L);
        Post post = new Post();
        post.setId(1L);
        existingComment.setPost(post);

        Mockito.when(commentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));

        commentValidator.validateCommentBeforeUpdate(commentId, commentDto);
    }

    @Test
    public void testValidateCommentBeforeUpdate_InvalidComment() {
        Long commentId = 1L;
        CommentDto commentDto = new CommentDto();
        commentDto.setAuthorId(2L);
        commentDto.setPostId(2L);

        Comment existingComment = new Comment();
        existingComment.setAuthorId(1L);
        Post post = new Post();
        post.setId(1L);
        existingComment.setPost(post);

        Mockito.when(commentRepository.findById(commentId)).thenReturn(Optional.of(existingComment));

        assertThrows(DataValidationException.class, () -> commentValidator.validateCommentBeforeUpdate(commentId, commentDto));
    }

    @Test
    public void testValidateCommentBeforeGetCommentsByPostId_ValidPost() {
        Long postId = 1L;
        Post post = new Post();
        post.setId(1L);

        Mockito.when(postService.getPostIfExist(postId)).thenReturn(post);

        commentValidator.validateCommentBeforeGetCommentsByPostId(postId);
    }

    @Test
    public void testValidateCommentBeforeGetCommentsByPostId_InvalidPost() {
        Long postId = 2L;

        Mockito.when(postService.getPostIfExist(postId)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> commentValidator.validateCommentBeforeGetCommentsByPostId(postId));
    }
}
