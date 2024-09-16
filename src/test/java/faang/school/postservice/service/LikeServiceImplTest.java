package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class LikeServiceImplTest {
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @InjectMocks
    private LikeServiceImpl likeService;
    private List<Like> likes;
    private List<UserDto> userDtos;

    @BeforeEach
    void init() {
        likes = new ArrayList<>();
        userDtos = new ArrayList<>();

        for (int i = 1; i < 150; i++) {
            Like like = new Like();
            like.setUserId((long) i);
            likes.add(like);

            UserDto userDto = new UserDto();
            userDto.setId((long) i);
            userDtos.add(userDto);
        }
    }

    @Test
    void getUsersLikedPost_whenOk() {
        long postId = 1L;
        List<Long> userIds = likes.stream()
                .map(Like::getUserId)
                .toList();
        Mockito.when(likeRepository.findByPostId(postId))
                .thenReturn(likes);
        Mockito.when(userServiceClient.getUsersByIds(userIds.subList(0, 100)))
                .thenReturn(userDtos.subList(0, 100));
        Mockito.when(userServiceClient.getUsersByIds(userIds.subList(100, 149)))
                .thenReturn(userDtos.subList(100, 149));

        Assertions.assertEquals(likeService.getUsersLikedPost(postId), userDtos);
    }

    @Test
    void getUsersLikedComm_whenOk() {
        long postId = 1L;
        List<Long> userIds = likes.stream()
                .map(Like::getUserId)
                .toList();
        Mockito.when(likeRepository.findByCommentId(postId))
                .thenReturn(likes);
        Mockito.when(userServiceClient.getUsersByIds(userIds.subList(0, 100)))
                .thenReturn(userDtos.subList(0, 100));
        Mockito.when(userServiceClient.getUsersByIds(userIds.subList(100, 149)))
                .thenReturn(userDtos.subList(100, 149));

        Assertions.assertEquals(likeService.getUsersLikedComm(postId), userDtos);
    }
}
