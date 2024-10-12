package faang.school.postservice.service.comment;

import faang.school.postservice.model.dto.comment.CommentRequestDto;
import faang.school.postservice.model.dto.comment.CommentResponseDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.impl.comment.CommentServiceImpl;
import faang.school.postservice.validator.comment.CommentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private CommentValidator commentValidator;

    @InjectMocks
    private CommentServiceImpl commentService;

    private Post post;
    private CommentRequestDto commentRequestDto;
    private CommentResponseDto commentResponseDto;
    private Comment comment;

    @BeforeEach
    void setUp() {
        post = Post.builder()
                .id(1L)
                .build();

        commentRequestDto = CommentRequestDto.builder()
                .id(1L)
                .content("This is a comment")
                .postId(1L)
                .build();

        commentResponseDto = CommentResponseDto.builder()
                .id(1L)
                .content("This is a comment")
                .postId(1L)
                .authorId(1L)
                .build();

        comment = Comment.builder()
                .id(1L)
                .content("This is a comment")
                .post(post)
                .authorId(1L)
                .build();
    }

    @Test
    void create_whenUserAndPostExist_shouldCreateComment() {
        // given
        doNothing().when(commentValidator).validateUser(anyLong());
        when(commentValidator.findPostById(anyLong())).thenReturn(post);
        when(commentMapper.toEntity(any(CommentRequestDto.class))).thenReturn(comment);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.toResponseDto(any(Comment.class))).thenReturn(commentResponseDto);
        // when
        CommentResponseDto result = commentService.create(1L, commentRequestDto);
        // then
        verify(commentValidator).validateUser(1L);
        verify(commentValidator).findPostById(1L);
        verify(commentRepository).save(comment);
        assertThat(result).isEqualTo(commentResponseDto);
    }

    @Test
    void update_whenCommentExists_shouldUpdateComment() {
        // given
        when(commentValidator.findCommentById(anyLong())).thenReturn(comment);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.toResponseDto(any(Comment.class))).thenReturn(commentResponseDto);
        // when
        CommentResponseDto result = commentService.update(commentRequestDto);
        // then
        verify(commentValidator).findCommentById(commentRequestDto.id());
        verify(commentRepository).save(comment);
        assertThat(result).isEqualTo(commentResponseDto);
    }

    @Test
    void findAll_shouldReturnListOfComments() {
        // given
        List<Comment> comments = List.of(comment);
        when(commentRepository.findAllByPostId(anyLong())).thenReturn(comments);
        when(commentMapper.toResponseDto(anyList())).thenReturn(List.of(commentResponseDto));
        // when
        List<CommentResponseDto> result = commentService.findAll(1L);
        // then
        verify(commentRepository).findAllByPostId(1L);
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(commentResponseDto);
    }

    @Test
    void delete_whenCommentExists_shouldDeleteComment() {
        // when
        commentService.delete(1L);
        // then
        verify(commentRepository).deleteById(1L);
    }
}