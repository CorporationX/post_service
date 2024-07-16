package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RunWith(SpringRunner.class)
public class LikeServiceTest {

    public static int BATCH_SIZE = 100;

    @Spy
    private ExecutorService executorService;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private LikeService likeService;

    private final List<Like> likes = new ArrayList<>();
    private final List<UserDto> usersDto = new ArrayList<>();

    @BeforeEach
    public void setup() {

        likeService.setBATCH_SIZE(BATCH_SIZE);

        Comment comment = Comment.builder()
                .id(2L)
                .build();

        Post post = Post.builder()
                .id(1L)
                .build();

        for (long i = 1; i <= 5; i++) {
            likes.add(Like.builder()
                    .userId(i)
                    .comment(comment)
                    .post(post)
                    .build());
        }

        for (long i = 1; i <= 5; i++) {
            usersDto.add(UserDto.builder()
                    .id(i)
                    .build());
        }
    }

    @Test
    @DisplayName("get all likes userDto by postId")
    public void getLikesUsersByPostIdTest() {

        when(likeRepository.findByPostId(anyLong())).thenReturn(likes);
        when(userServiceClient.getUsersByIds(anyList())).thenReturn(usersDto);

        List<UserDto> expectedLikesUsersByPostId = new ArrayList<>(usersDto);
        List<UserDto> actualLikesUsersByPostId = likeService.getLikesUsersByPostId(1L);

        Assertions.assertEquals(expectedLikesUsersByPostId, actualLikesUsersByPostId);

        verify(likeRepository, times(1)).findByPostId(anyLong());
        verify(userServiceClient, times(1)).getUsersByIds(anyList());

    }

    @Test
    @DisplayName("get all likes userDto by commentId")
    public void getLikesUsersByCommentIdTest() {

        when(likeRepository.findByCommentId(anyLong())).thenReturn(likes);
        when(userServiceClient.getUsersByIds(anyList())).thenReturn(usersDto);

        List<UserDto> expectedLikesUsersByPostId = new ArrayList<>(usersDto);
        List<UserDto> actualLikesUsersByPostId = likeService.getLikesUsersByCommentId(1L);

        Assertions.assertEquals(expectedLikesUsersByPostId, actualLikesUsersByPostId);

        verify(likeRepository, times(1)).findByCommentId(anyLong());
        verify(userServiceClient, times(1)).getUsersByIds(anyList());
    }
}
