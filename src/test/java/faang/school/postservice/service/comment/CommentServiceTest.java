package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.event.comment.CommentEvent;
import faang.school.postservice.mapper.comment.CommentMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.CommentEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validation.user.UserValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
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
    private PostRepository postRepository;

    @Mock
    private UserValidator userValidator;

    @Mock
    private CommentEventPublisher commentEventPublisher;

    @InjectMocks
    private CommentService commentService;

    @Test
    void createComment_ValidArgs() {
        CommentDto expected = getCommentDto();
        when(postRepository.findById(anyLong())).thenReturn(Optional.ofNullable(getPost()));
        when(commentRepository.save(any(Comment.class))).thenReturn(getComment());

        CommentDto actual = commentService.createComment(getId(), getId(), expected);

        assertEquals(expected, actual);
        verify(postRepository, times(1)).findById(anyLong());
        verify(commentEventPublisher, times(1)).publish(any(CommentEvent.class));
        verify(commentMapper, times(1)).toEntity(any(CommentDto.class));
        verify(commentMapper, times(1)).toDto(any(Comment.class));
    }

    @Test
    void updateComment_ValidArgs() {
        CommentDto expected = getCommentDtoUpdated();
        when(commentRepository.findById(anyLong())).thenReturn(Optional.ofNullable(getComment()));

        CommentDto actual = commentService.updateComment(getId(), expected);

        assertEquals(expected, actual);
        verify(commentRepository, times(1)).findById(anyLong());
        verify(commentMapper, times(1)).toDto(any(Comment.class));
    }

    @Test
    void getCommentsByPostId_ValidArgs() {
        List<CommentDto> expected = getCommentDtos();
        when(commentRepository.findAllByPostId(anyLong())).thenReturn(getComments());

        List<CommentDto> actual = commentService.getCommentsByPostId(getId());

        assertEquals(expected, actual);
        verify(commentRepository, times(1)).findAllByPostId(anyLong());
        verify(commentMapper, times(1)).toDto(anyList());
    }

    @Test
    void deleteComment_ValidArgs() {
        commentService.deleteComment(getId());

        verify(commentRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void findCommentsByVerified_ValidArgs() {
        commentService.findCommentsByVerified(true);

        verify(commentRepository, times(1)).findByVerified(true);
    }

    private List<Comment> getComments() {
        return List.of(
                Comment.builder()
                        .id(1L)
                        .authorId(0L)
                        .content("content")
                        .createdAt(LocalDateTime.MIN)
                        .build(),
                Comment.builder()
                        .id(2L)
                        .authorId(0L)
                        .content("content")
                        .createdAt(LocalDateTime.MIN.plusSeconds(5))
                        .build(),
                Comment.builder()
                        .id(3L)
                        .authorId(0L)
                        .content("content")
                        .createdAt(LocalDateTime.MIN.plusSeconds(10))
                        .build()
        );
    }

    private List<CommentDto> getCommentDtos() {
        return List.of(
                CommentDto.builder()
                        .id(3L)
                        .authorId(0L)
                        .content("content")
                        .createdAt(LocalDateTime.MIN.plusSeconds(10))
                        .build(),
                CommentDto.builder()
                        .id(2L)
                        .authorId(0L)
                        .content("content")
                        .createdAt(LocalDateTime.MIN.plusSeconds(5))
                        .build(),
                CommentDto.builder()
                        .id(1L)
                        .authorId(0L)
                        .content("content")
                        .createdAt(LocalDateTime.MIN)
                        .build()
        );
    }

    private Long getId() {
        return 1L;
    }

    private Comment getComment() {
        return Comment.builder()
                .id(1L)
                .authorId(0L)
                .post(Post.builder()
                        .id(1L)
                        .build())
                .content("content")
                .build();
    }

    private CommentDto getCommentDto() {
        return CommentDto.builder()
                .id(1L)
                .authorId(0L)
                .postId(1L)
                .content("content")
                .build();
    }

    private CommentDto getCommentDtoUpdated() {
        return CommentDto.builder()
                .id(1L)
                .authorId(0L)
                .postId(1L)
                .content("updated content")
                .build();
    }

    private Post getPost() {
        return Post.builder().build();
    }
}
