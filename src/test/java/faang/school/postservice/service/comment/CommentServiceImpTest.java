package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.post.PostService;
import feign.FeignException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = CommentServiceImp.class)
class CommentServiceImpTest {

    @Autowired
    private CommentServiceImp commentServiceImp;

    @MockBean
    private CommentRepository commentRepository;
    @MockBean
    private CommentMapper commentMapper;
    @MockBean
    private UserServiceClient userServiceClient;
    @MockBean
    private PostService postService;

    private CommentDto buildCommentDto() {
        return new CommentDto(1L, "Test content", 2L, 3L, LocalDateTime.now());
    }

    private Comment buildComment() {
        Comment comment = new Comment();
        Post post = Post.builder()
                .id(3L)
                .build();
        comment.setId(1L);
        comment.setContent("Test content");
        comment.setAuthorId(2L);
        comment.setPost(post);
        comment.setCreatedAt(LocalDateTime.now());
        return comment;
    }

    @Test
    @DisplayName("Should create comment when user and post are valid")
    void createCommentTest_success() {
        CommentDto request = buildCommentDto();
        Comment comment = buildComment();

        when(userServiceClient.getUser(request.getAuthorId()))
                .thenReturn(new UserDto(request.getAuthorId(), "testuser", "testuser@example.com"));
        when(commentMapper.toEntity(request)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.toCommentDto(comment)).thenReturn(request);

        CommentDto result = commentServiceImp.createComment(request);

        assertThat(result).isEqualTo(request);
        verify(userServiceClient).getUser(request.getAuthorId());
        verify(commentRepository).save(comment);
    }

    @Test
    @DisplayName("Should throw exception if user does not exist")
    void createCommentTest_userNotFound() {
        CommentDto request = buildCommentDto();

        doThrow(mock(FeignException.class)).when(userServiceClient).getUser(request.getAuthorId());

        assertThatThrownBy(() -> commentServiceImp.createComment(request))
                .isInstanceOf(DataValidationException.class)
                .hasMessageContaining("User with ID:" + request.getAuthorId() + " is not present");
    }

    @Test
    @DisplayName("Should update comment content when IDs match")
    void updateCommentContentTest_success() {
        CommentDto request = buildCommentDto();
        Comment comment = buildComment();

        when(commentRepository.findById(request.getId())).thenReturn(Optional.of(comment));
        doNothing().when(commentMapper).updateCommentContent(comment, request);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.toCommentDto(comment)).thenReturn(request);

        CommentDto result = commentServiceImp.updateCommentContent(request.getId(), request);

        assertThat(result).isEqualTo(request);
        verify(commentMapper).updateCommentContent(comment, request);
        verify(commentRepository).save(comment);
    }

    @Test
    @DisplayName("Should throw exception if IDs do not match in update")
    void updateCommentContentTest_idMismatch() {
        CommentDto request = buildCommentDto();
        long pathId = request.getId() + 1;

        assertThatThrownBy(() -> commentServiceImp.updateCommentContent(pathId, request))
                .isInstanceOf(DataValidationException.class)
                .hasMessageContaining("The IDs in the path and in the request body do not match.");
    }

    @Test
    @DisplayName("Should get all comments for a post, sorted by createdAt descending")
    void getAllCommentsTest_success() {
        CommentDto request = buildCommentDto();
        Comment comment = buildComment();

        when(postService.getPost(request.getPostId())).thenReturn(Optional.of(mock(Post.class)));
        when(commentRepository.findAllByPostId(request.getPostId())).thenReturn(List.of(comment));
        when(commentMapper.toCommentDto(comment)).thenReturn(request);

        List<CommentDto> result = commentServiceImp.getAllComments(request);

        assertThat(result).hasSize(1).containsExactly(request);
        verify(postService).getPost(request.getPostId());
        verify(commentRepository).findAllByPostId(request.getPostId());
    }

    @Test
    @DisplayName("Should throw exception if post does not exist")
    void getAllCommentsTest_postNotFound() {
        CommentDto request = buildCommentDto();

        when(postService.getPost(request.getPostId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentServiceImp.getAllComments(request))
                .isInstanceOf(DataValidationException.class)
                .hasMessageContaining("There are no Post with ID:" + request.getPostId());
    }

    @Test
    @DisplayName("Should delete comment by ID")
    void deleteCommentTest_success() {
        Comment comment = buildComment();

        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));
        doNothing().when(commentRepository).delete(comment);

        commentServiceImp.deleteComment(comment.getId());

        verify(commentRepository).delete(comment);
    }

    @Test
    @DisplayName("Should throw exception if comment to delete not found")
    void deleteCommentTest_notFound() {
        long id = 42L;
        when(commentRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> commentServiceImp.deleteComment(id))
                .isInstanceOf(DataValidationException.class)
                .hasMessageContaining("There are no comment with ID=" + id);
    }
}
