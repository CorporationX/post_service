package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.like.LikeService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @InjectMocks
    private LikeService likeService;

    private Long postId;
    private Long likeId;
    Like firstLike;
    Like secondLike;
    List<Like> likes;
    UserDto userOne;
    UserDto userTwo;
    List<UserDto> users;

    @BeforeEach
    void setUp() {
        postId = 1L;
        likeId = 2L;
        firstLike = Like.builder().id(1L).userId(1L).build();
        secondLike = Like.builder().id(2L).userId(2L).build();
        likes = List.of(firstLike, secondLike);
        userOne = new UserDto(1L, "first", "mail.one");
        userTwo = new UserDto(2L, "second", "mail.two");
        users = List.of(userOne, userTwo);
    }

    @Test
    public void getUsersThatLikedPostTest() {
        Mockito.when(likeRepository.findByPostId(postId)).thenReturn(likes);
        Mockito.when(userServiceClient.getUsersByIds(Mockito.anyList())).thenReturn(users);
        List<UserDto> actualUsers = likeService.getUsersThatLikedPost(postId);
        Mockito.verify(likeRepository, Mockito.times(1)).findByPostId(1L);
        Mockito.verify(userServiceClient, Mockito.times(1)).getUsersByIds(Mockito.anyList());
        Assert.assertEquals(actualUsers, users);
    }

    @Test
    public void getUsersThatLikedCommentTest() {
        Mockito.when(likeRepository.findByCommentId(likeId)).thenReturn(likes);
        Mockito.when(userServiceClient.getUsersByIds(Mockito.anyList())).thenReturn(users);
        List<UserDto> actualUsers = likeService.getUsersThatLikedComment(likeId);
        Mockito.verify(likeRepository, Mockito.times(1)).findByCommentId(likeId);
        Mockito.verify(userServiceClient, Mockito.times(1)).getUsersByIds(Mockito.anyList());
        Assert.assertEquals(actualUsers, users);
    }

    @Test
    public void deleteLikeFromNonExistentPostTest() {
        Mockito.when(likeRepository.findByPostIdAndUserId(1L, 1L)).thenReturn(Optional.empty());
        Assert.assertThrows(EntityNotFoundException.class, () -> {
            likeService.deleteLikeFromPost(1L, 1L);
        });
    }

    @Test
    public void deleteLikeFromCommentTest() {
        Mockito.when(likeRepository.findByCommentIdAndUserId(2L, 2L)).thenReturn(Optional.empty());
        Assert.assertThrows(EntityNotFoundException.class, () -> {
            likeService.deleteLikeFromComment(2L, 2L);
        });
    }
}
