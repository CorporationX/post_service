package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.mapper.comment.CommentMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.comment.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Spy
    private CommentMapperImpl commentMapper;

    @InjectMocks
    private CommentService commentService;

    private CommentDto commentDto;
    private Post post;
    private UserDto userDto;

    private static final long COMMENT_ID = 1L;
    private static final long POST_ID = 10L;
    private static final long AUTHOR_ID = 20L;
    private static final String CONTENT = "Test comment content";

    @BeforeEach
    void setUp() {
        post = Post.builder().id(POST_ID).build();
        userDto = new UserDto(AUTHOR_ID, "username", "email");
        commentDto = CommentDto.builder()
                .id(COMMENT_ID)
                .authorId(AUTHOR_ID)
                .postId(POST_ID)
                .content(CONTENT)
                .build();
    }

    @Test
    void testAddCommentSuccess() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));
        when(userServiceClient.getUser(AUTHOR_ID)).thenReturn(userDto);
        when(commentRepository.save(any(Comment.class))).thenReturn(Comment.builder().id(COMMENT_ID).build());
        when(commentMapper.toDto(any(Comment.class))).thenReturn(commentDto);

        CommentDto addedComment = commentService.addComment(commentDto);

        assertNotNull(addedComment);
        assertEquals(COMMENT_ID, addedComment.getId());  // Only checking ID
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void testAddCommentFailsIfPostNotFound() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> commentService.addComment(commentDto));

        assertEquals("Post not found", exception.getMessage());
        verify(postRepository).findById(POST_ID);
        verifyNoMoreInteractions(userServiceClient, commentMapper, commentRepository);
    }

    @Test
    void testAddCommentFailsIfAuthorNotFound() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));
        when(userServiceClient.getUser(AUTHOR_ID)).thenThrow(new IllegalArgumentException("Author not found"));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> commentService.addComment(commentDto));

        assertEquals("Author not found", exception.getMessage());
        verify(userServiceClient).getUser(AUTHOR_ID);
        verify(postRepository).findById(POST_ID);
        verifyNoMoreInteractions(commentMapper, commentRepository);
    }

    @Test
    void testUpdateCommentSuccess() {
        String updatedContent = "Updated comment content";
        CommentDto updatedCommentDto = CommentDto.builder().id(COMMENT_ID).content(updatedContent).build();
        Comment existingComment = Comment.builder().id(COMMENT_ID).content(CONTENT).build();

        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(existingComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(existingComment);
        when(commentMapper.toDto(any(Comment.class))).thenReturn(updatedCommentDto);

        CommentDto result = commentService.updateComment(updatedCommentDto);

        assertEquals(updatedContent, result.getContent());
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void testUpdateCommentNotFound() {
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> commentService.updateComment(commentDto));

        assertEquals("Comment not found", exception.getMessage());
        verify(commentRepository).findById(COMMENT_ID);
    }

    @Test
    void testGetAllCommentsSuccess() {
        when(commentRepository.findAllByPostId(POST_ID)).thenReturn(List.of(new Comment(), new Comment()));
        when(commentMapper.toDto(any())).thenReturn(commentDto);

        List<CommentDto> result = commentService.getAllComments(POST_ID);

        assertEquals(2, result.size());
        verify(commentRepository).findAllByPostId(POST_ID);
    }

    @Test
    void testDeleteCommentSuccess() {
        commentService.deleteComment(COMMENT_ID);
        verify(commentRepository).deleteById(COMMENT_ID);
    }

    @Test
    void testValidateComment_InvalidInput_ThrowsException() {
        commentDto.setAuthorId(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> commentService.addComment(commentDto));
        assertNotNull(exception.getMessage());
    }
}