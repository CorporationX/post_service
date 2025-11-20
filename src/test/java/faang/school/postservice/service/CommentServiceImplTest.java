package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.CreateCommentDto;
import faang.school.postservice.dto.comment.UpdateCommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.comment.CommentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class CommentServiceImplTest {

    @Mock
    private UserContext userContext;

    @Mock
    private PostRepository postRepository;

    @Spy
    private CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    private static final Long TEST_COMMENT_ID = 1L;
    private static final Long TEST_POST_ID = 10L;
    private static final Long INVALID_POST_ID = 11L;
    private static final Long TEST_AUTHOR_ID = 5L;
    private static final Long INVALID_AUTHOR_ID = 6L;
    private static final String TEST_COMMENT_CONTENT = "Test comment text";
    private static final LocalDateTime TEST_COMMENT_CREATED_AT = LocalDateTime.now();
    private static final LocalDateTime TEST_COMMENT_UPDATED_AT = LocalDateTime.now().plusHours(2);
    private static final Post post = new Post();
    private static final Comment comment = new Comment();
    private static final UpdateCommentDto updateCommentDto = new UpdateCommentDto(TEST_COMMENT_ID,
            TEST_AUTHOR_ID, TEST_POST_ID, TEST_COMMENT_CONTENT, TEST_COMMENT_UPDATED_AT);

    @BeforeEach
    void setUp() {
        post.setId(TEST_POST_ID);
        comment.setContent(TEST_COMMENT_CONTENT);
        comment.setAuthorId(TEST_AUTHOR_ID);
        comment.setCreatedAt(TEST_COMMENT_CREATED_AT);
        comment.setPost(post);
    }

    @Test
    void testAddCommentSuccess() {
        CreateCommentDto createCommentDto = new CreateCommentDto(TEST_AUTHOR_ID,
                TEST_POST_ID, TEST_COMMENT_CONTENT, TEST_COMMENT_CREATED_AT);
        CommentDto expectedCommentDto = new CommentDto(null, TEST_AUTHOR_ID,
                TEST_POST_ID, TEST_COMMENT_CONTENT, TEST_COMMENT_CREATED_AT, null);

        UserDto testUserDto = new UserDto(TEST_AUTHOR_ID, "Test user", "test@email.com");

        Mockito.when(postRepository.findById(TEST_POST_ID)).thenReturn(Optional.of(post));
        Mockito.when(userContext.getUserId()).thenReturn(TEST_AUTHOR_ID);
        Mockito.when(userServiceClient.getUser(TEST_AUTHOR_ID)).thenReturn(testUserDto);
        Mockito.when(commentRepository.save(Mockito.any(Comment.class))).thenReturn(comment);

        CommentDto result = commentService.addComment(TEST_POST_ID, createCommentDto);

        Mockito.verify(postRepository).findById(TEST_POST_ID);
        assertEquals(expectedCommentDto, result);
    }

    @Test
    void testUpdateCommentSuccess() {
        CommentDto expectedCommentDto = new CommentDto(TEST_COMMENT_ID,
                TEST_AUTHOR_ID, TEST_POST_ID, TEST_COMMENT_CONTENT, TEST_COMMENT_CREATED_AT, TEST_COMMENT_UPDATED_AT);

        comment.setUpdatedAt(TEST_COMMENT_UPDATED_AT);

        Mockito.when(commentRepository.findById(TEST_COMMENT_ID)).thenReturn(Optional.of(comment));
        Mockito.when(postRepository.findById(TEST_POST_ID)).thenReturn(Optional.of(post));
        Mockito.when(commentRepository.save(Mockito.any(Comment.class))).thenReturn(comment);

        CommentDto result = commentService.updateComment(TEST_AUTHOR_ID, updateCommentDto);

        Mockito.verify(postRepository).findById(TEST_POST_ID);
        assertEquals(expectedCommentDto, result);
    }

    @Test
    void testUpdateCommentInvalidUserId() {
        comment.setUpdatedAt(TEST_COMMENT_UPDATED_AT);

        Mockito.when(commentRepository.findById(TEST_COMMENT_ID)).thenReturn(Optional.of(comment));
        assertThrows(IllegalArgumentException.class, () -> commentService
                        .updateComment(INVALID_AUTHOR_ID, updateCommentDto),
                "Comment update is not allowed for current user!");
    }

    @Test
    void testUpdateCommentInvalidPostId() {
        UpdateCommentDto invalidPostIdDto = new UpdateCommentDto(TEST_COMMENT_ID
                , TEST_AUTHOR_ID, INVALID_POST_ID, TEST_COMMENT_CONTENT, TEST_COMMENT_UPDATED_AT);

        comment.setUpdatedAt(TEST_COMMENT_UPDATED_AT);

        Mockito.when(commentRepository.findById(TEST_COMMENT_ID)).thenReturn(Optional.of(comment));
        assertThrows(IllegalArgumentException.class, () -> commentService
                        .updateComment(TEST_AUTHOR_ID, invalidPostIdDto),
                "Post IDs of original and updated comments do not match!");
    }

    @Test
    void testGetCommentsByPostIdSuccess() {
        Comment comment1 = new Comment();
        comment1.setCreatedAt(LocalDateTime.now());
        Comment comment2 = new Comment();
        comment2.setCreatedAt(LocalDateTime.now().plusMinutes(5));
        Comment comment3 = new Comment();
        comment3.setCreatedAt(LocalDateTime.now().plusMinutes(10));

        List<Comment> commentList = List.of(comment1, comment2, comment3);
        List<CommentDto> expectedResult = commentList.stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt).reversed())
                .map(commentMapper::toCommentDto).toList();

        Mockito.when(commentRepository.findAllByPostId(TEST_POST_ID)).thenReturn(commentList);
        List<CommentDto> result = commentService.getCommentsByPostId(TEST_POST_ID);
        assertEquals(expectedResult, result);
    }

    @Test
    void testGetCommentsByPostIdNoCommentsExist() {
        Mockito.when(commentRepository.findAllByPostId(TEST_POST_ID)).thenReturn(Collections.emptyList());
        assertThrows(IllegalArgumentException.class, () -> commentService.getCommentsByPostId(TEST_POST_ID),
                "There are no comment under post with this ID!");
    }

    @Test
    void testDeleteCommentSuccess() {
        Mockito.when(commentRepository.findById(TEST_COMMENT_ID)).thenReturn(Optional.of(comment));

        commentService.deleteComment(TEST_COMMENT_ID);

        Mockito.verify(commentRepository).deleteById(TEST_COMMENT_ID);
    }

    @Test
    void testDeleteCommentIdDoesNotExist() {
        Mockito.when(commentRepository.findById(TEST_COMMENT_ID)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> commentService.deleteComment(TEST_COMMENT_ID),
                "Comment with this ID does not exist!");
    }
}