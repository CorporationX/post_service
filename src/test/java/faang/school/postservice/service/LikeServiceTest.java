package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.LikeEventMapper;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.producer.KafkaLikeProducer;
import faang.school.postservice.producer.KafkaProducer;
import faang.school.postservice.publisher.LikeEventPublisher;
import faang.school.postservice.redisPublisher.LikePostPublisher;
import faang.school.postservice.redisPublisher.PostLikeEventPublisher;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.validator.LikeServiceValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {

    public static int BATCH_SIZE = 100;

    @Mock
    private LikeRepository likeRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private LikeServiceValidator likeServiceValidator;
    @Mock
    private PostService postService;
    @Mock
    private CommentService commentService;
    @Mock
    private LikeMapper likeMapper;
    @Mock
    private LikeEventPublisher likeEventPublisher;
    @Mock
    private LikePostPublisher likePostPublisher;
    @Mock
    private LikeEventMapper likeEventMapper;
    @Mock
    private PostLikeEventPublisher postLikeEventPublisher;
    @Mock
    private KafkaProducer kafkaProducer;

    @InjectMocks
    private LikeService likeService;

    private final List<Like> likes = new ArrayList<>();
    private final List<UserDto> usersDto = new ArrayList<>();
    private long postId;
    private long userId;
    private long commentId;
    private LikeDto likeDtoPost;
    private LikeDto likeDtoComment;
    private Post post;
    private Comment comment;
    private LikeDto likeDto;
    private UserDto userDto;
    private Like like;

    @BeforeEach
    public void setup() {
        postId = 1;
        userId = 2;
        commentId = 3;

        userDto = UserDto.builder()
                .id(userId)
                .username("name")
                .email("email@google.com")
                .build();

        like = Like.builder()
                .id(1L)
                .userId(userId)
                .post(Post.builder()
                        .id(postId)
                        .build())
                .build();

        likeDtoPost = LikeDto.builder()
                .id(1L)
                .userId(1L)
                .postId(1L)
                .build();

        likeDtoComment = LikeDto.builder()
                .id(1L)
                .userId(1)
                .commentId(1)
                .build();

        likeDto = new LikeDto();
        likeDto.setId(1L);
        likeDto.setUserId(1L);

        likeService.setBATCH_SIZE(BATCH_SIZE);

        comment = Comment.builder()
                .id(2L)
                .likes(new ArrayList<>(Arrays.asList(new Like(), new Like())))
                .build();

        post = Post.builder()
                .id(1L)
                .authorId(2L)
                .likes(new ArrayList<>(Arrays.asList(new Like(), new Like())))
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

    @DisplayName("Когда метод по добавлению лайка к посту отработал")
    @Test
    public void testAddLikeToPostWhenValid() {
        UserDto userDto = new UserDto(1L, "name", "email@google.com", "", true);
        post.setAuthorId(1L);

        when(postService.getPost(likeDtoPost.getPostId())).thenReturn(post);
        when(userServiceClient.getUser(likeDtoPost.getUserId())).thenReturn(userDto);
        when(likeRepository.findByPostIdAndUserId(post.getId(), userDto.getId())).thenReturn(Optional.empty());
        when(likeMapper.toEntity(likeDtoPost)).thenReturn(like);
        when(likeMapper.toLikeDto(like)).thenReturn(likeDto);
        doNothing().when(likeEventPublisher).publish(any());
        doNothing().when(likePostPublisher).publish(any());
        doNothing().when(postLikeEventPublisher).publish(any());
        when(likeRepository.save(any())).thenReturn(like);

        likeService.addLikeToPost(likeDtoPost);

        verify(likeServiceValidator, times(1)).checkDuplicateLike(Optional.empty());
        verify(postService, times(1)).getPost(likeDtoPost.getPostId());
        verify(userServiceClient, times(1)).getUser(likeDtoPost.getUserId());
        verify(likeRepository, times(1)).findByPostIdAndUserId(post.getId(), userDto.getId());
        verify(likeMapper, times(1)).toEntity(likeDtoPost);
        verify(likeMapper, times(1)).toLikeDto(like);
        verify(likeEventPublisher, times(1)).publish(any());
        verify(likePostPublisher, times(1)).publish(any());
        verify(postLikeEventPublisher, times(1)).publish(any());
        verify(likeRepository, times(1)).save(like);
        verify(kafkaProducer, times(1)).sendEvent(any());
    }

    @DisplayName("Когда метод по удалению лайка с поста отработал")
    @Test
    public void testDeleteLikeFromPostWhenValid() {
        Like like = new Like();
        like.setId(1);
        Optional<Like> optionalLike = Optional.of(like);
        post.setLikes(Arrays.asList(like, new Like()));

        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(optionalLike);
        when(postService.getPost(postId)).thenReturn(post);

        likeService.deleteLikeFromPost(postId, userId);
        verify(likeRepository, times(1)).deleteByPostIdAndUserId(postId, userId);
        verify(likeServiceValidator, times(1)).checkAvailabilityLike(optionalLike);
    }

    @DisplayName("Когда метод по добавлению лайка на комментарий отработал")
    @Test
    public void testAddLikeToCommentWhenValid() {
        Like like = new Like();
        UserDto userDto = new UserDto(1L, "name", "email@google.com", "", true);

        when(commentService.getComment(likeDtoComment.getCommentId())).thenReturn(comment);
        when(userServiceClient.getUser(likeDtoComment.getUserId())).thenReturn(userDto);
        when(likeRepository.findByCommentIdAndUserId(comment.getId(), userDto.getId())).thenReturn(Optional.empty());
        when(likeMapper.toEntity(likeDtoComment)).thenReturn(like);
        when(likeMapper.toLikeDto(like)).thenReturn(likeDto);

        likeService.addLikeToComment(likeDtoComment);

        verify(likeServiceValidator, times(1)).checkDuplicateLike(Optional.empty());
        verify(likeRepository, times(1)).save(like);
    }

    @DisplayName("Когда метод по удалению лайка с комментария отработал")
    @Test
    public void testDeleteLikeFromCommentWhenValid() {
        Like like = new Like();
        like.setId(1);
        Optional<Like> optionalLike = Optional.of(like);
        List<Like> likes = Arrays.asList(like);
        comment.setLikes(likes);

        when(likeRepository.findByCommentIdAndUserId(commentId, userId)).thenReturn(optionalLike);
        when(commentService.getComment(commentId)).thenReturn(comment);

        likeService.deleteLikeFromComment(commentId, userId);
        verify(likeRepository, times(1)).deleteByCommentIdAndUserId(commentId, userId);
        verify(likeServiceValidator, times(1)).checkAvailabilityLike(optionalLike);
    }
}