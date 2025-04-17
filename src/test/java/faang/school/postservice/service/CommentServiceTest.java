package faang.school.postservice.service;

import faang.school.postservice.dto.comment.CommentCreateDto;
import faang.school.postservice.dto.comment.CommentReadDto;
import faang.school.postservice.dto.comment.CommentUpdateDto;
import faang.school.postservice.dto.post.CommentCreatedEvent;
import faang.school.postservice.event.CommentEvent;
import faang.school.postservice.exception.BusinessException;
import faang.school.postservice.mapper.comment.CommentMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.kafka.CommentEventProducer;
import faang.school.postservice.publisher.redis.CommentEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private CommentRepository commentRepository;

    @Spy
    private CommentMapperImpl commentMapper;

    @Mock
    private PostService postService;

    @Mock
    private CommentEventPublisher commentEventPublisher;

    @Mock
    private CommentEventProducer commentEventProducer;

    @InjectMocks
    private CommentService commentService;

    private static final long USER_ID = 1L;
    private static final long COMMENT_ID = 2L;
    private static final long POST_ID = 3L;
    private final Post post = Post.builder().id(POST_ID).authorId(USER_ID).build();
    private final CommentCreateDto commentCreateDto = CommentCreateDto.builder()
            .authorId(USER_ID)
            .postId(POST_ID)
            .content("content")
            .build();

    @Test
    void testAddCommentIfUserExist() {
        when(userService.isUserExists(anyLong())).thenReturn(true);
        when(postService.findById(anyLong())).thenReturn(post);

        Comment comment = commentMapper.toEntity(commentCreateDto);
        comment.setPost(post);
        CommentReadDto expected = CommentReadDto.builder()
                .authorId(USER_ID)
                .content(commentCreateDto.getContent())
                .postId(POST_ID)
                .build();
        when(commentRepository.save(comment)).thenReturn(comment);

        CommentReadDto result = commentService.addComment(commentCreateDto);

        assertEquals(expected, result);
        verify(commentEventPublisher).publish(CommentEvent.builder()
                .postId(POST_ID)
                .authorId(USER_ID)
                .commentId(comment.getId())
                .content(comment.getContent())
                .build());
        verify(commentEventProducer).sendEvent(any(CommentCreatedEvent.class));
    }

    @Test
    void testAddCommentThrowExceptionIfUserNotExists() {
        when(userService.isUserExists(anyLong())).thenReturn(false);

        assertThrows(BusinessException.class, () -> commentService.addComment(commentCreateDto));
    }

    @Test
    void testEditComment() {
        CommentUpdateDto commentUpdateDto = CommentUpdateDto.builder()
                .content("updated content")
                .commentId(COMMENT_ID)
                .build();

        Comment comment = commentMapper.toEntity(commentCreateDto);
        Comment updatedComment = commentMapper.update(comment, commentUpdateDto);
        CommentReadDto expectedCommentDto = commentMapper.toReadDto(updatedComment);

        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        when(commentRepository.save(updatedComment)).thenReturn(updatedComment);

        CommentReadDto result = commentService.editComment(commentUpdateDto);
        assertEquals(expectedCommentDto, result);
    }

    @Test
    void testEditCommentThrowExceptionIfCommentNotExists() {
        CommentUpdateDto commentUpdateDto = CommentUpdateDto.builder()
                .content("updated content")
                .commentId(COMMENT_ID)
                .build();

        when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> commentService.editComment(commentUpdateDto));
    }

    @Test
    void testGetComments() {
        Comment comment = commentMapper.toEntity(commentCreateDto);
        List<Comment> comments = List.of(comment);
        when(commentRepository.findAllByPostId(anyLong())).thenReturn(comments);

        List<CommentReadDto> result = commentService.getComments(POST_ID);
        assertEquals(comments.size(), result.size());
    }

    @Test
    void testDeleteCommentIfCommentExists() {
        when(commentRepository.existsById(COMMENT_ID)).thenReturn(true);
        commentService.deleteComment(COMMENT_ID);

        verify(commentRepository).deleteById(COMMENT_ID);
    }

    @Test
    void testDeleteCommentIfCommentNotExists() {
        when(commentRepository.existsById(COMMENT_ID)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> commentService.deleteComment(COMMENT_ID));
        verify(commentRepository, times(0)).deleteById(COMMENT_ID);
    }
}