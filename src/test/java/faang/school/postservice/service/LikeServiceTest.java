package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceImplTest {
    private static final Request REQUEST = Request.create(Request.HttpMethod.GET, "/likes", Collections.emptyMap(), null, Charset.defaultCharset(), new RequestTemplate());
    private static final Long NON_EXISTENT_USER_ID = 999L;
    private static final Long COMMENT_ID = 1L;
    private static final Long POST_ID = 1L;

    @Mock
    private LikeRepository likeRepository;
    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private LikeServiceImpl likeService;

    private final UserDto firstUserDto = new UserDto(1L, "firstUser", "firstUser@email.com");
    private final UserDto secondUserDto = new UserDto(1L, "secondUser", "secondUser@email.com");

    @Test
    void testGetUsersWhoLikedPost() {
        long postId = 1L;
        Post post = Post.builder().id(postId).build();
        Like like = Like.builder().post(post).build();

        when(likeRepository.findByPostId(postId)).thenReturn(List.of(like));
        when(userServiceClient.getUsersByIds(anyList())).thenReturn(Arrays.asList(firstUserDto, secondUserDto));

        List<UserDto> result = likeService.getUsersWhoLikedPost(postId);
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(likeRepository, times(1)).findByPostId(postId);
        verify(userServiceClient, times(1)).getUsersByIds(anyList());
    }

    @Test
    void testGetUsersWhoLikedComment() {
        long commentId = 1L;
        Comment comment = Comment.builder().id(commentId).build();
        Like like = Like.builder().comment(comment).build();

        when(likeRepository.findByCommentId(commentId)).thenReturn(List.of(like));
        when(userServiceClient.getUsersByIds(anyList())).thenReturn(Arrays.asList(firstUserDto, secondUserDto));

        List<UserDto> result = likeService.getUsersWhoLikedComment(commentId);
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(likeRepository, times(1)).findByCommentId(commentId);
        verify(userServiceClient, times(1)).getUsersByIds(anyList());
    }

    @Test
    void getUsersWhoLikedCommentWithException() {
        Like likeWithNonExistentUser = Like.builder().userId(NON_EXISTENT_USER_ID).build();

        when(likeRepository.findByCommentId(COMMENT_ID)).thenReturn(List.of(likeWithNonExistentUser));
        when(userServiceClient.getUsersByIds(anyList()))
                .thenThrow(new FeignException.NotFound("User not found", REQUEST, null, null));

        assertThrows(FeignException.NotFound.class, () -> likeService.getUsersWhoLikedComment(COMMENT_ID));
        verify(userServiceClient).getUsersByIds(List.of(NON_EXISTENT_USER_ID));
    }

    @Test
    void getUsersWhoLikedPostWithException() {
        Like likeWithNonExistentUser = Like.builder().userId(NON_EXISTENT_USER_ID).build();

        when(likeRepository.findByPostId(POST_ID)).thenReturn(List.of(likeWithNonExistentUser));
        when(userServiceClient.getUsersByIds(anyList()))
                .thenThrow(new FeignException.NotFound("User not found", REQUEST, null, null));

        assertThrows(FeignException.NotFound.class, () -> likeService.getUsersWhoLikedPost(POST_ID));
        verify(userServiceClient).getUsersByIds(List.of(NON_EXISTENT_USER_ID));
    }
}
