package faang.school.postservice.validator.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.model.dto.user.UserDto;
import faang.school.postservice.model.entity.Comment;
import faang.school.postservice.model.entity.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentValidatorTest {

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private CommentValidator commentValidator;

    @Test
    void validateUser_whenUserNotFound_shouldThrowException() {
        // given
        when(userServiceClient.getUser(anyLong())).thenReturn(null);
        // when & then
        assertThatThrownBy(() -> commentValidator.validateUser(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Author with ID: 1 not found");
        verify(userServiceClient).getUser(1L);
    }

    @Test
    void validateUser_whenUserExists_shouldNotThrowException() {
        // given
        UserDto userDto = UserDto.builder()
                .id(1L)
                .username("John Doe")
                .build();
        when(userServiceClient.getUser(anyLong())).thenReturn(userDto);
        // when & then
        commentValidator.validateUser(1L);
        verify(userServiceClient).getUser(1L);
    }

    @Test
    void findPostById_whenPostNotFound_shouldThrowException() {
        // given
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());
        // when
        assertThatThrownBy(() -> commentValidator.findPostById(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Post with ID: 1 not found");
        // then
        verify(postRepository).findById(1L);
    }

    @Test
    void findPostById_whenPostExists_shouldReturnPost() {
        // given
        Post post = Post.builder().build();
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        // when
        Post result = commentValidator.findPostById(1L);
        // then
        assertThat(result).isNotNull();
        verify(postRepository).findById(1L);
    }

    @Test
    void findCommentById_whenCommentNotFound_shouldThrowException() {
        // given
        when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());
        // when & then
        assertThatThrownBy(() -> commentValidator.findCommentById(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Comment with ID: 1 not found");
        verify(commentRepository).findById(1L);
    }

    @Test
    void findCommentById_whenCommentExists_shouldReturnComment() {
        // given
        Comment comment = Comment.builder().build();
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        // when
        Comment result = commentValidator.findCommentById(1L);
        // then
        assertThat(result).isNotNull();
        verify(commentRepository).findById(1L);
    }
}