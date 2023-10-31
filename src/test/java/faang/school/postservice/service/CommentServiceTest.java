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
import org.mockito.junit.jupiter.MockitoExtension;

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
    void getAllCommentsTest() {
        Comment comment1 = new Comment();
        comment1.setId(1L);
        comment1.setContent("comment1");

        Comment comment2 = new Comment();
        comment2.setId(2L);
        comment2.setContent("comment2");

        List<Comment> commentList = List.of(comment1, comment2);

        CommentDto commentDto1 = new CommentDto();
        commentDto1.setId(1L);
        commentDto1.setContent("comment1");

        CommentDto commentDto2 = new CommentDto();
        commentDto2.setId(2L);
        commentDto2.setContent("comment2");

        List<CommentDto> commentDtoList = List.of(commentDto1, commentDto2);

        when(commentRepository.findAll()).thenReturn(commentList);
        when(commentMapper.toDto(comment1)).thenReturn(commentDto1);
        when(commentMapper.toDto(comment2)).thenReturn(commentDto2);

        commentService = new CommentService(commentRepository, redisPostRepository, commentEventPublisher, kafkaCommentProducer, commentMapper, redisCommentMapper);

        List<CommentDto> result = commentService.getAllComments();

        assertNotNull(result);
        assertEquals(commentDtoList, result);
    }

    @Test
    void getAllCommentsByIdTest() {
        Post post = new Post();
        post.setId(1L);
        Comment comment1 = new Comment();
        comment1.setId(1L);
        comment1.setContent("comment1");
        comment1.setId(post.getId());

        Comment comment2 = new Comment();
        comment2.setId(2L);
        comment2.setContent("comment2");
        comment2.setId(post.getId());

        List<Comment> commentList = List.of(comment1, comment2);

        CommentDto commentDto1 = new CommentDto();
        commentDto1.setId(1L);
        commentDto1.setContent("comment1");

        CommentDto commentDto2 = new CommentDto();
        commentDto2.setId(2L);
        commentDto2.setContent("comment2");

        List<CommentDto> commentDtoList = List.of(commentDto1, commentDto2);

        when(commentRepository.findAllByPostId(post.getId())).thenReturn(commentList);
        when(commentMapper.toDto(comment1)).thenReturn(commentDto1);
        when(commentMapper.toDto(comment2)).thenReturn(commentDto2);

        commentService = new CommentService(commentRepository, redisPostRepository, commentEventPublisher, kafkaCommentProducer, commentMapper, redisCommentMapper);

        List<CommentDto> result = commentService.getAllCommentsById(post.getId());

        assertNotNull(result);
        assertEquals(commentDtoList, result);
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