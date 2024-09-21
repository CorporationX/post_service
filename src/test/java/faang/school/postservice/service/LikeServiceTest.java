package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.util.CustomValidator;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.any;
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
    private CustomValidator validator;

    @InjectMocks
    private LikeService likeService;

    @BeforeEach
    void setUp() {
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

}