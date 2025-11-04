package faang.school.postservice.service.like;


import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.externalservice.ExternalServiceConnectException;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LikeServiceImplTest {
    private static final int CHUNK_SIZE = 2;
    private static final long POST_ID = 1L;
    private static final long COMMENT_ID = 1L;
    private static final long LIKE_ID_1 = 1L;
    private static final long LIKE_ID_2 = 2L;
    private static final long LIKE_ID_3 = 3L;
    private static final long LIKE_ID_4 = 4L;
    private static final long USER_ID_1 = 101L;
    private static final long USER_ID_2 = 102L;
    private static final long USER_ID_3 = 103L;
    private static final long USER_ID_4 = 104L;
    private final UserDto user1 = new UserDto(USER_ID_1, "JohnDoe", "johndoe@example.com");
    private final UserDto user2 = new UserDto(USER_ID_2, "JaneSmith", "johndoe@example.com");
    private final UserDto user3 = new UserDto(USER_ID_3, "MichaelJohnson", "johndoe@example.com");
    private final UserDto user4 = new UserDto(USER_ID_4, "EmilyDavis", "johndoe@example.com");
    private final Like like1 = Like.builder().id(LIKE_ID_1).userId(USER_ID_1).build();
    private final Like like2 = Like.builder().id(LIKE_ID_2).userId(USER_ID_2).build();
    private final Like like3 = Like.builder().id(LIKE_ID_3).userId(USER_ID_3).build();
    private final Like like4 = Like.builder().id(LIKE_ID_4).userId(USER_ID_4).build();
    private final List<UserDto> users = List.of(user1, user2, user3, user4);
    private final List<Like> likes = List.of(like1, like2, like3, like4);
    @Mock
    LikeRepository likeRepository;
    @Mock
    UserServiceClient userServiceClient;
    private LikeService service;

    @BeforeEach
    void setUp() {
        service = new LikeServiceImpl(likeRepository, userServiceClient, CHUNK_SIZE);
    }

    @Test
    void getUsersLikersByPostId_ChunksMerged() {
        when(likeRepository.findByPostId(eq(POST_ID))).thenReturn(likes);
        getUsersLikerByCommentIdOrPostId_ChunksMerged(() -> service.getUsersLikesByPostId(POST_ID));
    }

    @Test
    void getUsersLikersByPostId_NullLikes() {
        when(likeRepository.findByPostId(POST_ID)).thenReturn(null);
        getUsersLikerByCommentIdOrPostId_NullLikes(() -> service.getUsersLikesByPostId(POST_ID));
    }

    @Test
    void getUsersLikersByPostId_Empty() {
        when(likeRepository.findByPostId(POST_ID)).thenReturn(List.of());
        getUsersLikerByCommentIdOrPostId_Empty(() -> service.getUsersLikesByPostId(POST_ID));
    }

    @Test
    void getUsersLikersByPostId_ExternalServiceConnectException() {
        when(likeRepository.findByPostId(POST_ID)).thenReturn(List.of(like1, like2));
        getUsersLikerByCommentIdOrPostId_ExternalServiceConnectException(
                () -> service.getUsersLikesByPostId(POST_ID));
    }

    @Test
    void getUsersLikerByCommentId_ChunksMerged() {
        when(likeRepository.findByCommentId(eq(COMMENT_ID))).thenReturn(likes);
        getUsersLikerByCommentIdOrPostId_ChunksMerged(() -> service.getUsersLikesByCommentId(COMMENT_ID));
    }


    @Test
    void getUsersLikerByCommentId_NullLikes() {
        when(likeRepository.findByCommentId(COMMENT_ID)).thenReturn(null);
        getUsersLikerByCommentIdOrPostId_NullLikes(() -> service.getUsersLikesByCommentId(COMMENT_ID));
    }

    @Test
    void getUsersLikerByCommentId_Empty() {
        when(likeRepository.findByCommentId(COMMENT_ID)).thenReturn(List.of());
        getUsersLikerByCommentIdOrPostId_Empty(() -> service.getUsersLikesByCommentId(COMMENT_ID));
    }

    @Test
    void getUsersLikerByCommentId_ExternalServiceConnectException() {
        when(likeRepository.findByCommentId(COMMENT_ID)).thenReturn(List.of(like1, like2));
        getUsersLikerByCommentIdOrPostId_ExternalServiceConnectException(
                () -> service.getUsersLikesByCommentId(COMMENT_ID));
    }


    private void getUsersLikerByCommentIdOrPostId_ExternalServiceConnectException(Supplier<List<UserDto>> fn) {
        when(userServiceClient.getUsersByIds(eq(List.of(like1.getUserId(), like2.getUserId()))))
                .thenThrow(new RuntimeException("Exception - RuntimeException"));

        ExternalServiceConnectException ex = assertThrows(
                ExternalServiceConnectException.class,
                fn::get);

        assertTrue(ex.getMessage().contains("The service is not available"));
        assertNotNull(ex.getCause());
    }


    private void getUsersLikerByCommentIdOrPostId_Empty(Supplier<List<UserDto>> fn) {
        List<UserDto> result = fn.get();
        assertTrue(result.isEmpty());
        verifyNoInteractions(userServiceClient);
    }

    private void getUsersLikerByCommentIdOrPostId_ChunksMerged(Supplier<List<UserDto>> fn) {
        when(userServiceClient.getUsersByIds(eq(List.of(like1.getUserId(), like2.getUserId())))).thenReturn(List.of(user1, user2));
        when(userServiceClient.getUsersByIds(eq(List.of(like3.getUserId(), like4.getUserId())))).thenReturn(List.of(user3, user4));

        List<UserDto> resultUsersDto = fn.get();

        assertEquals(users, resultUsersDto);
        verify(userServiceClient, times(1)).getUsersByIds(eq(List.of(like1.getUserId(), like2.getUserId())));
        verify(userServiceClient, times(1)).getUsersByIds(eq(List.of(like3.getUserId(), like4.getUserId())));
        verifyNoMoreInteractions(userServiceClient);
    }

    private void getUsersLikerByCommentIdOrPostId_NullLikes(Supplier<List<UserDto>> fn) {
        List<UserDto> result = fn.get();

        assertTrue(result.isEmpty());
        verifyNoInteractions(userServiceClient);
    }
}