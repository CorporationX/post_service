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
    private List<UserDto> userDtos;
    private final long POST_ID = 1L;

    @BeforeEach
    void init() {
        List<Like> likes = new ArrayList<>();
        userDtos = new ArrayList<>();


        for (int i = 1; i < 150; i++) {
            Like like = new Like();
            like.setUserId((long) i);
            likes.add(like);

            UserDto userDto = new UserDto();
            userDto.setId((long) i);
            userDtos.add(userDto);
        }

        List<Long> userIds = likes.stream()
                .map(Like::getUserId)
                .toList();
        Mockito.lenient().when(likeRepository.findByPostId(POST_ID))
                .thenReturn(likes);
        Mockito.lenient().when(likeRepository.findByCommentId(POST_ID))
                .thenReturn(likes);
        Mockito.lenient().when(userServiceClient.getUsersByIds(userIds.subList(0, 100)))
                .thenReturn(userDtos.subList(0, 100));
        Mockito.lenient().when(userServiceClient.getUsersByIds(userIds.subList(100, 149)))
                .thenReturn(userDtos.subList(100, 149));
    }

    @Test
    void getUsersLikedPost_whenOk() {
        Assertions.assertEquals(likeService.getUsersLikedPost(POST_ID), userDtos);
    }

    @Test
    void getUsersLikedComm_whenOk() {
        Assertions.assertEquals(likeService.getUsersLikedComm(POST_ID), userDtos);
    }
}
