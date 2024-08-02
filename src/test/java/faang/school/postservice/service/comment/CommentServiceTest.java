package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CommentEvent;
import faang.school.postservice.mapper.CommentMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.KafkaCommentsProducer;
import faang.school.postservice.publisher.CommentEventPublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.post.PostServiceImpl;
import faang.school.postservice.validator.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentMapperImpl commentMapper;
    @Mock
    private PostRepository postRepository;
    @Mock
    private UserValidator userValidator;
    @Mock
    private PostServiceImpl postService;
    @Mock
    private KafkaCommentsProducer kafkaCommentsProducer;
    @InjectMocks
    private CommentServiceImpl commentService;

    @Mock
    private CommentEventPublisher commentEventPublisher;

    private Comment comment;
    private CommentDto commentDto;
    private Post post;
    private Long userId;
    private Long postId;
    private Long commentId;
    private CommentDto commentDtoUpdated;
    private List<Comment> comments;
    private List<CommentDto> commentDtos;

    @BeforeEach
    void setUp() {
        userId = 2L;
        postId = 3L;
        commentId = 1L;

        post = Post.builder()
                .id(3L)
                .content("Post")
                .build();

        commentDto = CommentDto.builder()
                .id(1L)
                .content("Comment")
                .authorId(2L)
                .postId(3L)
                .createdAt(LocalDateTime.now())
                .build();

        comment = Comment.builder()
                .id(1L)
                .content("Comment")
                .authorId(2L)
                .post(post)
                .createdAt(LocalDateTime.now())
                .build();

        commentDtoUpdated = CommentDto.builder()
                .id(1L)
                .authorId(0L)
                .content("updated content")
                .build();

        comments = Arrays.asList(
                Comment.builder().id(3L).authorId(0L).content("content").createdAt(LocalDateTime.MIN.plusSeconds(10)).build(),
                Comment.builder().id(2L).authorId(0L).content("content").createdAt(LocalDateTime.MIN.plusSeconds(5)).build(),
                Comment.builder().id(1L).authorId(0L).content("content").createdAt(LocalDateTime.MIN).build()
        );

        commentDtos = Arrays.asList(
                CommentDto.builder().id(3L).authorId(0L).content("content").createdAt(LocalDateTime.MIN.plusSeconds(10)).build(),
                CommentDto.builder().id(2L).authorId(0L).content("content").createdAt(LocalDateTime.MIN.plusSeconds(5)).build(),
                CommentDto.builder().id(1L).authorId(0L).content("content").createdAt(LocalDateTime.MIN).build()
        );
    }

    @Test
    void shouldCreateComment() {
        when(postService.getPostById(anyLong())).thenReturn(post);
        when(commentMapper.toEntity(commentDto)).thenReturn(comment);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.toDto(comment)).thenReturn(commentDto);
        when(commentMapper.toEvent(commentDto)).thenReturn(new CommentEvent());

        CommentDto actual = commentService.createComment(userId, postId, commentDto);

        CommentEvent expectedEvent = CommentEvent.builder()
                .commentAuthorId(comment.getAuthorId())
                .postAuthorId(post.getAuthorId())
                .postId(post.getId())
                .commentId(comment.getId())
                .build();

        assertEquals(commentDto, actual);
        verify(userValidator).validateUserExist(userId);
        verify(postService).getPostById(postId);
        verify(commentMapper).toEntity(commentDto);
        verify(commentRepository).save(any(Comment.class));
        verify(commentMapper).toDto(comment);
        verify(commentEventPublisher).publish(expectedEvent);
        verify(commentMapper).toEvent(commentDto);
        verify(kafkaCommentsProducer).sendEvent(any(CommentEvent.class));
    }

    @Test
    void shouldUpdateComment() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentMapper.toDto(any(Comment.class))).thenReturn(commentDtoUpdated);

        CommentDto actual = commentService.updateComment(commentId, commentDtoUpdated);

        assertEquals(commentDtoUpdated, actual);
        verify(commentRepository).findById(commentId);
        verify(commentMapper).toDto(any(Comment.class));
    }

    @Test
    void shouldGetCommentsByPostId() {
        when(commentRepository.findAllByPostId(postId)).thenReturn(comments);
        when(commentMapper.toDto(comments)).thenReturn(commentDtos);

        List<CommentDto> actual = commentService.getCommentsByPostId(postId);

        assertEquals(commentDtos, actual);
        verify(commentRepository).findAllByPostId(postId);
        verify(commentMapper).toDto(comments);
    }

    @Test
    void shouldDeleteComment() {
        commentService.deleteComment(postId);
        verify(commentRepository, times(1)).deleteById(anyLong());
    }
}