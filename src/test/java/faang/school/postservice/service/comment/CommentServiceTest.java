package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentServiceTest {
    @InjectMocks
    private CommentService commentService;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentMapper commentMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateComment() {
        Comment comment = new Comment();
        CommentDto commentDto = CommentDto.builder().build();
        Mockito.when(commentMapper.toEntity(commentDto))
                .thenReturn(comment);

        assertEquals(null, commentDto.getCreatedAt());
        commentService.createComment(commentDto);
        LocalDateTime time = commentDto.getCreatedAt();

        Mockito.verify(commentMapper, Mockito.times(1))
                .toEntity(commentDto);
        Mockito.verify(commentRepository, Mockito.times(1))
                .save(comment);

        commentService.createComment(commentDto);
        assertEquals(time, commentDto.getCreatedAt());
    }

    @Test
    void updateComment() {
    }

    @Test
    void getAllComments() {
    }

    @Test
    void deleteComment() {
    }
}