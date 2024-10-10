package faang.school.postservice.service.comment;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.SortingBy;
import faang.school.postservice.dto.comment.SortingOrder;
import faang.school.postservice.dto.comment.SortingStrategyDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.comment.sort.SortByUpdateAscending;
import faang.school.postservice.service.comment.sort.SortByUpdateDescending;
import faang.school.postservice.service.comment.sort.SortingStrategyAppliersMap;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static faang.school.postservice.exception.comment.ExceptionMessages.COMMENT_NOT_FOUND;
import static faang.school.postservice.exception.comment.ExceptionMessages.POST_DELETED_OR_NOT_PUBLISHED;
import static faang.school.postservice.exception.comment.ExceptionMessages.POST_NOT_FOUND;
import static faang.school.postservice.exception.comment.ExceptionMessages.WRONG_AUTHOR_ID;
import static faang.school.postservice.exception.comment.ExceptionMessages.WRONG_POST_ID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {
    private static final Long COMMENT_ID = 1L;
    private static final Long AUTHOR_ID = 1L;
    private static final Long POST_ID = 1L;
    private static final LocalDateTime INITIAL_TIME =
            LocalDateTime.of(2021, 1, 1, 1, 1);

    private Post post;
    private UserDto author;

    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private UserContext userContext;

    @Spy
    private CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);

    @Mock
    private CommentChecker commentChecker;

    @Mock
    private RedisMessagePublisher redisMessagePublisher;

    private SortingStrategyAppliersMap sortingStrategyAppliersMap;

    @BeforeEach
    void setUp() {
        sortingStrategyAppliersMap = new SortingStrategyAppliersMap(
                List.of(new SortByUpdateAscending(), new SortByUpdateDescending()));
        commentService = new CommentServiceImpl(
                commentRepository,
                postRepository,
                userServiceClient,
                userContext,
                commentMapper,
                sortingStrategyAppliersMap,
                commentChecker,
                redisMessagePublisher);
        post = initPost(POST_ID, true, false);
        author = initAuthor(AUTHOR_ID);
    }

    @Test
    @DisplayName("Creating comment")
    void commentServiceTest_CreatingComment() {
        CommentDto commentCreationDto = initCommentDto(null, AUTHOR_ID, "test", INITIAL_TIME);
        Comment createdComment = initComment(COMMENT_ID, AUTHOR_ID, post, "test", INITIAL_TIME);
        CommentDto expectedDto = initCommentDto(COMMENT_ID, AUTHOR_ID, "test", INITIAL_TIME);
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));
        when(userServiceClient.getUser(AUTHOR_ID)).thenReturn(author);
        when(userContext.getUserId()).thenReturn(AUTHOR_ID);
        when(commentRepository.save(any(Comment.class))).thenReturn(createdComment);

        CommentDto result = commentService.createComment(POST_ID, commentCreationDto);

        verify(postRepository).findById(POST_ID);
        verify(userServiceClient).getUser(AUTHOR_ID);
        verify(userContext).getUserId();
        verify(commentRepository).save(any(Comment.class));
        assertEquals(expectedDto.authorId(), result.authorId());
        assertEquals(expectedDto.content(), result.content());
        assertEquals(expectedDto.updatedAt(), result.updatedAt());
    }

    @Test
    @DisplayName("Creating comment to non existing post")
    void commentServiceTest_CreatingCommentToNonExistingPost() {
        CommentDto commentCreationDto = initCommentDto(null, AUTHOR_ID, "test", INITIAL_TIME);
        when(postRepository.findById(POST_ID)).thenReturn(Optional.empty());
        String expectedMessage = POST_NOT_FOUND.getMessage().formatted(POST_ID);

        var ex = assertThrows(EntityNotFoundException.class,
                () -> commentService.createComment(POST_ID, commentCreationDto));
        assertEquals(expectedMessage, ex.getMessage());
        verify(postRepository).findById(POST_ID);
    }

    @Test
    @DisplayName("Creating comment to deleted post")
    void commentServiceTest_CreatingCommentToDeletedPost() {
        CommentDto commentCreationDto = initCommentDto(null, AUTHOR_ID, "test", INITIAL_TIME);
        post.setDeleted(true);
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));
        String expectedMessage = POST_DELETED_OR_NOT_PUBLISHED.getMessage().formatted(POST_ID);

        var ex = assertThrows(DataValidationException.class,
                () -> commentService.createComment(POST_ID, commentCreationDto));
        assertEquals(expectedMessage, ex.getMessage());
        verify(postRepository).findById(POST_ID);
    }

    @Test
    @DisplayName("Creating comment to not published post")
    void commentServiceTest_CreatingCommentToNotPublishedPost() {
        CommentDto commentCreationDto = initCommentDto(null, AUTHOR_ID, "test", INITIAL_TIME);
        post.setPublished(false);
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));
        String expectedMessage = POST_DELETED_OR_NOT_PUBLISHED.getMessage().formatted(POST_ID);

        var ex = assertThrows(DataValidationException.class,
                () -> commentService.createComment(POST_ID, commentCreationDto));
        assertEquals(expectedMessage, ex.getMessage());
        verify(postRepository).findById(POST_ID);
    }

    @Test
    @DisplayName("Updating comment")
    void commentServiceTest_UpdatingComment() {
        CommentDto commentUpdateDto = initCommentDto(null, null, "new content", null);
        Comment commentToUpdate = initComment(COMMENT_ID, AUTHOR_ID, post, "test", INITIAL_TIME);
        CommentDto expectedDto = initCommentDto(COMMENT_ID, AUTHOR_ID, "new content", INITIAL_TIME);
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(commentToUpdate));
        when(userServiceClient.getUser(AUTHOR_ID)).thenReturn(author);
        when(userContext.getUserId()).thenReturn(AUTHOR_ID);

        CommentDto result = commentService.updateComment(POST_ID, COMMENT_ID, commentUpdateDto);

        verify(commentRepository).findById(COMMENT_ID);
        verify(userServiceClient).getUser(AUTHOR_ID);
        verify(userContext).getUserId();
        verify(commentRepository).save(commentToUpdate);
        assertEquals(expectedDto, result);
    }

    @Test
    @DisplayName("Updating non existing comment")
    void commentServiceTest_UpdatingNonExistingComment() {
        CommentDto commentUpdateDto = initCommentDto(null, null, "new content", null);
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.empty());
        String expectedMessage = COMMENT_NOT_FOUND.getMessage().formatted(COMMENT_ID);

        var ex = assertThrows(EntityNotFoundException.class,
                () -> commentService.updateComment(POST_ID, COMMENT_ID, commentUpdateDto));
        assertEquals(expectedMessage, ex.getMessage());
        verify(commentRepository).findById(COMMENT_ID);
    }

    @Test
    @DisplayName("Updating comment not by author")
    void commentServiceTest_UpdatingCommentNotByAuthor() {
        CommentDto commentUpdateDto = initCommentDto(null, null, "new content", null);
        Comment commentToUpdate = initComment(COMMENT_ID, AUTHOR_ID, post, "test", INITIAL_TIME);
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(commentToUpdate));
        when(userServiceClient.getUser(AUTHOR_ID)).thenReturn(author);
        when(userContext.getUserId()).thenReturn(2L);
        String expectedMessage = WRONG_AUTHOR_ID.getMessage().formatted(2L, AUTHOR_ID);

        var ex = assertThrows(DataValidationException.class,
                () -> commentService.updateComment(POST_ID, COMMENT_ID, commentUpdateDto));
        assertEquals(expectedMessage, ex.getMessage());
        verify(commentRepository).findById(COMMENT_ID);
        verify(userServiceClient).getUser(AUTHOR_ID);
        verify(userContext).getUserId();
    }

    @Test
    @DisplayName("Updating comment from wrong post")
    void commentServiceTest_UpdatingCommentFromWrongPost() {
        CommentDto commentUpdateDto = initCommentDto(null, null, "new content", null);
        Comment commentToUpdate = initComment(COMMENT_ID, AUTHOR_ID, post, "test", INITIAL_TIME);
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(commentToUpdate));
        when(userServiceClient.getUser(AUTHOR_ID)).thenReturn(author);
        when(userContext.getUserId()).thenReturn(AUTHOR_ID);
        String expectedMessage = WRONG_POST_ID.getMessage().formatted(2L, POST_ID);

        var ex = assertThrows(DataValidationException.class,
                () -> commentService.updateComment(2L, COMMENT_ID, commentUpdateDto));
        assertEquals(expectedMessage, ex.getMessage());
        verify(commentRepository).findById(COMMENT_ID);
        verify(userServiceClient).getUser(AUTHOR_ID);
        verify(userContext).getUserId();
    }

    @Test
    @DisplayName("Updating comment to deleted post")
    void commentServiceTest_UpdatingCommentToDeletedPost() {
        CommentDto commentUpdateDto = initCommentDto(null, null, "new content", null);
        Comment commentToUpdate = initComment(COMMENT_ID, AUTHOR_ID, post, "test", INITIAL_TIME);
        post.setDeleted(true);
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(commentToUpdate));
        when(userServiceClient.getUser(AUTHOR_ID)).thenReturn(author);
        when(userContext.getUserId()).thenReturn(AUTHOR_ID);
        String expectedMessage = POST_DELETED_OR_NOT_PUBLISHED.getMessage().formatted(POST_ID);

        var ex = assertThrows(DataValidationException.class,
                () -> commentService.updateComment(POST_ID, COMMENT_ID, commentUpdateDto));
        assertEquals(expectedMessage, ex.getMessage());
        verify(commentRepository).findById(COMMENT_ID);
        verify(userServiceClient).getUser(AUTHOR_ID);
        verify(userContext).getUserId();
    }

    @Test
    @DisplayName("Getting sorted comments")
    void commentServiceTest_GettingSortedComments() {
        SortingStrategyDto sortingStrategyDto = initSortingStrategyDto(SortingBy.UPDATED_AT, SortingOrder.ASC);
        Comment comment1 = initComment(1L, AUTHOR_ID, post, "test1", INITIAL_TIME);
        Comment comment2 = initComment(2L, 2L, post, "test2", LocalDateTime.of(2021, 1, 1, 1, 2));
        List<CommentDto> expectedDtos = List.of(
                initCommentDto(COMMENT_ID, AUTHOR_ID, "test1", INITIAL_TIME),
                initCommentDto(2L, 2L, "test2", LocalDateTime.of(2021, 1, 1, 1, 2)));
        post.setComments(List.of(comment1, comment2));
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));

        List<CommentDto> result = commentService.getComments(POST_ID, sortingStrategyDto);
        System.out.println(result);

        verify(postRepository).findById(POST_ID);
        assertEquals(2, result.size());
        assertTrue(result.containsAll(expectedDtos));
    }

    @Test
    @DisplayName("Getting empty list of comments from post")
    void commentServiceTest_GettingEmptyListOfCommentsFromPost() {
        SortingStrategyDto sortingStrategyDto = initSortingStrategyDto(SortingBy.UPDATED_AT, SortingOrder.DESC);
        post.setComments(List.of());
        when(postRepository.findById(POST_ID)).thenReturn(Optional.of(post));

        List<CommentDto> result = commentService.getComments(POST_ID, sortingStrategyDto);

        verify(postRepository).findById(POST_ID);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Getting comments from non existing post")
    void commentServiceTest_GettingCommentsFromNonExistingPost() {
        SortingStrategyDto sortingStrategyDto = initSortingStrategyDto(SortingBy.UPDATED_AT, SortingOrder.DESC);
        when(postRepository.findById(POST_ID)).thenReturn(Optional.empty());
        String expectedMessage = POST_NOT_FOUND.getMessage().formatted(POST_ID);

        var ex = assertThrows(EntityNotFoundException.class,
                () -> commentService.getComments(POST_ID, sortingStrategyDto));
        assertEquals(expectedMessage, ex.getMessage());
        verify(postRepository).findById(POST_ID);
    }

    @Test
    @DisplayName("Delete comment")
    void commentServiceTest_DeleteComment() {
        Comment commentToDelete = initComment(COMMENT_ID, AUTHOR_ID, post, "test", INITIAL_TIME);
        CommentDto expectedDto = initCommentDto(COMMENT_ID, AUTHOR_ID, "test", INITIAL_TIME);
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(commentToDelete));
        when(userServiceClient.getUser(AUTHOR_ID)).thenReturn(author);
        when(userContext.getUserId()).thenReturn(AUTHOR_ID);

        CommentDto result = commentService.deleteComment(POST_ID, COMMENT_ID);

        verify(commentRepository).findById(COMMENT_ID);
        verify(userServiceClient).getUser(AUTHOR_ID);
        verify(userContext).getUserId();
        verify(commentRepository).delete(commentToDelete);
        assertEquals(expectedDto, result);
    }

    @Test
    @DisplayName("Delete non existing comment")
    void commentServiceTest_DeleteNonExistingComment() {
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.empty());
        String expectedMessage = COMMENT_NOT_FOUND.getMessage().formatted(COMMENT_ID);

        var ex = assertThrows(EntityNotFoundException.class,
                () -> commentService.deleteComment(POST_ID, COMMENT_ID));
        assertEquals(expectedMessage, ex.getMessage());
        verify(commentRepository).findById(COMMENT_ID);
    }

    @Test
    @DisplayName("Delete comment of deleted post")
    void commentServiceTest_DeleteCommentOfDeletedPost() {
        Comment commentToDelete = initComment(COMMENT_ID, AUTHOR_ID, post, "test", INITIAL_TIME);
        post.setDeleted(true);
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(commentToDelete));
        when(userServiceClient.getUser(AUTHOR_ID)).thenReturn(author);
        when(userContext.getUserId()).thenReturn(AUTHOR_ID);
        String expectedMessage = POST_DELETED_OR_NOT_PUBLISHED.getMessage().formatted(POST_ID);

        var ex = assertThrows(DataValidationException.class,
                () -> commentService.deleteComment(POST_ID, COMMENT_ID));
        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    @DisplayName("Delete comment not by author")
    void commentServiceTest_DeleteCommentNotByAuthor() {
        Comment commentToDelete = initComment(COMMENT_ID, AUTHOR_ID, post, "test", INITIAL_TIME);
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(commentToDelete));
        when(userServiceClient.getUser(AUTHOR_ID)).thenReturn(author);
        when(userContext.getUserId()).thenReturn(2L);
        String expectedMessage = WRONG_AUTHOR_ID.getMessage().formatted(2L, AUTHOR_ID);

        var ex = assertThrows(DataValidationException.class,
                () -> commentService.deleteComment(POST_ID, COMMENT_ID));
        assertEquals(expectedMessage, ex.getMessage());
        verify(commentRepository).findById(COMMENT_ID);
    }

    @Test
    @DisplayName("Delete comment from wrong post")
    void commentServiceTest_DeleteCommentFromWrongPost() {
        Comment commentToDelete = initComment(COMMENT_ID, AUTHOR_ID, post, "test", INITIAL_TIME);
        when(commentRepository.findById(COMMENT_ID)).thenReturn(Optional.of(commentToDelete));
        when(userServiceClient.getUser(AUTHOR_ID)).thenReturn(author);
        when(userContext.getUserId()).thenReturn(AUTHOR_ID);
        String expectedMessage = WRONG_POST_ID.getMessage().formatted(2L, POST_ID);

        var ex = assertThrows(DataValidationException.class,
                () -> commentService.deleteComment(2L, COMMENT_ID));
        assertEquals(expectedMessage, ex.getMessage());
        verify(commentRepository).findById(COMMENT_ID);
    }

    @Test
    @DisplayName("Success returning unverified comments")
    public void testGettingUnverifiedComments() {
        List<Comment> comments = List.of(initComment(COMMENT_ID, AUTHOR_ID, post, "test", INITIAL_TIME),
                initComment(2L, AUTHOR_ID, post, "test", INITIAL_TIME),
                initComment(3L, AUTHOR_ID, post, "test", INITIAL_TIME));
        when(commentRepository.findUnverifiedComments(any())).thenReturn(comments);

        List<Comment> unverifiedComments = commentService.getUnverifiedComments();

        assertEquals(3, unverifiedComments.size());
        assertEquals(3L, unverifiedComments.get(2).getId());
    }

    @Test
    @DisplayName("Success saving verified comments")
    public void testSavingVerifiedComments() {
        List<Comment> comments = List.of(initComment(COMMENT_ID, AUTHOR_ID, post, "test", INITIAL_TIME),
                initComment(2L, AUTHOR_ID, post, "test", INITIAL_TIME),
                initComment(3L, AUTHOR_ID, post, "test", INITIAL_TIME));

        commentService.verifyComments(comments);

        verify(commentRepository).saveAll(comments);
    }

    @Test
    @DisplayName("Getting users to ban")
    public void testGettingUsersToBan() {
        commentService.banUsersWithObsceneCommentsMoreThan(anyInt());

        verify(commentRepository).findUserIdsToBan(anyInt());
    }

    CommentDto initCommentDto(Long id, Long authorId, String content, LocalDateTime updateAt) {
        return CommentDto.builder()
                .id(id)
                .authorId(authorId)
                .content(content)
                .updatedAt(updateAt)
                .build();
    }

    Comment initComment(Long id, Long authorId, Post post, String content, LocalDateTime updateAt) {
        return Comment.builder()
                .id(id)
                .authorId(authorId)
                .post(post)
                .content(content)
                .updatedAt(updateAt)
                .build();
    }

    Post initPost(Long id, boolean isPublished, boolean isDeleted) {
        return Post.builder()
                .id(id)
                .published(isPublished)
                .deleted(isDeleted)
                .build();
    }

    UserDto initAuthor(Long id) {
        return new UserDto(id, null, null);
    }

    SortingStrategyDto initSortingStrategyDto(SortingBy field, SortingOrder order) {
        return new SortingStrategyDto(field, order);
    }
}
