package faang.school.postservice.service;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.dto.kafka.KafkaKey;
import faang.school.postservice.mapper.CommentMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.kafka.producer.KafkaCommentProducer;
import faang.school.postservice.service.redis.CommentEventPublisher;
import faang.school.postservice.validator.CommentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentEventPublisher commentEventPublisher;
    @Mock
    private KafkaCommentProducer kafkaCommentProducer;
    @Mock
    private CommentValidator commentValidator;
    @Spy
    private CommentMapperImpl commentMapper;
    @InjectMocks
    private CommentService commentService;

    private Comment comment;
    private CommentDto commentDto;
    private final Long COMMENT_ID = 1L;
    private final Long POST_ID = 1L;

    @BeforeEach
    void initData() {
        Post post = Post.builder()
                .id(1L)
                .build();
        comment = Comment.builder()
                .id(1L)
                .authorId(1L)
                .post(post)
                .content("comment")
                .build();
        commentDto = CommentDto.builder()
                .id(1L)
                .authorId(1L)
                .postId(POST_ID)
                .content("comment")
                .build();
    }

    @Test
    void testGetComment() {
        when(commentValidator.validCommentId(COMMENT_ID)).thenReturn(comment);
        CommentDto actualDto = commentService.getComment(COMMENT_ID);

        assertEquals(commentDto, actualDto);
    }

    @Test
    void testCreateComment() {
        when(commentRepository.save(comment)).thenReturn(comment);
        CommentDto actualDto = commentService.createComment(commentDto);

        assertEquals(commentDto, actualDto);
        verify(commentEventPublisher).publish(commentDto);
        verify(kafkaCommentProducer).sendMessage(KafkaKey.CREATE, commentDto);
    }

    @Test
    void testUpdateComment() {
        when(commentValidator.validCommentId(COMMENT_ID)).thenReturn(comment);
        commentDto.setContent("newContent");

        CommentDto actualDto = commentService.updateComment(commentDto);
        assertEquals(commentDto, actualDto);
        verify(kafkaCommentProducer).sendMessage(KafkaKey.UPDATE, commentDto);
    }

    @Test
    void testGetAllComments() {
        when(commentRepository.findAllByPostId(POST_ID)).thenReturn(List.of(comment));

        List<CommentDto> actualList = commentService.getAllComments(POST_ID);
        List<CommentDto> expectedList = List.of(commentDto);
        assertEquals(expectedList, actualList);
    }

    @Test
    void testDeleteComment() {
        when(commentValidator.validCommentId(COMMENT_ID)).thenReturn(comment);
        commentService.deleteComment(COMMENT_ID);

        verify(commentRepository).deleteById(COMMENT_ID);
        verify(kafkaCommentProducer).sendMessage(KafkaKey.DELETE, commentDto);
    }
}