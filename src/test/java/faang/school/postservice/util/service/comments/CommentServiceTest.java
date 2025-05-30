package faang.school.postservice.util.service.comments;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.comments.CommentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @Spy
    private CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @InjectMocks
    private CommentService commentService;

    @Test
    void createCommentSuccess() {
        UserDto fakeUser = new UserDto(1L, "test", "test@mail,ru");

        CommentDto dto = new CommentDto();
        dto.setContent("comment");
        dto.setAuthorId(1L);
        dto.setPostId(2L);

        Post post = new Post();
        post.setId(2L);

        Comment saved = new Comment();
        saved.setId(3L);
        saved.setContent(dto.getContent());
        saved.setAuthorId(dto.getAuthorId());
        saved.setPost(post);

        CommentDto expected = new CommentDto();
        expected.setId(3L);
        expected.setContent(dto.getContent());
        expected.setAuthorId(dto.getAuthorId());
        expected.setPostId(post.getId());

        when(userServiceClient.getUser(1L)).thenReturn(fakeUser);
        when(postRepository.findById(2L)).thenReturn(Optional.of(post));
        when(commentRepository.save(any(Comment.class))).thenReturn(saved);

        CommentDto result = commentService.createComment(dto);

        assertEquals(expected, result);
        verify(userServiceClient).getUser(1L);
        verify(postRepository).findById(2L);
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void createComment_whenUserNotFound() {
        CommentDto dto = new CommentDto();
        dto.setContent("Test");
        dto.setAuthorId(5L);
        dto.setPostId(1L);

        Mockito.doThrow(new IllegalArgumentException()).when(userServiceClient).getUser(5L);

        assertThrows(IllegalArgumentException.class, () -> commentService.createComment(dto));
    }

    @Test
    void getCommentsByPostId_returnsSortedComments() {
        long postId = 1L;
        Comment comment1 = new Comment();
        comment1.setCreatedAt(java.time.LocalDateTime.now().minusMinutes(10));
        Comment comment2 = new Comment();
        comment2.setCreatedAt(java.time.LocalDateTime.now());

        List<Comment> comments = List.of(comment1, comment2);

        Mockito.when(commentRepository.findAllByPostId(postId)).thenReturn(comments);
        Mockito.when(commentMapper.toDto(Mockito.any())).thenReturn(new CommentDto());

        List<CommentDto> dto = commentService.getCommentsByPostId(postId);

        assertEquals(2, dto.size());
    }

    @Test
    void updateComment_authorOrPostChanged_throwsException() {
        CommentDto dto = new CommentDto();
        dto.setContent("Updated");
        dto.setAuthorId(999L); // wrong authorID
        dto.setPostId(123L);   // wrong postID

        Comment comment = new Comment();
        comment.setAuthorId(1L);
        Post post = new Post();
        post.setId(2L);
        comment.setPost(post);

        Mockito.when(commentRepository.findById(3L)).thenReturn(java.util.Optional.of(comment));

        assertThrows(IllegalArgumentException.class, () -> commentService.updateComment(3L, dto));
    }
}
