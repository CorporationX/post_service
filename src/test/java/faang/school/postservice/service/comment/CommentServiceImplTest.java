package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentRequestDto;
import faang.school.postservice.dto.comment.CommentResponseDto;
import faang.school.postservice.event.BanEvent;
import faang.school.postservice.event.CommentEvent;
import faang.school.postservice.mapper.comment.CommentMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.CommentEventPublisher;
import faang.school.postservice.publisher.RedisBanMessagePublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.impl.comment.CommentServiceImpl;
import faang.school.postservice.validator.comment.CommentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Spy
    private CommentMapperImpl commentMapper;

    @Mock
    private CommentValidator commentValidator;

    @Mock
    private RedisBanMessagePublisher redisBanMessagePublisher;

    @Mock
    private CommentEventPublisher commentEventPublisher;

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
                .authorId(1L)
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
        var commentEvent = CommentEvent.builder()
                .commentAuthorId(commentResponseDto.authorId())
                .postAuthorId(post.getAuthorId())
                .postId(post.getId())
                .content(commentResponseDto.content())
                .commentId(commentResponseDto.id())
                .build();

        doNothing().when(commentValidator).validateUser(anyLong());
        when(commentValidator.findPostById(anyLong())).thenReturn(post);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.toResponseDto(any(Comment.class))).thenReturn(commentResponseDto);
        // when
        CommentResponseDto result = commentService.create(1L, commentRequestDto);
        // then
        verify(commentValidator).validateUser(1L);
        verify(commentValidator).findPostById(1L);
        verify(commentRepository).save(comment);
        verify(commentEventPublisher).publish(commentEvent);
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
    void testCommentersBanCheck() {
        // Arrange
        Comment comment1 = new Comment();
        comment1.setAuthorId(1L);

        Comment comment2 = new Comment();
        comment2.setAuthorId(1L);

        Comment comment3 = new Comment();
        comment3.setAuthorId(2L);

        when(commentRepository.findAllByVerifiedFalse()).thenReturn(List.of(comment1, comment2, comment3));

        // Act
        commentService.commentersBanCheck(2);

        // Assert
        verify(redisBanMessagePublisher, times(1)).publish(new BanEvent(1L));
        verify(redisBanMessagePublisher, never()).publish(new BanEvent(2L));
    }
}
