package faang.school.postservice.service;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.client.UserDto;
import faang.school.postservice.dto.kafka.CommentPostEvent;
import faang.school.postservice.dto.kafka.EventAction;
import faang.school.postservice.dto.redis.CommentEventDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.mapper.CommentMapperImpl;
import faang.school.postservice.mapper.redis.RedisCommentMapper;
import faang.school.postservice.mapper.redis.RedisCommentMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.KafkaCommentProducer;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.redis.CommentEventPublisher;
import faang.school.postservice.validator.CommentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private PostService postService;
    @Mock
    private RedisCacheService redisCacheService;
    @Mock
    private UserContext userContext;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentEventPublisher redisCommentEventPublisher;
    @Mock
    private KafkaCommentProducer kafkaCommentEventPublisher;
    @Spy
    private CommentMapper commentMapper = new CommentMapperImpl();
    @Spy
    private RedisCommentMapper redisCommentMapper = new RedisCommentMapperImpl();
    @Mock
    private CommentValidator commentValidator;
    @InjectMocks
    private CommentService commentService;

    private CommentDto commentCreateDto;
    private CommentDto commentUpdateDto;

    private Comment comment;

    private CommentEventDto commentEventDto;
    private CommentPostEvent commentPostEvent;

    private UserDto userDto;

    private Post post;

    private final Long authorId = 1L;
    private final Long postId = 1L;
    private final Long commentId = 1L;

    private final LocalDateTime currentTime = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        post = Post.builder()
                .id(postId)
                .build();
        commentCreateDto = CommentDto.builder()
                .content("content")
                .authorId(authorId)
                .postId(postId)
                .build();
        commentUpdateDto = CommentDto.builder()
                .id(commentId)
                .content("update content")
                .authorId(authorId)
                .postId(postId)
                .build();
        comment = Comment.builder()
                .id(commentId)
                .content("content")
                .authorId(authorId)
                .post(post)
                .createdAt(currentTime)
                .build();
        commentEventDto = CommentEventDto.builder()
                .commentId(commentId)
                .authorId(authorId)
                .postId(postId)
                .createdAt(currentTime)
                .build();
        commentPostEvent = CommentPostEvent.builder()
                .postId(postId)
                .commentDto(redisCommentMapper.toDto(comment))
                .eventAction(EventAction.CREATE)
                .build();
        userDto = UserDto.builder()
                .id(authorId)
                .build();

    }

    @Test
    void createCommentTest() {
        when(postService.findAlreadyPublishedAndNotDeletedPost(postId))
                .thenReturn(Optional.of(post));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(redisCacheService.findUserBy(authorId)).thenReturn(userDto);

        CommentDto result = commentService.createComment(commentCreateDto);

        assertEquals(commentMapper.toDto(comment), result);

        verify(commentValidator).validateUserExistence(authorId);
        verify(postService).findAlreadyPublishedAndNotDeletedPost(postId);
        verify(commentRepository).save(any(Comment.class));
        verify(redisCommentEventPublisher).publish(commentEventDto);
        verify(kafkaCommentEventPublisher).publish(commentPostEvent);
        verify(redisCacheService).findUserBy(authorId);
        verify(redisCacheService).updateOrCacheUser(userDto);
    }

    @Test
    void updateCommentTest() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        CommentDto result = commentService.updateComment(commentUpdateDto);

        assertEquals("update content", result.getContent());

        verify(commentRepository).findById(commentId);
        verify(kafkaCommentEventPublisher).publish(any(CommentPostEvent.class));
    }

    @Test
    void getCommentsByPostTest() {
        Pageable pageable = PageRequest.of(0, 5);
        Pageable sortedPageable = PageRequest.of(0, 5, Sort.by(Sort.Order.asc("createdAt")));

        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withIgnorePaths("authorId", "verified", "post.views",
                        "post.published", "post.corrected", "post.deleted",
                        "post.verified")
                .withMatcher("post.id", ExampleMatcher.GenericPropertyMatcher::exact);

        Example<Comment> example = Example.of(Comment.builder().post(post).build(), exampleMatcher);

        Page<Comment> page = new PageImpl<>(new ArrayList<>(List.of(comment)));
        Page<CommentDto> expected = new PageImpl<>(List.of(commentMapper.toDto(comment)));

        when(commentRepository.findAll(example, sortedPageable)).thenReturn(page);

        Page<CommentDto> result = commentService.getCommentsByPost(postId, pageable);

        assertEquals(expected, result);
        verify(commentRepository).findAll(example, sortedPageable);
    }

    @Test
    void deleteCommentTest() {
        CommentPostEvent deleteEvent = CommentPostEvent.builder()
                .postId(postId)
                .commentDto(redisCommentMapper.toDto(comment))
                .eventAction(EventAction.DELETE)
                .build();

        when(userContext.getUserId()).thenReturn(authorId);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        commentService.deleteComment(commentId);

        verify(userContext).getUserId();
        verify(commentRepository).findById(commentId);
        verify(commentRepository).deleteById(commentId);
        verify(kafkaCommentEventPublisher).publish(deleteEvent);
    }

    @Test
    void getCommentTest() {
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        Comment result = commentService.getComment(commentId);

        assertEquals(comment, result);
        verify(commentRepository).findById(commentId);
    }

    @Test
    void findUnverifiedCommentsTest() {
        when(commentRepository.findUnverifiedComments()).thenReturn(List.of(comment));

        List<Comment> result = commentService.findUnverifiedComments();

        assertEquals(List.of(comment), result);
        verify(commentRepository).findUnverifiedComments();
    }

    @Test
    void saveAllTest() {
        commentService.saveAll(List.of(comment));
        verify(commentRepository).saveAll(List.of(comment));
    }
}
