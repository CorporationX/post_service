package faang.school.postservice.service.like;


import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.externalservice.ExternalServiceConnectException;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LikeServiceImplTest {
    private static final int CHUNK_SIZE = 2;
    private static final long USER_ID_1 = 1;
    private static final long USER_ID_2 = 2;
    private static final long USER_ID_3 = 3;
    private static final long USER_ID_4 = 4;
    private static final long POST_ID = 1L;
    private static final long COMMENT_ID = 1L;
    @Mock
    LikeRepository likeRepository;
    @Mock
    UserServiceClient userServiceClient;
    private LikeService service;
    private Like like1;
    private Like like2;
    private Like like3;
    private Like like4;
    private UserDto user1;
    private UserDto user2;
    private UserDto user3;
    private UserDto user4;
    private List<UserDto> users;
    private List<Like> likes;

    @BeforeAll
    void globalSettings() {
        long likeId1 = 1L;
        long likeId2 = 2L;
        long likeId3 = 3L;
        long likeId4 = 4L;
        like1 = Like.builder().id(likeId1).userId(USER_ID_1).build();
        like2 = Like.builder().id(likeId2).userId(USER_ID_2).build();
        like3 = Like.builder().id(likeId3).userId(USER_ID_3).build();
        like4 = Like.builder().id(likeId4).userId(USER_ID_4).build();
        user1 = new UserDto(USER_ID_1, "JohnDoe", "johndoe@example.com");
        user2 = new UserDto(USER_ID_2, "JaneSmith", "johndoe@example.com");
        user3 = new UserDto(USER_ID_3, "MichaelJohnson", "johndoe@example.com");
        user4 = new UserDto(USER_ID_4, "EmilyDavis", "johndoe@example.com");
        users = List.of(user1, user2, user3, user4);
        likes = List.of(like1, like2, like3, like4);
    }

    @BeforeEach
    void setUp() {
        service = new LikeServiceImpl(likeRepository, userServiceClient, CHUNK_SIZE);
    }

    @Test
    void getUsersLikersByPostId_ChunksMerged() {
        when(likeRepository.findByPostId(eq(POST_ID))).thenReturn(likes);
        getUsersLikerByCommentIdOrPostId_ChunksMerged(() -> service.getUsersLikersByPostId(POST_ID));
    }

    @Test
    void getUsersLikersByPostId_NullLikes() {
        when(likeRepository.findByPostId(POST_ID)).thenReturn(null);
        getUsersLikerByCommentIdOrPostId_NullLikes(() -> service.getUsersLikersByPostId(POST_ID));
    }

    @Test
    void getUsersLikersByPostId_Empty() {
        when(likeRepository.findByPostId(POST_ID)).thenReturn(List.of());
        getUsersLikerByCommentIdOrPostId_Empty(() -> service.getUsersLikersByPostId(POST_ID));
    }

    @Test
    void getUsersLikersByPostId_ExternalServiceConnectException() {
        when(likeRepository.findByPostId(POST_ID)).thenReturn(List.of(like1, like2));
        getUsersLikerByCommentIdOrPostId_ExternalServiceConnectException(
                () -> service.getUsersLikersByPostId(POST_ID));
    }

    @Test
    void getUsersLikerByCommentId_ChunksMerged() {
        when(likeRepository.findByCommentId(eq(COMMENT_ID))).thenReturn(likes);
        getUsersLikerByCommentIdOrPostId_ChunksMerged(() -> service.getUsersLikerByCommentId(COMMENT_ID));
    }


    @Test
    void getUsersLikerByCommentId_NullLikes() {
        when(likeRepository.findByCommentId(COMMENT_ID)).thenReturn(null);
        getUsersLikerByCommentIdOrPostId_NullLikes(() -> service.getUsersLikerByCommentId(COMMENT_ID));
    }

    @Test
    void getUsersLikerByCommentId_Empty() {
        when(likeRepository.findByCommentId(COMMENT_ID)).thenReturn(List.of());
        getUsersLikerByCommentIdOrPostId_Empty(() -> service.getUsersLikerByCommentId(COMMENT_ID));
    }

    @Test
    void getUsersLikerByCommentId_ExternalServiceConnectException() {
        when(likeRepository.findByCommentId(COMMENT_ID)).thenReturn(List.of(like1, like2));
        getUsersLikerByCommentIdOrPostId_ExternalServiceConnectException(
                () -> service.getUsersLikerByCommentId(POST_ID));
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