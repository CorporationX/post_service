package faang.school.postservice.service.comment;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validation.comment.UserClientValidation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    private PostService postService;
    @Mock
    private UserClientValidation userClientValidation;
    CommentDto commentDto;
    Comment existingComment;
    Post post;
    long postId = 1L;

    @BeforeEach
    void init() {
        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setAuthorId(1L);
        commentDto.setContent("Updated content");
        commentDto.setPostId(1L);

        existingComment = new Comment();
        existingComment.setId(1L);
        existingComment.setContent("Original content");

        post = new Post();
    }

    @Test
    @DisplayName("testAddCommentService")
    void testAddCommentService() {
        doNothing().when(userClientValidation).checkUser(anyLong());
        when(commentMapper.toEntity(any(CommentDto.class)))
                .thenReturn(existingComment);
        when(postService.getPost(anyLong()))
                .thenReturn(post);
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(existingComment);
        when(commentMapper.toDto(any(Comment.class))).thenReturn(new CommentDto());

        commentService.addNewCommentInPost(commentDto);

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
    @DisplayName("testUpdateCommentService")
    void testUpdateCommentService() {
        Comment updatedComment = new Comment();
        updatedComment.setId(1L);
        updatedComment.setContent("Updated content");

//        Mockito.when(userServiceClient.getUser(anyLong()))
//                .thenReturn(new UserDto(1L, null, null));
        doNothing().when(userClientValidation).checkUser(anyLong());
        when(commentRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(existingComment));
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(updatedComment);
        when(commentMapper.toDto(updatedComment))
                .thenReturn(commentDto);

        CommentDto result = commentService.updateExistingComment(commentDto);

        verify(commentRepository, times(1))
                .findById(anyLong());
        verify(commentRepository, times(1))
                .save(any(Comment.class));
        verify(commentMapper, times(1))
                .toDto(updatedComment);

        assertEquals("Updated content", result.getContent());
    }

    @Test
    @DisplayName("testGetCommentsService")
    void testGetCommentsService() {
        Comment commentOne = Comment.builder().id(1L).content("Updated content").build();
        Post post1 = Post.builder().id(postId).comments(List.of(commentOne)).build();
        when(postService.getPost(postId)).thenReturn(post1);
        when(commentMapper.toDto(any(Comment.class))).thenReturn(commentDto);

        commentService.getCommentsForPost(postId);

        verify(postService, times(1)).getPost(postId);
        verify(commentMapper, times(1)).toDto(commentOne);
    }

    @Test
    @DisplayName("testDeleteCommentService")
    void testDeleteCommentService() {
        doNothing().when(commentRepository).deleteById(commentDto.getAuthorId());

        CommentDto resultTest = commentService.deleteExistingCommentInPost(commentDto);

        verify(commentRepository, times(1)).deleteById(existingComment.getId());
        assertEquals(commentDto, resultTest);
    }
}