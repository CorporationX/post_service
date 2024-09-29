package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.util.ExceptionThrowingValidator;
import faang.school.postservice.validator.LikeValidator;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    private static final Request REQUEST = Request.create(Request.HttpMethod.GET, "/some-url", Collections.emptyMap(), null, Charset.defaultCharset(), new RequestTemplate());
    private static final FeignException.NotFound USER_NOT_FOUND = new FeignException.NotFound("User not found", REQUEST, null, null);
    private static final UserDto USER_1 = new UserDto(101L, "user1", "user1@example.com");
    private static final UserDto USER_2 = new UserDto(102L, "user2", "user2@example.com");

    @Mock
    private LikeRepository likeRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private ExceptionThrowingValidator validator;
    @Mock
    private LikeValidator likeValidator;
    @Mock
    private LikeMapper likeMapper;
    @InjectMocks
    private LikeService likeService;

    private Long postId;
    private Long commentId;
    private Long userId;
    private LikeDto likeDto;
    private Like likeEntity;

    @BeforeEach
    void setUp() {
        postId = 1L;
        commentId = 1L;
        userId = 2L;

        likeDto = new LikeDto();
        likeDto.setUserId(userId);
        likeDto.setPostId(postId);
        likeDto.setCommentId(commentId);

        likeEntity = new Like();
        likeEntity.setUserId(userId);
        likeEntity.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testGetAllUsersLikedPost_positive() {
        long postId = 1L;
        Post post = new Post();
        post.setId(postId);
        Like like = new Like();
        like.setPost(post);
        List<Like> likes = List.of(like);

        when(likeRepository.findByPostId(postId)).thenReturn(likes);

        List<UserDto> userDtos = Arrays.asList(USER_1, USER_2);

        when(userServiceClient.getUsersByIds(anyList())).thenReturn(userDtos);

        List<UserDto> result = likeService.getAllUsersLikedPost(postId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(likeRepository, times(1)).findByPostId(postId);
        verify(userServiceClient, times(1)).getUsersByIds(anyList());
        verify(validator, times(2)).validate(any(UserDto.class));
    }

    @Test
    void testGetAllUsersLikedComment() {
        long commentId = 1L;
        Comment comment = new Comment();
        comment.setId(commentId);
        Like like = new Like();
        like.setComment(comment);
        List<Like> likes = List.of(like);

        when(likeRepository.findByCommentId(commentId)).thenReturn(likes);

        List<UserDto> userDtos = Arrays.asList(USER_1, USER_2);
        when(userServiceClient.getUsersByIds(anyList())).thenReturn(userDtos);

        List<UserDto> result = likeService.getAllUsersLikedComment(commentId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(likeRepository, times(1)).findByCommentId(commentId);
        verify(userServiceClient, times(1)).getUsersByIds(anyList());
        verify(validator, times(2)).validate(any(UserDto.class));
    }

    @Test
    void testGetUsersByButchesWithException() {
        long postId = 1L;
        Post post = new Post();
        post.setId(postId);
        Like like1 = new Like();
        Like like2 = new Like();
        like1.setPost(post);
        like1.setUserId(101L);
        like2.setPost(post);
        like2.setUserId(102L);
        List<Like> likes = List.of(like1, like2);
        when(likeRepository.findByPostId(postId)).thenReturn(likes);

        List<Long> userIds = Arrays.asList(101L, 102L);
        when(userServiceClient.getUsersByIds(userIds)).thenThrow(USER_NOT_FOUND);

        when(userServiceClient.getUser(101L)).thenReturn(USER_1);
        when(userServiceClient.getUser(102L)).thenReturn(USER_2);

        List<UserDto> result = likeService.getAllUsersLikedPost(postId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(likeRepository, times(1)).findByPostId(postId);
        verify(userServiceClient, times(1)).getUsersByIds(userIds);
        verify(userServiceClient, times(1)).getUser(101L);
        verify(userServiceClient, times(1)).getUser(102L);
        verify(validator, times(2)).validate(any(UserDto.class));
    }

    @Test
    void testGetAllUsersLikedCommentWithException() {
        long commentId = 1L;
        Comment comment = new Comment();
        comment.setId(commentId);
        Like like1 = new Like();
        Like like2 = new Like();
        like1.setComment(comment);
        like1.setUserId(101L);
        like2.setComment(comment);
        like2.setUserId(102L);
        List<Like> likes = List.of(like1, like2);
        when(likeRepository.findByCommentId(commentId)).thenReturn(likes);

        List<Long> userIds = Arrays.asList(101L, 102L);
        when(userServiceClient.getUsersByIds(userIds)).thenThrow(USER_NOT_FOUND);

        when(userServiceClient.getUser(101L)).thenReturn(USER_1);
        when(userServiceClient.getUser(102L)).thenReturn(USER_2);

        List<UserDto> result = likeService.getAllUsersLikedComment(commentId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(likeRepository, times(1)).findByCommentId(commentId);
        verify(userServiceClient, times(1)).getUsersByIds(userIds);
        verify(userServiceClient, times(1)).getUser(101L);
        verify(userServiceClient, times(1)).getUser(102L);
        verify(validator, times(2)).validate(any(UserDto.class));
    }

    @Test
    void addLikeToPostTest_Success() {
        doNothing().when(likeValidator).userValidation(userId);
        doNothing().when(likeValidator).validatePostExists(postId);
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.empty());
        when(likeMapper.toEntity(likeDto)).thenReturn(likeEntity);
        when(likeRepository.save(likeEntity)).thenReturn(likeEntity);

        likeService.addLikeToPost(postId, likeDto);

        verify(likeRepository, times(1)).save(likeEntity);
        assertNotNull(likeEntity.getCreatedAt());
    }

    @Test
    void addLikeToPostTest_LikeAlreadyExists() {
        doNothing().when(likeValidator).userValidation(userId);
        doNothing().when(likeValidator).validatePostExists(postId);
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.of(likeEntity));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> likeService.addLikeToPost(postId, likeDto));
        assertEquals("Лайк уже поставлен", exception.getMessage());
        verify(likeRepository, never()).save(any());
    }

    @Test
    void removeLikeFromPostTest_Success() {
        doNothing().when(likeValidator).userValidation(userId);
        doNothing().when(likeValidator).validatePostExists(postId);
        likeService.removeLikeFromPost(postId, likeDto);

        verify(likeRepository, times(1)).deleteByPostIdAndUserId(postId, userId);
    }

    @Test
    void addLikeToCommentTest_Success() {
        doNothing().when(likeValidator).userValidation(userId);
        doNothing().when(likeValidator).validateCommentExists(commentId);
        when(likeRepository.findByCommentIdAndUserId(commentId, userId)).thenReturn(Optional.empty());

        likeDto.setCommentId(commentId);
        when(likeMapper.toEntity(likeDto)).thenReturn(likeEntity);
        when(likeRepository.save(likeEntity)).thenReturn(likeEntity);

        likeService.addLikeToComment(commentId, likeDto);

        verify(likeRepository, times(1)).save(likeEntity);
        assertNotNull(likeEntity.getCreatedAt());
    }

    @Test
    void addLikeToCommentTest_LikeAlreadyExists() {
        doNothing().when(likeValidator).userValidation(userId);
        doNothing().when(likeValidator).validateCommentExists(commentId);
        when(likeRepository.findByCommentIdAndUserId(commentId, userId)).thenReturn(Optional.of(likeEntity));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> likeService.addLikeToComment(commentId, likeDto));

        assertEquals("Лайк уже поставлен", exception.getMessage());
        verify(likeRepository, never()).save(any());
    }

    @Test
    void removeLikeFromCommentTest_Success() {
        doNothing().when(likeValidator).userValidation(userId);
        doNothing().when(likeValidator).validateCommentExists(commentId);

        likeService.removeLikeFromComment(commentId, likeDto);

        verify(likeRepository, times(1)).deleteByCommentIdAndUserId(commentId, userId);
    }

    @Test
    void getLikesFromPostTest_Success() {
        List<Like> likes = Arrays.asList(new Like() {
            {
                setUserId(1L);
            }
        }, new Like() {
            {
                setUserId(2L);
            }
        }, new Like() {
            {
                setUserId(3L);
            }
        });

        when(likeRepository.findByPostId(postId)).thenReturn(likes);

        List<Long> result = likeService.getLikesFromPost(postId);

        assertEquals(3, result.size());
        assertTrue(result.contains(1L));
        assertTrue(result.contains(2L));
        assertTrue(result.contains(3L));
    }

    @Test
    void getLikesFromCommentTest_Success() {
        List<Like> likes = Arrays.asList(new Like() {
            {
                setUserId(1L);
            }
        }, new Like() {
            {
                setUserId(2L);
            }
        }, new Like() {
            {
                setUserId(3L);
            }
        });

        when(likeRepository.findByCommentId(commentId)).thenReturn(likes);

        List<Long> result = likeService.getLikesFromComment(commentId);

        assertEquals(3, result.size());
        assertTrue(result.contains(1L));
        assertTrue(result.contains(2L));
        assertTrue(result.contains(3L));
    }
}