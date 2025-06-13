package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.CommentNotFoundException;
import faang.school.postservice.mapper.comment.CommentMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.util.Utils;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    @Mock
    private CommentRepository commentRepository;
    @Spy
    private Utils utils;
    @Spy
    private CommentMapperImpl commentMapper;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private PostRepository postRepository;
    @InjectMocks
    private CommentService commentService;

    private CommentDto commentDto;
    private Comment comment;
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

        comment = Comment.builder()
                .id(COMMENT_ID)
                .authorId(AUTHOR_ID)
                .content(CONTENT)
                .post(post)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    public void findCommentByIdSuccess() {
        Long commentId = 10L;
        Comment mockComment = Comment.builder()
                .id(commentId)
                .content("mock comment")
                .build();

        when(commentRepository.findById(commentId)).thenReturn(Optional.ofNullable(mockComment));

        Comment actualComment = commentService.findCommentById(commentId);
        assertNotNull(actualComment);
        assertNotNull(mockComment);
        assertEquals(mockComment.getId(), actualComment.getId());
    }

    @Test
    public void findCommentByIdFail() {
        Long commentId = 10L;
        String expected = utils.format(CommentService.COMMENT_BY_ID_NOT_FOUND, commentId);

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        CommentNotFoundException result = assertThrows(
                CommentNotFoundException.class, () -> commentService.findCommentById(commentId));
        assertEquals(expected, result.getMessage());
    }

    @Test
    void testAddCommentSuccess() {
        // Arrange
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post)); // настроили поведение postRepository

        when(userServiceClient.getUser(AUTHOR_ID)).thenReturn(userDto);
        when(commentMapper.toEntity(commentDto)).thenReturn(comment);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.toDto(comment)).thenReturn(commentDto);

        // Act
        CommentDto addedComment = commentService.addComment(commentDto);

        // Assert
        assertNotNull(addedComment);
        assertEquals(COMMENT_ID, addedComment.getId());
        assertEquals(CONTENT, addedComment.getContent());
        assertEquals(AUTHOR_ID, addedComment.getAuthorId());
        assertEquals(POST_ID, addedComment.getPostId());

        verify(userServiceClient).getUser(AUTHOR_ID);
        verify(commentRepository).save(any(Comment.class));
        verify(commentMapper).toDto(comment);
    }

    @Test
    void testAddCommentFailsIfPostNotFound() {
        when(postRepository.findById(POST_ID)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () -> commentService.addComment(commentDto));

        assertEquals("Post not found", exception.getMessage());
        verify(postRepository).findById(POST_ID);
    }

    @Test
    void testAddCommentFailsIfAuthorNotFound() {
        when(userServiceClient.getUser(AUTHOR_ID)).thenThrow(FeignException.NotFound.class);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> commentService.addComment(commentDto));

        assertEquals("Author not found", exception.getMessage());
        verify(userServiceClient).getUser(AUTHOR_ID);
        verifyNoInteractions(commentMapper, commentRepository);
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
        verify(commentRepository).findById(COMMENT_ID);
        verify(commentRepository).save(any(Comment.class));
        verify(commentMapper).toDto(any(Comment.class));
    }

    @Test
    void testUpdateCommentNotFound() {
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                commentService.updateComment(commentDto));

        assertEquals("Comment not found", exception.getMessage());
        verify(commentRepository).findById(COMMENT_ID);
        verifyNoInteractions(commentMapper);
        verifyNoMoreInteractions(commentRepository);
    }

    @Test
    void testGetAllCommentsSuccess() {
        List<Comment> comments = List.of(createComment(), createComment());
        when(commentRepository.findAllByPostId(POST_ID)).thenReturn(comments);
        when(commentMapper.toDto(any(Comment.class))).thenReturn(createCommentDto());

        List<CommentDto> result = commentService.getAllComments(POST_ID);

        assertEquals(2, result.size());
        verify(commentRepository).findAllByPostId(POST_ID);
        verify(commentMapper, times(2)).toDto(any(Comment.class));
    }

    @Test
    void deleteCommentShouldCallDeleteById() {
        Long id = 5L;
        doNothing().when(commentRepository).deleteById(id);

        commentService.deleteComment(id);

        verify(commentRepository).deleteById(id);
    }

    private CommentDto createCommentDto() {
        return CommentDto.builder()
                .id(COMMENT_ID)
                .authorId(AUTHOR_ID)
                .postId(POST_ID)
                .content(CONTENT)
                .build();
    }

    private Comment createComment() {
        return Comment.builder()
                .id(COMMENT_ID)
                .authorId(AUTHOR_ID)
                .content(CONTENT)
                .post(post)
                .createdAt(LocalDateTime.now())
                .build();
    }
}