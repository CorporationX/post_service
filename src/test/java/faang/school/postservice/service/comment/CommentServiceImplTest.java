package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentServiceImpl commentService;

    private Post post;
    private CommentDto commentDto;
    private Comment comment;

    @BeforeEach
    void setUp() {
        post = Post.builder()
                .id(1L)
                .build();
        commentDto = CommentDto.builder()
                .id(1L)
                .content("This is a comment")
                .authorId(1L)
                .postId(1L)
                .build();
        comment = Comment.builder()
                .id(1L)
                .content("This is a comment")
                .post(post)
                .build();
    }

    @Test
    void create_whenUserAndPostExist_shouldCreateComment() {
        // given
        when(userServiceClient.getUser(anyLong())).thenReturn(null);
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(commentMapper.toEntity(any())).thenReturn(comment);
        when(commentRepository.save(any())).thenReturn(comment);
        when(commentMapper.toDto(any(Comment.class))).thenReturn(commentDto);
        // when
        CommentDto result = commentService.create(1L, commentDto);
        // then
        verify(userServiceClient).getUser(1L);
        verify(postRepository).findById(1L);
        verify(commentRepository).save(comment);
        assertThat(result).isEqualTo(commentDto);
    }

    @Test
    void create_whenPostNotFound_shouldThrowException() {
        // given
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());
        // when & then
        assertThatThrownBy(() -> commentService.create(1L, commentDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Post with ID: 1 not found");
        verify(commentRepository, never()).save(any());
    }

    @Test
    void update_whenCommentExists_shouldUpdateComment() {
        // given
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        when(commentRepository.save(any())).thenReturn(comment);
        when(commentMapper.toDto(any(Comment.class))).thenReturn(commentDto);
        // when
        CommentDto result = commentService.update(commentDto);
        // then
        verify(commentRepository).findById(commentDto.id());
        verify(commentRepository).save(comment);
        assertThat(result).isEqualTo(commentDto);
    }

    @Test
    void update_whenCommentNotFound_shouldThrowException() {
        // given
        when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());
        // when & then
        assertThatThrownBy(() -> commentService.update(commentDto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Comment with ID: 1 not found");
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void findAll_shouldReturnListOfComments() {
        // given
        List<Comment> comments = List.of(comment);
        when(commentRepository.findAllByPostId(anyLong())).thenReturn(comments);
        when(commentMapper.toDto(anyList())).thenReturn(List.of(commentDto));
        // when
        List<CommentDto> result = commentService.findAll(1L);
        // then
        verify(commentRepository).findAllByPostId(1L);
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(commentDto);
    }

    @Test
    void delete_whenCommentExists_shouldDeleteComment() {
        // when
        commentService.delete(1L);
        // then
        verify(commentRepository).deleteById(1L);
    }

    @Test
    void delete_whenCommentNotFound_shouldThrowException() {
        // given
        doThrow(new EntityNotFoundException("Comment with ID: 1 not found"))
                .when(commentRepository).deleteById(anyLong());
        // when & then
        assertThatThrownBy(() -> commentService.delete(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Comment with ID: 1 not found");
    }
}