package faang.school.postservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import faang.school.postservice.client.FeignClientValidator;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.event.LikeEventDto;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.ErrorResponse;
import faang.school.postservice.exception.LikeExistsException;
import faang.school.postservice.exception.LikeNotFoundException;
import faang.school.postservice.exception.UserNotFoundException;
import faang.school.postservice.mapper.LikeMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.impl.LikeEventProducer;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.util.Utils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class LikeServiceTest {
    private static final String COMMENT_CONTENT = "comment content";
    private static final String POST_CONTENT = "post content";
    private static final Long LIKE_ENTITY_ID = 100L;
    private static final Long USER_ID = 10L;
    private static final Long COMMENT_ID = 30L;
    private static final Long POST_ID = 50L;

    @Spy
    private Utils utils;
    @Spy
    private LikeMapperImpl mapper;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private FeignClientValidator feignClientValidator;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private PostService postService;
    @Mock
    private CommentService commentService;
    @Mock
    private LikeEventProducer likeEventProducer;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private LikeService likeService;

    @BeforeEach
    public void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        likeService = new LikeService(
            likeRepository, feignClientValidator, userServiceClient, postService, commentService,
            likeEventProducer, mapper, utils);
    }

    @Test
    public void testAddLikeCommentSuccess() {
        LikeDto likeDto = getLikeDto(COMMENT_ID, null);
        Comment comment = getComment();
        Like resultEntity = getLikeEntity(comment, null);

        doNothing().when(feignClientValidator).validateById(any(Runnable.class), anyString());
        when(commentService.findCommentById(COMMENT_ID)).thenReturn(comment);
        when(likeRepository.findByCommentIdAndUserId(COMMENT_ID, USER_ID)).thenReturn(Optional.empty());
        when(likeRepository.save(any(Like.class))).thenReturn(resultEntity);

        final LikeDto responseDto = likeService.addLikeToComment(likeDto);

        verify(feignClientValidator).validateById(any(Runnable.class), anyString());
        verify(likeRepository).save(any(Like.class));
        verify(likeEventProducer).publish(any(LikeEventDto.class));
        assertNotNull(responseDto);
        assertNotNull(responseDto.id());
        assertEquals(USER_ID, responseDto.userId());
        assertEquals(COMMENT_ID, responseDto.commentId());
    }

    @Test
    public void testAddLikeCommentAndLikeExists() {
        LikeDto requestDto = getLikeDto(COMMENT_ID, null);
        Comment comment = getComment();
        Like resultEntity = getLikeEntity(comment, null);

        doNothing().when(feignClientValidator).validateById(any(Runnable.class), anyString());
        when(commentService.findCommentById(COMMENT_ID)).thenReturn(comment);
        when(likeRepository.findByCommentIdAndUserId(COMMENT_ID, USER_ID))
            .thenReturn(Optional.ofNullable(resultEntity));

        LikeExistsException resultException = assertThrows(
            LikeExistsException.class, () -> likeService.addLikeToComment(requestDto));

        verify(likeRepository, times(0)).save(any(Like.class));
        verify(likeEventProducer, times(0)).publish(any(LikeEventDto.class));
        assertEquals(LikeService.USER_LIKED_THIS_COMMENT, resultException.getMessage());
    }

    @Test
    public void testDeleteLikeCommentSuccess() {
        LikeDto requestDto = getLikeDto(COMMENT_ID, null);
        Comment comment = getComment();
        Like resultEntity = getLikeEntity(comment, null);

        doNothing().when(feignClientValidator).validateById(any(Runnable.class), anyString());
        when(likeRepository.deleteByCommentIdAndUserId(COMMENT_ID, USER_ID))
            .thenReturn(Optional.ofNullable(resultEntity));

        likeService.deleteLikeFromComment(requestDto);

        verify(likeRepository).deleteByCommentIdAndUserId(any(Long.class), any(Long.class));
    }

    @Test
    public void testDeleteLikeCommentButLikeIsMissing() {
        LikeDto requestDto = getLikeDto(COMMENT_ID, null);

        doNothing().when(feignClientValidator).validateById(any(Runnable.class), anyString());
        when(likeRepository.deleteByCommentIdAndUserId(COMMENT_ID, USER_ID))
            .thenReturn(Optional.empty());

        LikeNotFoundException resultException = assertThrows(LikeNotFoundException.class,
            () -> likeService.deleteLikeFromComment(requestDto));

        String expectedError = utils.format(LikeService.COMMENT_LIKE_NOT_FOUND, USER_ID, COMMENT_ID);
        assertEquals(expectedError, resultException.getMessage());
    }

    @Test
    public void testAddLikePostSuccess() {
        LikeDto requestDto = getLikeDto(null, POST_ID);
        Post post = getPost();
        Like resultEntity = getLikeEntity(null, post);

        doNothing().when(feignClientValidator).validateById(any(Runnable.class), anyString());
        when(postService.findPostById(POST_ID)).thenReturn(post);
        when(likeRepository.findByPostIdAndUserId(POST_ID, USER_ID)).thenReturn(Optional.empty());
        when(likeRepository.save(any(Like.class))).thenReturn(resultEntity);

        LikeDto responseDto = likeService.addLikeToPost(requestDto);

        verify(likeRepository).save(any(Like.class));
        verify(likeEventProducer).publish(any(LikeEventDto.class));
        assertNotNull(responseDto);
        assertNotNull(responseDto.id());
        assertEquals(USER_ID, responseDto.userId());
        assertEquals(POST_ID, responseDto.postId());
    }

    @Test
    public void testAddLikePostAndLikeExists() {
        LikeDto requestDto = getLikeDto(null, POST_ID);
        Post post = getPost();
        Like resultEntity = getLikeEntity(null, post);

        doNothing().when(feignClientValidator).validateById(any(Runnable.class), anyString());
        when(postService.findPostById(POST_ID)).thenReturn(post);
        when(likeRepository.findByPostIdAndUserId(POST_ID, USER_ID))
            .thenReturn(Optional.ofNullable(resultEntity));

        LikeExistsException resultException = assertThrows(
            LikeExistsException.class, () -> likeService.addLikeToPost(requestDto));

        verify(likeRepository, times(0)).save(any(Like.class));
        verify(likeEventProducer, times(0)).publish(any(LikeEventDto.class));
        assertEquals(LikeService.USER_LIKED_THIS_POST, resultException.getMessage());
    }

    @Test
    public void testDeleteLikePostSuccess() {
        LikeDto requestDto = getLikeDto(null, POST_ID);
        Post post = getPost();
        Like resultEntity = getLikeEntity(null, post);

        doNothing().when(feignClientValidator).validateById(any(Runnable.class), anyString());
        when(likeRepository.deleteByPostIdAndUserId(POST_ID, USER_ID))
            .thenReturn(Optional.ofNullable(resultEntity));

        likeService.deleteLikeFromPost(requestDto);

        verify(likeRepository).deleteByPostIdAndUserId(any(Long.class), any(Long.class));
    }

    @Test
    public void testDeleteLikePostButLikeIsMissing() {
        LikeDto requestDto = getLikeDto(null, POST_ID);

        doNothing().when(feignClientValidator).validateById(any(Runnable.class), anyString());
        when(likeRepository.deleteByPostIdAndUserId(POST_ID, USER_ID))
            .thenReturn(Optional.empty());

        LikeNotFoundException resultException = assertThrows(LikeNotFoundException.class,
            () -> likeService.deleteLikeFromPost(requestDto));

        String expectedError = utils.format(LikeService.POST_LIKE_NOT_FOUND, USER_ID, POST_ID);
        assertEquals(expectedError, resultException.getMessage());
    }

    @Test
    public void testDeleteLikePostButUserIsMissing() {
        LikeDto requestDto = getLikeDto(null, POST_ID);
        ErrorResponse errorResponse = new ErrorResponse(utils.format(LikeService.USER_NOT_FOUND, USER_ID));
        UserNotFoundException mockException = new UserNotFoundException(errorResponse.getErrorMessage());

        doThrow(mockException).when(feignClientValidator).validateById(any(Runnable.class), anyString());

        UserNotFoundException resultException = assertThrows(UserNotFoundException.class,
            () -> likeService.deleteLikeFromPost(requestDto));

        verify(likeRepository, times(0))
            .deleteByPostIdAndUserId(any(Long.class), any(Long.class));
        String expectedError = utils.format(LikeService.USER_NOT_FOUND, USER_ID);
        assertEquals(expectedError, resultException.getMessage());
    }

    @Test
    public void testWhenExceptionMessageIsEmpty() {
        LikeDto requestDto = getLikeDto(null, POST_ID);
        UserNotFoundException mockException = new UserNotFoundException(
            utils.format(LikeService.USER_NOT_FOUND, USER_ID));

        doThrow(mockException).when(feignClientValidator).validateById(any(Runnable.class), anyString());

        UserNotFoundException resultException = assertThrows(UserNotFoundException.class,
            () -> likeService.deleteLikeFromPost(requestDto));

        verify(likeRepository, times(0))
            .deleteByPostIdAndUserId(any(Long.class), any(Long.class));
        String expectedError = utils.format(LikeService.USER_NOT_FOUND, USER_ID);
        assertEquals(expectedError, resultException.getMessage());
    }

    private Comment getComment() {
        return Comment.builder()
            .id(COMMENT_ID)
            .content(COMMENT_CONTENT)
            .build();
    }

    private Post getPost() {
        return Post.builder()
            .id(POST_ID)
            .content(POST_CONTENT)
            .build();
    }

    private LikeDto getLikeDto(Long commentId, Long postId) {
        return LikeDto.builder()
            .userId(USER_ID)
            .postId(postId)
            .commentId(commentId)
            .build();
    }

    private Like getLikeEntity(Comment comment, Post post) {
        return Like.builder()
            .id(LIKE_ENTITY_ID)
            .userId(USER_ID)
            .comment(comment)
            .post(post)
            .build();
    }
}