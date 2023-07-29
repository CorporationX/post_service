package faang.school.postservice.service.comment;

import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;


class CommentServiceTest {
    @InjectMocks
    private CommentService commentService;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private CommentRepository commentRepository;
    private long rightId;
    private List<Comment> comments = new ArrayList<>();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        rightId = 1L;

        Mockito.when(commentRepository.findAllByPostId(rightId))
                .thenReturn(comments);
    }

    @Test
    void createComment() {
    }

    @Test
    void updateComment() {
    }

    @Test
    void getAllComments() {
        commentService.getAllComments(rightId);

        Mockito.verify(commentMapper, Mockito.times(1))
                .toDto(comments);
        Mockito.verify(commentRepository, Mockito.times(1))
                .findAllByPostId(rightId);
    }

    @Test
    void deleteComment() {
    }
}