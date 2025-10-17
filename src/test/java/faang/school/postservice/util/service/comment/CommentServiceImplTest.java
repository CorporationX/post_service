package faang.school.postservice.util.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CreateCommentDto;
import faang.school.postservice.dto.comment.ResponseCommentDto;
import faang.school.postservice.dto.comment.UpdateCommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.comment.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.CommentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceImplTest {

    private static final Long POST_ID = 1L;
    private static final Long COMMENT_ID = 20L;
    private static final Long AUTHOR_ID = 123L;
    private static final Long ANOTHER_POST_ID = 2L;
    private static final Long NON_EXISTENT_POST_ID = 999L;
    private static final Long NON_EXISTENT_COMMENT_ID = 777L;

    private static final String CONTENT = "Test comment content";
    private static final String UPDATED_CONTENT = "Updated comment content";
    private static final String EMPTY_CONTENT = "";
    private static final String BLANK_CONTENT = "   ";
    private static final String LONG_CONTENT = "а".repeat(4097);

    private final Post testPost = Post.builder().id(POST_ID).build();
    private final Comment testComment = Comment.builder()
            .id(COMMENT_ID)
            .content(CONTENT)
            .authorId(AUTHOR_ID)
            .post(testPost)
            .createdAt(LocalDateTime.now().minusHours(1))
            .updatedAt(LocalDateTime.now().minusHours(1))
            .build();

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Spy
    private final CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @InjectMocks
    private CommentServiceImpl commentService;

    @Test
    void getAllComments_WithExistingPostShouldReturnSortedComments() {
        Comment olderComment = createComment(2L, LocalDateTime.now().minusHours(3));
        Comment newerComment = createComment(3L, LocalDateTime.now().minusMinutes(30));
        List<Comment> comments = new ArrayList<>(List.of(testComment, olderComment, newerComment));

        when(commentRepository.findAllByPostId(POST_ID)).thenReturn(comments);

        List<ResponseCommentDto> result = commentService.getAllComments(POST_ID);

        assertNotNull(result);
        assertEquals(3, result.size());
        verify(commentRepository).findAllByPostId(POST_ID);
        verify(commentMapper, times(3)).toResponseDto(any(Comment.class));
    }

    @Test
    void createComment_WithValidDataShouldCreateComment() {
        CreateCommentDto createDto = new CreateCommentDto(AUTHOR_ID, CONTENT);

        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(testPost));
        when(userServiceClient.getUser(AUTHOR_ID)).thenReturn(new UserDto(AUTHOR_ID, "Test User", "test@example.com"));
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);

        ResponseCommentDto result = commentService.createComment(POST_ID, createDto);

        assertNotNull(result);
        verify(postRepository).findById(POST_ID);
        verify(userServiceClient).getUser(AUTHOR_ID);
        verify(commentMapper).toEntity(createDto);
        verify(commentRepository).save(any(Comment.class));
        verify(commentMapper).toResponseDto(testComment);
    }

    @Test
    void createComment_WithNonExistentPostShouldThrowException() {
        CreateCommentDto createDto = new CreateCommentDto(AUTHOR_ID, CONTENT);

        when(postRepository.findById(NON_EXISTENT_POST_ID)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> commentService.createComment(NON_EXISTENT_POST_ID, createDto));

        verify(postRepository).findById(NON_EXISTENT_POST_ID);
        verifyNoInteractions(userServiceClient, commentMapper, commentRepository);
    }

    @Test
    void createComment_WithEmptyContentShouldThrowException() {
        CreateCommentDto createDto = new CreateCommentDto(AUTHOR_ID, EMPTY_CONTENT);

        assertThrows(IllegalArgumentException.class,
                () -> commentService.createComment(POST_ID, createDto));

        verifyNoInteractions(postRepository, userServiceClient, commentMapper, commentRepository);
    }

    @Test
    void createComment_WithBlankContentShouldThrowException() {
        CreateCommentDto createDto = new CreateCommentDto(AUTHOR_ID, BLANK_CONTENT);

        assertThrows(IllegalArgumentException.class,
                () -> commentService.createComment(POST_ID, createDto));

        verifyNoInteractions(postRepository, userServiceClient, commentMapper, commentRepository);
    }

    @Test
    void createComment_WithTooLongContentShouldThrowException() {
        CreateCommentDto createDto = new CreateCommentDto(AUTHOR_ID, LONG_CONTENT);

        assertThrows(IllegalArgumentException.class,
                () -> commentService.createComment(POST_ID, createDto));

        verifyNoInteractions(postRepository, userServiceClient, commentMapper, commentRepository);
    }

    @Test
    void updateComment_WithValidDataShouldUpdateComment() {
        UpdateCommentDto updateDto = new UpdateCommentDto(UPDATED_CONTENT);

        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(testComment));
        when(commentRepository.save(testComment)).thenReturn(testComment);

        ResponseCommentDto result = commentService.updateComment(POST_ID, COMMENT_ID, updateDto);

        assertNotNull(result);
        verify(commentRepository).findById(COMMENT_ID);
        verify(commentMapper).updateEntity(updateDto, testComment);
        verify(commentRepository).save(testComment);
        verify(commentMapper).toResponseDto(testComment);
    }

    @Test
    void updateComment_WithNonExistentCommentShouldThrowException() {
        UpdateCommentDto updateDto = new UpdateCommentDto(UPDATED_CONTENT);

        when(commentRepository.findById(NON_EXISTENT_COMMENT_ID)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> commentService.updateComment(POST_ID, NON_EXISTENT_COMMENT_ID, updateDto));

        verify(commentRepository).findById(NON_EXISTENT_COMMENT_ID);
        verifyNoMoreInteractions(commentRepository, commentMapper);
    }

    @Test
    void updateComment_WithWrongPostIdShouldThrowException() {
        UpdateCommentDto updateDto = new UpdateCommentDto(UPDATED_CONTENT);
        Comment commentWithDifferentPost = createCommentWithDifferentPost();

        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(commentWithDifferentPost));

        assertThrows(IllegalArgumentException.class,
                () -> commentService.updateComment(POST_ID, COMMENT_ID, updateDto));

        verify(commentRepository).findById(COMMENT_ID);
        verifyNoMoreInteractions(commentRepository, commentMapper);
    }

    @Test
    void deleteComment_WithValidDataShouldDeleteComment() {
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(testComment));

        commentService.deleteComment(POST_ID, COMMENT_ID);

        verify(commentRepository).findById(COMMENT_ID);
        verify(commentRepository).deleteById(COMMENT_ID);
    }

    @Test
    void deleteComment_WithNonExistentCommentShouldThrowException() {
        when(commentRepository.findById(NON_EXISTENT_COMMENT_ID)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> commentService.deleteComment(POST_ID, NON_EXISTENT_COMMENT_ID));

        verify(commentRepository).findById(NON_EXISTENT_COMMENT_ID);
        verifyNoMoreInteractions(commentRepository);
    }

    @Test
    void deleteComment_WithWrongPostIdShouldThrowException() {
        Comment commentWithDifferentPost = createCommentWithDifferentPost();

        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(commentWithDifferentPost));

        assertThrows(IllegalArgumentException.class,
                () -> commentService.deleteComment(POST_ID, COMMENT_ID));

        verify(commentRepository).findById(COMMENT_ID);
        verifyNoMoreInteractions(commentRepository);
    }

    private Comment createComment(Long id, LocalDateTime createdAt) {
        return Comment.builder()
                .id(id)
                .content("Comment " + id)
                .authorId(AUTHOR_ID + id)
                .post(testPost)
                .createdAt(createdAt)
                .build();
    }

    private Comment createCommentWithDifferentPost() {
        Post differentPost = Post.builder().id(ANOTHER_POST_ID).build();
        return Comment.builder()
                .id(COMMENT_ID)
                .content(CONTENT)
                .authorId(AUTHOR_ID)
                .post(differentPost)
                .build();
    }
}

