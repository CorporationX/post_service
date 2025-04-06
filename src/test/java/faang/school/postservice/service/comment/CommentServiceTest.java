package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.validator.CommentValidator;
import faang.school.postservice.validator.PostValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {


    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostValidator postValidator;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private CommentValidator commentValidator;

    @InjectMocks
    private CommentService commentService;

    private CommentDto commentDto;
    private Comment comment;
    private Post post;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        Long POST_ID = 1L;
        Long AUTHOR_ID = 1L;
        Long COMMENT_ID = 1L;
        commentDto = CommentDto.builder()
                .postId(POST_ID)  // Добавляем postId
                .content("Test comment")
                .authorId(AUTHOR_ID)
                .build();

        post = Post.builder()
                .id(POST_ID)
                .build();

        comment = Comment.builder()
                .id(COMMENT_ID)
                .content("Test comment")
                .authorId(AUTHOR_ID)
                .post(post)
                .createdAt(LocalDateTime.now())
                .build();

        userDto = UserDto.builder()
                .id(AUTHOR_ID)
                .build();
    }

    @Test
    void testCreateComment_Success() {
        when(postValidator.getPostById(1L)).thenReturn(post);
        when(userServiceClient.getUser(1L)).thenReturn(userDto);
        when(commentMapper.toCommentDto(any(Comment.class))).thenReturn(commentDto);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto result = commentService.createComment(1L, commentDto);

        assertNotNull(result);
        assertEquals(commentDto.getContent(), result.getContent());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void testUpdateComment_Success() {
        Comment updatedComment = Comment.builder()
                .id(1L)
                .content("Updated comment")
                .authorId(1L)
                .post(post)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        CommentDto updatedCommentDto = CommentDto.builder()
                .content("Updated comment")
                .postId(1L)
                .authorId(1L)
                .build();

        when(commentRepository.findById(1L)).thenReturn(Optional.ofNullable(comment));
        when(commentMapper.toCommentDto(any(Comment.class))).thenReturn(updatedCommentDto);
        when(commentRepository.save(any(Comment.class))).thenReturn(updatedComment);

        CommentDto result = commentService.updateComment(1L, updatedCommentDto);

        assertNotNull(result);
        assertEquals("Updated comment", result.getContent());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void testUpdateComment_CommentNotFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                commentService.updateComment(1L, commentDto)
        );
    }

    @Test
    void testGetAllComments_Success() {
        List<Comment> comments = List.of(
                Comment.builder().id(1L).content("Comment 1").createdAt(LocalDateTime.now()).build(),
                Comment.builder().id(2L).content("Comment 2").createdAt(LocalDateTime.now().minusHours(1)).build()
        );

        CommentDto commentDto1 = CommentDto.builder().id(1L).content("Comment 1").postId(1L).authorId(1L).build();
        CommentDto commentDto2 = CommentDto.builder().id(2L).content("Comment 2").postId(1L).authorId(2L).build();

        when(commentRepository.findAllByPostId(1L)).thenReturn(comments);
        when(commentMapper.toCommentDto(comments.get(0))).thenReturn(commentDto1);
        when(commentMapper.toCommentDto(comments.get(1))).thenReturn(commentDto2);

        List<CommentDto> result = commentService.getAllComments(1L);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Comment 1", result.get(0).getContent());
        verify(commentValidator, times(1)).validateListComments(1L);
    }

    @Test
    void testDeleteComment_Success() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        commentService.deleteComment(1L);

        verify(commentRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteComment_CommentNotFound() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
                commentService.deleteComment(1L)
        );
    }
}