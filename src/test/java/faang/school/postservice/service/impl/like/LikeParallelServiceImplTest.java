package faang.school.postservice.service.impl.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class LikeParallelServiceImplTest {

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private LikeParallelServiceImpl likeParallelServiceImpl;

    private long id;
    private List<Like> likes;
    private List<UserDto> userDtos;
    private List<Long> userIds;

    @BeforeEach
    void setUp() {
        id = 1L;
        likes = List.of(
                Like.builder().userId(1L).build(),
                Like.builder().userId(2L).build(),
                Like.builder().userId(3L).build()
        );
        userDtos = List.of(
                UserDto.builder().id(1L).build(),
                UserDto.builder().id(2L).build(),
                UserDto.builder().id(3L).build()
        );
        userIds = List.of(1L, 2L, 3L);
    }

    @Test
    void getUsersLikedPost_Success() {
        when(likeRepository.findByPostId(id)).thenReturn(likes);
        when(userServiceClient.getUsersByIds(userIds)).thenReturn(userDtos);

        List<UserDto> result = likeParallelServiceImpl.getUsersLikedPost(id);

        assertEquals(userDtos, result);
    }

    @Test
    void getUsersLikedComment_Success() {
        when(likeRepository.findByCommentId(id)).thenReturn(likes);
        when(userServiceClient.getUsersByIds(userIds)).thenReturn(userDtos);

        List<UserDto> result = likeParallelServiceImpl.getUsersLikedComment(id);

        assertEquals(userDtos, result);
    }

    @Test
    void getUsersLikedPost_EmptyLikes() {
        when(likeRepository.findByPostId(id)).thenReturn(Collections.emptyList());

        List<UserDto> result = likeParallelServiceImpl.getUsersLikedPost(id);

        assertTrue(result.isEmpty());
    }

    @Test
    void handleException_WhenFetchingUsers() {
        when(likeRepository.findByPostId(id)).thenReturn(likes);
        when(userServiceClient.getUsersByIds(userIds))
                .thenThrow(new RuntimeException("Failed to fetch users"));

        List<UserDto> result = likeParallelServiceImpl.getUsersLikedPost(id);

        assertTrue(result.isEmpty());
    }

    @Test
    void shutdownExecutor() {
        LikeParallelServiceImpl service = new LikeParallelServiceImpl(likeRepository, userServiceClient);

        service.shutdownExecutor();
        boolean isShutdown = service.getExecutor().isShutdown();

        assertTrue(isShutdown);
    }
}