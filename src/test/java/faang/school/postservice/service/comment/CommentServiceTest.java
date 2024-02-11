package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.exception.comment.DataValidationException;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validation.comment.CommentValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;


@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentValidator commentValidator;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentService commentService;

    @Mock
    private Comment comment;

    @Mock
    private Post post;

    private CommentDto commentDto;

    @BeforeEach
    public void init() {
        commentDto = CommentDto.builder()
                .authorId(1L)
                .id(2L)
                .content("Content")
                .postId(3L)
                .build();
    }

    @Test
    public void whenAddNewCommentThenNoDataInDB() {
        try {
            commentService.addNewComment(1L, new CommentDto());
        } catch (DataValidationException e) {
            assertThat(e).isInstanceOf(RuntimeException.class)
                    .hasMessage("There are no posts with that ID");
        }
    }

    @Test
    public void whenAddNewCommentThenSuccess() {
        Mockito.when(commentRepository.findById(anyLong()))
                .thenReturn(Optional.of(comment));
        Mockito.when(postRepository.findById(anyLong()))
                .thenReturn(Optional.of(post));
        commentService.addNewComment(1L, new CommentDto());
        Mockito.verify(commentRepository, times(1))
                .save(comment);
    }

}

