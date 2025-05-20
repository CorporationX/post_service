package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.like.LikeCommentRequestDto;
import faang.school.postservice.dto.like.LikeCommentResponseDto;
import faang.school.postservice.dto.like.LikePostRequestDto;
import faang.school.postservice.dto.like.LikePostResponseDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.LikeExistsException;
import faang.school.postservice.exception.LikeNotFoundException;
import faang.school.postservice.exception.UserNotFoundException;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.mapper.LikeMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.util.Utils;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class LikeServiceTest {
    private static final String MOCK_NAME = "Mock User Name";
    private static final String MOCK_EMAIL = "email@mail.ru";
    private static final String COMMENT_CONTENT = "comment content";
    private static final String POST_CONTENT = "post content";
    private static final Long LIKE_ENTITY_ID = 100L;
    private static final Long USER_ID = 10L;
    private static final Long COMMENT_ID = 30L;
    private static final Long POST_ID = 50L;


    private final Utils utils = new Utils();
    private final LikeMapper mapper = new LikeMapperImpl();

    @Mock
    private LikeRepository likeRepository;
    @Mock
    private UserServiceClient userService;
    @Mock
    private UserContext userContext;
    @Mock
    private PostService postService;
    @Mock
    private CommentService commentService;
    private LikeService likeService;

    @BeforeEach
    public void setUp() {
        likeService = new LikeService(
                likeRepository, userService, userContext, postService, commentService, mapper, utils
        );
    }

    @Test
    public void testAddLikeCommentSuccess() {
        LikeCommentRequestDto requestDto = getLikeCommentRequestDto();
        UserDto userDto = getUserDto();
        Comment comment = getComment();
        Like resultEntity = getLikeEntity(comment, null);

        when(userService.getUser(USER_ID)).thenReturn(userDto);
        when(commentService.findCommentById(COMMENT_ID)).thenReturn(comment);
        when(likeRepository.findByCommentIdAndUserId(COMMENT_ID, USER_ID)).thenReturn(Optional.empty());
        when(likeRepository.save(any(Like.class))).thenReturn(resultEntity);

        LikeCommentResponseDto responseDto = likeService.addComment(requestDto);

        verify(likeRepository, times(1)).save(any(Like.class));
        assertNotNull(responseDto);
        assertNotNull(responseDto.id());
        assertEquals(USER_ID, responseDto.userId());
        assertEquals(COMMENT_ID, responseDto.commentId());
    }

    @Test
    public void testAddLikeCommentAndLikeExists() {
        LikeCommentRequestDto requestDto = getLikeCommentRequestDto();
        UserDto userDto = getUserDto();
        Comment comment = getComment();
        Like resultEntity = getLikeEntity(comment, null);

        when(userService.getUser(USER_ID)).thenReturn(userDto);
        when(commentService.findCommentById(COMMENT_ID)).thenReturn(comment);
        when(likeRepository.findByCommentIdAndUserId(COMMENT_ID, USER_ID))
                .thenReturn(Optional.ofNullable(resultEntity));

        LikeExistsException resultException = assertThrows(
                LikeExistsException.class, () -> likeService.addComment(requestDto));

        verify(likeRepository, times(0)).save(any(Like.class));
        assertEquals(LikeService.USER_LIKED_THIS_COMMENT, resultException.getMessage());
    }

    @Test
    public void testDeleteLikeCommentSuccess() {
        LikeCommentRequestDto requestDto = getLikeCommentRequestDto();
        UserDto userDto = getUserDto();
        Comment comment = getComment();
        Like resultEntity = getLikeEntity(comment, null);

        when(userService.getUser(USER_ID)).thenReturn(userDto);
        when(likeRepository.deleteByCommentIdAndUserId(COMMENT_ID, USER_ID))
                .thenReturn(Optional.ofNullable(resultEntity));

        LikeCommentResponseDto responseDto = likeService.deleteComment(requestDto);

        verify(likeRepository, times(1))
                .deleteByCommentIdAndUserId(any(Long.class), any(Long.class));
        assertNotNull(responseDto);
        assertEquals(LIKE_ENTITY_ID, responseDto.id());
        assertEquals(USER_ID, responseDto.userId());
        assertEquals(COMMENT_ID, responseDto.commentId());
    }

    @Test
    public void testDeleteLikeCommentButLikeIsMissing() {
        final String expectedError = utils.format(LikeService.COMMENT_LIKE_NOT_FOUND, USER_ID, COMMENT_ID);

        LikeCommentRequestDto requestDto = getLikeCommentRequestDto();
        UserDto userDto = getUserDto();

        when(userService.getUser(USER_ID)).thenReturn(userDto);
        when(likeRepository.deleteByCommentIdAndUserId(COMMENT_ID, USER_ID))
                .thenReturn(Optional.empty());

        LikeNotFoundException resultException = assertThrows(LikeNotFoundException.class,
                () -> likeService.deleteComment(requestDto));

        assertEquals(expectedError, resultException.getMessage());
    }

    @Test
    public void testAddLikePostSuccess() {
        LikePostRequestDto requestDto = getLikePostRequestDto();
        UserDto userDto = getUserDto();
        Post post = getPost();
        Like resultEntity = getLikeEntity(null, post);
        when(userService.getUser(USER_ID)).thenReturn(userDto);
        when(postService.findPostById(POST_ID)).thenReturn(post);
        when(likeRepository.findByPostIdAndUserId(POST_ID, USER_ID)).thenReturn(Optional.empty());
        when(likeRepository.save(any(Like.class))).thenReturn(resultEntity);

        LikePostResponseDto responseDto = likeService.addPost(requestDto);

        verify(likeRepository, times(1)).save(any(Like.class));
        assertNotNull(responseDto);
        assertNotNull(responseDto.id());
        assertEquals(USER_ID, responseDto.userId());
        assertEquals(POST_ID, responseDto.postId());
    }

    @Test
    public void testAddLikePostAndLikeExists() {
        LikePostRequestDto requestDto = getLikePostRequestDto();
        UserDto userDto = getUserDto();
        Post post = getPost();
        Like resultEntity = getLikeEntity(null, post);
        when(userService.getUser(USER_ID)).thenReturn(userDto);
        when(postService.findPostById(POST_ID)).thenReturn(post);
        when(likeRepository.findByPostIdAndUserId(POST_ID, USER_ID))
                .thenReturn(Optional.ofNullable(resultEntity));

        LikeExistsException resultException = assertThrows(
                LikeExistsException.class, () -> likeService.addPost(requestDto));

        verify(likeRepository, times(0)).save(any(Like.class));
        assertEquals(LikeService.USER_LIKED_THIS_POST, resultException.getMessage());
    }

    @Test
    public void testDeleteLikePostSuccess() {
        LikePostRequestDto requestDto = getLikePostRequestDto();
        UserDto userDto = getUserDto();
        Post post = getPost();
        Like resultEntity = getLikeEntity(null, post);

        when(userService.getUser(USER_ID)).thenReturn(userDto);
        when(likeRepository.deleteByPostIdAndUserId(POST_ID, USER_ID))
                .thenReturn(Optional.ofNullable(resultEntity));

        LikePostResponseDto responseDto = likeService.deletePost(requestDto);

        verify(likeRepository, times(1))
                .deleteByPostIdAndUserId(any(Long.class), any(Long.class));
        assertNotNull(responseDto);
        assertEquals(LIKE_ENTITY_ID, responseDto.id());
        assertEquals(USER_ID, responseDto.userId());
        assertEquals(POST_ID, responseDto.postId());
    }

    @Test
    public void testDeleteLikePostButLikeIsMissing() {
        final String expectedError = utils.format(LikeService.POST_LIKE_NOT_FOUND, USER_ID, POST_ID);

        LikePostRequestDto requestDto = getLikePostRequestDto();
        UserDto userDto = getUserDto();

        when(userService.getUser(USER_ID)).thenReturn(userDto);
        when(likeRepository.deleteByPostIdAndUserId(POST_ID, USER_ID))
                .thenReturn(Optional.empty());

        LikeNotFoundException resultException = assertThrows(LikeNotFoundException.class,
                () -> likeService.deletePost(requestDto));

        assertEquals(expectedError, resultException.getMessage());
    }

    @Test
    public void testDeleteLikePostButUserIsMissing() {
        LikePostRequestDto requestDto = getLikePostRequestDto();
        when(userService.getUser(USER_ID)).thenThrow(FeignException.NotFound.class);

        FeignException.NotFound userNotFoundException = assertThrows(
                FeignException.NotFound.class, () -> userService.getUser(USER_ID));
        UserNotFoundException resultException = assertThrows(UserNotFoundException.class,
                () -> likeService.deletePost(requestDto));

        verify(likeRepository, times(0))
                .deleteByPostIdAndUserId(any(Long.class), any(Long.class));
        final String expectedError = utils.format(LikeService.USER_NOT_FOUND, USER_ID);
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

    private LikeCommentRequestDto getLikeCommentRequestDto() {
        return LikeCommentRequestDto.builder()
                .userId(USER_ID)
                .commentId(COMMENT_ID)
                .build();
    }

    private LikePostRequestDto getLikePostRequestDto() {
        return LikePostRequestDto.builder()
                .userId(USER_ID)
                .postId(POST_ID)
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

    private UserDto getUserDto() {
        return UserDto.builder()
                .id(USER_ID)
                .email(MOCK_EMAIL)
                .username(MOCK_NAME)
                .build();
    }
}