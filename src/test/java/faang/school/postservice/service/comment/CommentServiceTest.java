package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.comment.CommentMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.validator.comment.CommentServiceValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @Spy
    CommentMapperImpl commentMapper = new CommentMapperImpl();

    @Mock
    private CommentRepository commentRepository;

    @Mock
    CommentServiceValidator validator;

    @InjectMocks
    private CommentService commentService;

    @Test
    void create() {
    }

    @Test
    void get() {
        Long postId = 10L;
        List<Comment> comments = createComments(postId);
        when(commentRepository.findAllByPostId(postId)).thenReturn(comments);

        List<CommentDto> commentDtos = commentService.get(postId);

        assertAll(
                () -> assertEquals(2, commentDtos.size()),
                () -> assertEquals(commentDtos.get(0).getPostId(), postId),
                () -> assertEquals(commentDtos.get(1).getPostId(), postId),
                () -> assertTrue(commentDtos.get(0).getUpdatedAt().isAfter(commentDtos.get(1).getUpdatedAt()))
        );
    }

    @Test
    void deleteSuccessful() {
        Long commentId = 10L;
        Comment comment = new Comment();
        comment.setId(commentId);

        assertDoesNotThrow(() -> commentService.delete(commentId));
    }

    @Test
    void update() {
    }

    private List<Comment> createComments(Long postId) {
        Post post = new Post();
        post.setId(postId);
        List<Comment> comments = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            comments.add(new Comment());
        }
        comments.get(0).setPost(post);
        comments.get(0).setUpdatedAt(LocalDateTime.now());
        comments.get(1).setPost(post);
        comments.get(1).setUpdatedAt(LocalDateTime.now().plusMinutes(1));
        return comments;
    }

}