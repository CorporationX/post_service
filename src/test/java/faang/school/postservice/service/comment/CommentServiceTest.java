package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.post.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @InjectMocks
    private CommentService commentService;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private PostService postService;
    CommentDto commentDto;
    Comment existingComment;
    Post post;

    @BeforeEach
    void init() {
        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setAuthorId(1L);
        commentDto.setContent("Updated content");

        existingComment = new Comment();
        existingComment.setId(1L);
        existingComment.setContent("Original content");

        post = new Post();
    }

    @Test
    void testAddCommentServiceValidateAuthorExists() {
        testValidateAuthorExists();
    }

    @Test
    void testAddCommentService() {
        when(userServiceClient.getUser(anyLong()))
                .thenReturn(new UserDto(1L, null, null));
        when(commentMapper.toEntity(any(CommentDto.class)))
                .thenReturn(existingComment);
        when(postService.getPost(anyLong()))
                .thenReturn(post);
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(existingComment);
        when(commentMapper.toDto(any(Comment.class))).thenReturn(new CommentDto());

        commentService.addCommentService(1L, commentDto);

        verify(commentMapper, times(1))
                .toEntity(any(CommentDto.class));
        verify(postService, times(1))
                .getPost(anyLong());
        verify(commentRepository, times(1))
                .save(existingComment);
        verify(commentMapper, times(1))
                .toDto(any(Comment.class));
    }

    @Test
    void testUpdateCommentService() {
        Long postId = 1L;

        Comment updatedComment = new Comment();
        updatedComment.setId(1L);
        updatedComment.setContent("Updated content");

        Mockito.when(userServiceClient.getUser(anyLong()))
                .thenReturn(new UserDto(1L, null, null));
        when(commentRepository.findAllByPostId(anyLong()))
                .thenReturn(List.of(existingComment));
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(updatedComment);
        when(commentMapper.toDto(updatedComment))
                .thenReturn(commentDto);

        CommentDto result = commentService.updateCommentService(postId, commentDto);

        verify(commentRepository, times(1))
                .findAllByPostId(anyLong());
        verify(commentRepository, times(1))
                .save(any(Comment.class));
        verify(commentMapper, times(1))
                .toDto(updatedComment);

        assertEquals("Updated content", result.getContent());
    }

    @Test
    void testGetCommentsService() {
        long postId = 1L;
        Comment commentOne = Comment.builder().id(1L).content("Updated content").build();
        Post post1 = Post.builder().id(postId).comments(List.of(commentOne)).build();
        when(postService.getPost(postId)).thenReturn(post1);
        when(commentMapper.toDto(any(Comment.class))).thenReturn(commentDto);

        commentService.getCommentsService(postId);

        verify(postService, times(1)).getPost(postId);
        verify(commentMapper, times(1)).toDto(commentOne);
    }

    @Test
    void testDeleteCommentService() {
        long postId = 1L;
        Comment updatedComment = new Comment();
        updatedComment.setId(1L);
        updatedComment.setAuthorId(1L);
        updatedComment.setContent("Updated content");

        when(userServiceClient.getUser(anyLong()))
                .thenReturn(new UserDto(1L, null, null));
        when(commentRepository.findAllByPostId(anyLong())).thenReturn(List.of(updatedComment));
        when(commentMapper.toDto(updatedComment)).thenReturn(commentDto);

        CommentDto resultTest = commentService.deleteCommentService(postId, commentDto);

        verify(userServiceClient, times(1)).getUser(anyLong());
        verify(commentRepository, times(1)).findAllByPostId(postId);
        verify(commentRepository, times(1)).deleteById(updatedComment.getId());
        verify(commentMapper, times(1)).toDto(updatedComment);

        assertEquals(commentDto, resultTest);
    }

    private void testValidateAuthorExists() {
        Long postId = 1L;
        when(userServiceClient.getUser(anyLong()))
                .thenReturn(null);
        assertThrows(NullPointerException.class,
                () -> commentService.addCommentService(postId, commentDto));
    }
}