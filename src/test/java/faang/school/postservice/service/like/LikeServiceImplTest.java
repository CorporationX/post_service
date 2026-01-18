package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.externalservice.ExternalServiceConnectException;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceImplTest {

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

    private final UserDto user1 = user(USER_ID_1, "JohnDoe", "johndoe@example.com");
    private final UserDto user2 = user(USER_ID_2, "JaneSmith", "janesmith@example.com");
    private final UserDto user3 = user(USER_ID_3, "MichaelJohnson", "michael@example.com");
    private final UserDto user4 = user(USER_ID_4, "EmilyDavis", "emily@example.com");

    private final Like like1 = like(LIKE_ID_1, USER_ID_1);
    private final Like like2 = like(LIKE_ID_2, USER_ID_2);
    private final Like like3 = like(LIKE_ID_3, USER_ID_3);
    private final Like like4 = like(LIKE_ID_4, USER_ID_4);

    private final List<UserDto> expectedUsers = List.of(user1, user2, user3, user4);
    private final List<Like> likes = List.of(like1, like2, like3, like4);

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private UserServiceClient userServiceClient;

    private LikeService service;

    @BeforeEach
    void setUp() {
        service = new LikeServiceImpl(likeRepository, userServiceClient, CHUNK_SIZE);
    }

    // -------------------- post --------------------

    @Test
    @DisplayName("getUsersLikesByPostId should merge chunks from user-service into a single list")
    void getUsersLikesByPostId_shouldMergeChunks() {
        when(likeRepository.findByPostId(eq(POST_ID))).thenReturn(likes);
        assertMergedChunks(() -> service.getUsersLikesByPostId(POST_ID));
    }

    @Test
    @DisplayName("getUsersLikesByPostId should return empty list when repository returns null")
    void getUsersLikesByPostId_shouldReturnEmpty_whenNullLikes() {
        when(likeRepository.findByPostId(POST_ID)).thenReturn(null);
        assertEmptyWithoutUserServiceCall(() -> service.getUsersLikesByPostId(POST_ID));
    }

    @Test
    @DisplayName("getUsersLikesByPostId should return empty list when there are no likes")
    void getUsersLikesByPostId_shouldReturnEmpty_whenNoLikes() {
        when(likeRepository.findByPostId(POST_ID)).thenReturn(List.of());
        assertEmptyWithoutUserServiceCall(() -> service.getUsersLikesByPostId(POST_ID));
    }

    @Test
    @DisplayName("getUsersLikesByPostId should throw ExternalServiceConnectException when user-service fails")
    void getUsersLikesByPostId_shouldThrow_whenUserServiceFails() {
        when(likeRepository.findByPostId(POST_ID)).thenReturn(List.of(like1, like2));
        assertExternalServiceFailure(() -> service.getUsersLikesByPostId(POST_ID));
    }

    // -------------------- comment --------------------

    @Test
    @DisplayName("getUsersLikesByCommentId should merge chunks from user-service into a single list")
    void getUsersLikesByCommentId_shouldMergeChunks() {
        when(likeRepository.findByCommentId(eq(COMMENT_ID))).thenReturn(likes);
        assertMergedChunks(() -> service.getUsersLikesByCommentId(COMMENT_ID));
    }

    @Test
    @DisplayName("getUsersLikesByCommentId should return empty list when repository returns null")
    void getUsersLikesByCommentId_shouldReturnEmpty_whenNullLikes() {
        when(likeRepository.findByCommentId(COMMENT_ID)).thenReturn(null);
        assertEmptyWithoutUserServiceCall(() -> service.getUsersLikesByCommentId(COMMENT_ID));
    }

    @Test
    @DisplayName("getUsersLikesByCommentId should return empty list when there are no likes")
    void getUsersLikesByCommentId_shouldReturnEmpty_whenNoLikes() {
        when(likeRepository.findByCommentId(COMMENT_ID)).thenReturn(List.of());
        assertEmptyWithoutUserServiceCall(() -> service.getUsersLikesByCommentId(COMMENT_ID));
    }

    @Test
    @DisplayName("getUsersLikesByCommentId should throw ExternalServiceConnectException when user-service fails")
    void getUsersLikesByCommentId_shouldThrow_whenUserServiceFails() {
        when(likeRepository.findByCommentId(COMMENT_ID)).thenReturn(List.of(like1, like2));
        assertExternalServiceFailure(() -> service.getUsersLikesByCommentId(COMMENT_ID));
    }

    // -------------------- shared assertions --------------------

    private void assertMergedChunks(Supplier<List<UserDto>> call) {
        when(userServiceClient.getUsersByIds(eq(List.of(like1.getUserId(), like2.getUserId()))))
                .thenReturn(List.of(user1, user2));
        when(userServiceClient.getUsersByIds(eq(List.of(like3.getUserId(), like4.getUserId()))))
                .thenReturn(List.of(user3, user4));

        List<UserDto> actual = call.get();

        assertEquals(expectedUsers, actual);
        verify(userServiceClient).getUsersByIds(eq(List.of(like1.getUserId(), like2.getUserId())));
        verify(userServiceClient).getUsersByIds(eq(List.of(like3.getUserId(), like4.getUserId())));
        verifyNoMoreInteractions(userServiceClient);
    }

    private void assertEmptyWithoutUserServiceCall(Supplier<List<UserDto>> call) {
        List<UserDto> result = call.get();
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verifyNoInteractions(userServiceClient);
    }

    private void assertExternalServiceFailure(Supplier<List<UserDto>> call) {
        when(userServiceClient.getUsersByIds(eq(List.of(like1.getUserId(), like2.getUserId()))))
                .thenThrow(new RuntimeException("Boom"));

        ExternalServiceConnectException ex = assertThrows(
                ExternalServiceConnectException.class,
                call::get
        );

        assertTrue(ex.getMessage().contains("The service is not available"));
        assertNotNull(ex.getCause());
    }

    // -------------------- fixtures --------------------

    private static Like like(long likeId, long userId) {
        return Like.builder()
                .id(likeId)
                .userId(userId)
                .build();
    }

    private static UserDto user(long id, String username, String email) {
        return UserDto.builder()
                .id(id)
                .username(username)
                .email(email)
                .build();
    }
}
