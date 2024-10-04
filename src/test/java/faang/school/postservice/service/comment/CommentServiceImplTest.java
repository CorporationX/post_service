package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentRequestDto;
import faang.school.postservice.dto.comment.CommentResponseDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.validator.comment.CommentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

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

    private Comment verifiedComment;
    private Comment unverifiedComment1;
    private Comment unverifiedComment2;

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


        verifiedComment = Comment.builder()
                .id(1L)
                .content("This is a verified comment")
                .authorId(1L)
                .verified(true)
                .build();

        unverifiedComment1 = Comment.builder()
                .id(2L)
                .content("This is an unverified comment")
                .authorId(1L)
                .verified(false)
                .build();

        unverifiedComment2 = Comment.builder()
                .id(3L)
                .content("This is another unverified comment")
                .authorId(2L)
                .verified(false)
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

    @Test
    void collectUnverifiedComments_shouldReturnOnlyUnverifiedComments() {
        // Given
        List<Comment> allComments = List.of(verifiedComment, unverifiedComment1, unverifiedComment2);
        when(commentRepository.findAll()).thenReturn(allComments);

        // When
        List<Comment> result = commentService.collectUnverifiedComments();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).contains(unverifiedComment1, unverifiedComment2);
    }

    @Test
    void groupUnverifiedCommentAuthors_shouldGroupByAuthorId() {
        // Given
        List<Comment> unverifiedComments = List.of(unverifiedComment1, unverifiedComment2, unverifiedComment1);

        // When
        Map<Long, Long> result = commentService.groupUnverifiedCommentAuthors(unverifiedComments);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(1L)).isEqualTo(2L); // Author 1L has 2 unverified comments
        assertThat(result.get(2L)).isEqualTo(1L); // Author 2L has 1 unverified comment
    }
}