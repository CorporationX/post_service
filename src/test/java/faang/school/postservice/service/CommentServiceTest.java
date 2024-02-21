package faang.school.postservice.service;

import faang.school.postservice.dto.client.CommentDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.mapper.redis.RedisCommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.KafkaCommentProducer;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.service.redis.CommentEventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentEventPublisher commentEventPublisher;
    @Mock
    private KafkaCommentProducer kafkaCommentProducer;
    @Mock
    private RedisPostRepository redisPostRepository;
    @Mock
    private RedisCommentMapper redisCommentMapper;
    @InjectMocks
    private CommentService commentService;

    @Test
    void createCommentTest() {
        CommentDto commentDto = new CommentDto();
        commentDto.setAuthorId(1L);
        commentDto.setId(1L);

        Comment comment = new Comment();
        comment.setId(1L);

        when(commentMapper.toEntity(commentDto)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.toDto(comment)).thenReturn(commentDto);

        commentService = new CommentService(commentRepository, redisPostRepository, commentEventPublisher, kafkaCommentProducer, commentMapper, redisCommentMapper);

        CommentDto result = commentService.createComment(1L, commentDto);
        assertNotNull(result);
        assertEquals(commentDto, result);
    }

    @Test
    void getCommentTest() {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);

        Comment comment = new Comment();
        comment.setId(1L);

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentMapper.toDto(comment)).thenReturn(commentDto);

        commentService = new CommentService(commentRepository, redisPostRepository, commentEventPublisher, kafkaCommentProducer, commentMapper, redisCommentMapper);

        CommentDto result = commentService.getComment(1L);

        assertNotNull(result);
        assertEquals(commentDto, result);
    }

    @Test
    void updateCommentTest() {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setContent("update content");
        commentDto.setAuthorId(1L);

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setContent("old content");

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentMapper.toDto(comment)).thenReturn(commentDto);

        commentService = new CommentService(commentRepository, redisPostRepository, commentEventPublisher, kafkaCommentProducer, commentMapper, redisCommentMapper);

        CommentDto result = commentService.updateComment(commentDto);

        assertNotNull(result);
        assertEquals(commentDto, result);
        assertEquals("update content", result.getContent());
    }

    @Test
    void getAllCommentsByIdTest() {
        Pageable pageable = Pageable.unpaged();
        Comment comment = new Comment();
        comment.setId(1L);

        Page<Comment> comments = Mockito.mock(Page.class);

        when(commentRepository.findAll(Example.of(Comment.builder()
                .post((Post.builder()
                .id(1L).build()))
                .build()), pageable)).thenReturn(comments);
        when(commentMapper.toDto(any(Comment.class))).thenReturn(new CommentDto());

        Page<CommentDto> result = commentService.getAllCommentsById(pageable, 1L);

        //assertNotNull(result);

        verify(commentRepository).findAll(any(Example.class), any(Pageable.class));
        verify(commentMapper).toDto(any(Comment.class));
    }

    @Test
    void deleteCommentTest() {
        long commentId = 1L;

        doNothing().when(commentRepository).deleteById(commentId);

        commentService = new CommentService(commentRepository, redisPostRepository, commentEventPublisher, kafkaCommentProducer, commentMapper, redisCommentMapper);

        commentService.deleteComment(commentId);
        verify(commentRepository).deleteById(commentId);
    }
}