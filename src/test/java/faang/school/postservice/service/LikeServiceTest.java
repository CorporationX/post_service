package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.EventMapperImpl;
import faang.school.postservice.mapper.LikeMapperImpl;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.KafkaLikeProducer;
import faang.school.postservice.publisher.like.LikeEventPublisher;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.service.validator.LikeValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {
    @Mock
    private LikeEventPublisher likeEventPublisher;
    @Mock
    private PostService postService;
    @Mock
    private UserService userService;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private LikeValidator likeValidator;
    @Mock
    private LikeMapperImpl likeMapperImpl;
    @Mock
    private KafkaLikeProducer kafkaLikeProducer;
    @Mock
    private EventMapperImpl eventMapperImpl;
    @InjectMocks
    private LikeService likeService;

    private LikeDto likeDtoPost;
    private LikeDto likeDtoComment;
    private UserDto userDto;
    private Like like;
    private List<Like> likes;
    private List<UserDto> usersDto;

    @BeforeEach
    public void init() {
        likeDtoPost = LikeDto.builder().postId(1L).userId(1L).build();
        likeDtoComment = LikeDto.builder().commentId(1L).userId(1L).build();

        userDto = UserDto.builder()
                .id(1L)
                .username("name")
                .email("email")
                .build();
        like = Like.builder()
                .id(1L)
                .userId(1L)
                .build();
        likes = List.of(like);
        usersDto = List.of(userDto);
    }

    @Test
    public void testCreateLikePostSuccess() {
        Like like = likeMapperImpl.toEntity(likeDtoPost);
        when(postService.getPostById(likeDtoPost.postId())).thenReturn(Post.builder().authorId(1L).build());
        likeService.createPostLike(likeDtoPost);
        verify(likeValidator, times(1)).validateLikeCreationParams(likeDtoPost);
        verify(likeRepository, times(1)).save(like);
        verify(kafkaLikeProducer).publish(any());
    }

    @Test
    public void testCreateLikeCommentSuccess() {
        Like like = likeMapperImpl.toEntity(likeDtoComment);
        likeService.createCommentLike(likeDtoComment);
        verify(likeValidator, times(1)).validateLikeCreationParams(likeDtoComment);
        verify(likeRepository, times(1)).save(like);
    }

    @Test
    public void testRemovePostLikeSuccess() {
        Mockito.when(userService.getUserDtoById(likeDtoPost.userId())).thenReturn(UserDto.builder().build());
        likeService.removePostLike(likeDtoPost);
        verify(likeValidator, times(1)).checkLikeBeforeDelete(likeDtoPost);
        verify(likeRepository, times(1)).deleteLikeByPostIdAndUserId(likeDtoPost.postId(), likeDtoPost.userId());
    }

    @Test
    public void testRemoveCommentLikeSuccess() {
        Mockito.when(userService.getUserDtoById(likeDtoComment.userId())).thenReturn(UserDto.builder().build());
        likeService.removeCommentLike(likeDtoComment);
        verify(likeValidator, times(1)).checkLikeBeforeDelete(likeDtoComment);
        verify(likeRepository, times(1)).deleteLikeByCommentIdAndUserId(likeDtoComment.commentId(), likeDtoComment.userId());
    }

    @Test
    public void tesGetAllUsersWhoLikedPost() {
        when(likeRepository.findAllByPostId(anyLong())).thenReturn(likes);
        when(userServiceClient.getUsersByIds(anyList())).thenReturn(usersDto);

        List<UserDto> expected = new ArrayList<>(usersDto);
        List<UserDto> result = likeService.getAllUsersWhoLikedPost(1L);

        assertNotNull(result);
        assertEquals(expected, result);

        verify(likeRepository, times(1)).findAllByPostId(anyLong());
        verify(userServiceClient, times(1)).getUsersByIds(anyList());
    }

    @Test
    public void testGetAllUsersWhoLikedComment() {
        when(likeRepository.findAllByCommentId(anyLong())).thenReturn(likes);
        when(userServiceClient.getUsersByIds(anyList())).thenReturn(usersDto);

        List<UserDto> expected = new ArrayList<>(usersDto);
        List<UserDto> result = likeService.getAllUsersWhoLikedComment(1L);

        assertNotNull(result);
        assertEquals(expected, result);

        verify(likeRepository, times(1)).findAllByCommentId(anyLong());
        verify(userServiceClient, times(1)).getUsersByIds(anyList());
    }
}
