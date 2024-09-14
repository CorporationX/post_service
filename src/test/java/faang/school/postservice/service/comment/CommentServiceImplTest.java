package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.comment.CommentMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Spy
    private CommentMapperImpl commentMapper;

    @InjectMocks
    private CommentServiceImpl commentService;

    private long authorId;
    private CommentDto commentDto;
    private long postId;
    private Post post;


    @BeforeEach
    void setUp() {
        authorId = 1L;
        postId = 1L;
        post = Post.builder()
                .id(postId)
                .authorId(authorId)
                .build();
        commentDto = CommentDto.builder()
                .id(123L)
                .content("Hello world!")
                .authorId(authorId)
                .postId(1L)
                .build();
    }

    @Test
    void addComment_ExistsUserAndPost() {
        Comment comment = Comment.builder()
                .id(123L)
                .content("Hello world!")
                .authorId(authorId)
                .post(post)
                .build();
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        CommentDto result = commentService.addComment(commentDto);

        assertEquals(commentDto, result);
        verify(commentRepository).save(any(Comment.class));
        verify(userServiceClient).getUser(authorId);
        verify(commentMapper).toDto(comment);
    }

    @Test
    void addComment_NotExistsUser() {
        when(userServiceClient.getUser(authorId)).thenThrow(FeignException.class);

        assertThrows(FeignException.class,
                () -> commentService.addComment(commentDto));

        verify(userServiceClient).getUser(authorId);
        verify(commentRepository, never()).save(any(Comment.class));
        verify(commentMapper, never()).toDto(any(Comment.class));
    }

    @Test
    void addComment_NotExistsPost() {
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> commentService.addComment(commentDto));

        verify(postRepository).findById(postId);
        verify(commentRepository, never()).save(any(Comment.class));
        verify(commentMapper, never()).toDto(any(Comment.class));
    }

    @Test
    void updateContent() {
        long commentId = 123L;
        String content = "Hello world!";

        commentService.updateContent(commentId, content);

        verify(commentRepository).updateContentById(commentId, content);
        verify(commentRepository).updateUpdateDateById(eq(commentId), any(LocalDateTime.class));
    }

    @Test
    void getCommentsByPostId() {
        long postId = 123L;
        List<Comment> comments = List.of(
                Comment.builder().id(1L).authorId(456L).content("Java").build(),
                Comment.builder().id(2L).authorId(456L).content("Python").build()
        );
        List<CommentDto> correctAnswer = List.of(
                CommentDto.builder().id(1L).authorId(456L).content("Java").build(),
                CommentDto.builder().id(2L).authorId(456L).content("Python").build()
        );
        when(commentRepository.getByPostIdOrderByCreatedAtDesc(postId)).thenReturn(comments);

        List<CommentDto> result = commentService.getCommentsByPostId(postId);

        assertEquals(correctAnswer, result);
        verify(commentRepository).getByPostIdOrderByCreatedAtDesc(postId);
        verify(commentMapper).toDto(comments);
    }

    @Test
    void deleteComment() {
        long commentId = 123L;

        commentService.deleteComment(commentId);

        verify(commentRepository).deleteById(commentId);
    }
}