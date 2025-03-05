package faang.school.postservice.service.like;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.kafka.producer.KafkaLikeProducer;
import faang.school.postservice.publisher.MessageSenderForLikeAnalyticsImpl;
import faang.school.postservice.dto.like.AnalyticsEventDto;
import faang.school.postservice.dto.like.LikeDtoForComment;
import faang.school.postservice.dto.like.LikeDtoForPost;
import faang.school.postservice.dto.like.ResponseLikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.like.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validator.dto.user.UserDtoValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {

    @Mock
    UserDtoValidator userDtoValidator;

    @Mock
    LikeRepository likeRepository;

    @Mock
    LikeMapper likeMapper;

    @Mock
    UserServiceClient userServiceClient;

    @Mock
    PostService postService;

    @Mock
    CommentService commentService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private MessageSenderForLikeAnalyticsImpl likeEventPublisher;

    @InjectMocks
    LikeService likeService;

    @Mock
    private KafkaLikeProducer kafkaLikeProducer;


    @Test
    public void positiveDeleteLikeFromPostTest() {
        long postId = 1L;
        long userId = 1L;

        when(postService.existsPost(postId)).thenReturn(true);
        doNothing().when(likeRepository).deleteByPostIdAndUserId(postId, userId);

        likeService.deleteLikeFromPost(new LikeDtoForPost(userId, postId));

        verify(postService, times(1)).existsPost(postId);
        verify(likeRepository, times(1)).deleteByPostIdAndUserId(postId, userId);

    }

    @Test
    public void negativeDeleteLikeFromPostTest() {
        long postId = 1L;
        long userId = 1L;

        when(postService.existsPost(postId)).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () ->
                likeService.deleteLikeFromPost(new LikeDtoForPost(userId, postId)));

        verify(postService, times(1)).existsPost(postId);
        verify(likeRepository, times(0)).deleteByPostIdAndUserId(postId, userId);
    }

    @Test
    void negativeTestAddLikeByCommentUserNotFound() {
        long userId = 1L;
        long commentId = 100L;
        LikeDtoForComment likeDtoForComment = getLikeDtoForComment(userId, commentId);

        when(userServiceClient.getUser(userId)).thenReturn(null);

        doThrow(new EntityNotFoundException("User not found")).when(userDtoValidator).validateUserDto(null);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                likeService.addLikeByComment(likeDtoForComment)
        );

        verify(userServiceClient, times(1)).getUser(userId);

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void positiveTestAddLikeByComment() {
        long userId = 1L;
        long commentId = 100L;
        LikeDtoForComment likeDtoForComment = getLikeDtoForComment(userId, commentId);
        Comment comment = getComment(1L);

        UserDto userDto = getUserDto(1L);

        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        doNothing().when(userDtoValidator).validateUserDto(userDto);
        when(commentService.findCommentById(likeDtoForComment.getCommentId())).thenReturn(comment);
        when(likeRepository.findByCommentIdAndUserId(likeDtoForComment.getCommentId(), likeDtoForComment.getUserId()))
                .thenReturn(Optional.empty());

        Like likeForComment = Like.builder()
                .userId(userId)
                .comment(comment)
                .build();

        when(likeRepository.save(any(Like.class))).thenReturn(likeForComment);
        ResponseLikeDto responseLikeDto = ResponseLikeDto.builder()
                .userId(userId)
                .commentId(commentId)
                .build();
        when(likeMapper.toLikeDtoFromEntity(likeForComment)).thenReturn(responseLikeDto);

        ResponseLikeDto result = likeService.addLikeByComment(likeDtoForComment);

        verify(userServiceClient, times(1)).getUser(userId);
        verify(userDtoValidator, times(1)).validateUserDto(userDto);
        verify(commentService, times(1)).findCommentById(commentId);
        verify(likeRepository, times(1)).save(any(Like.class));
        verify(likeMapper, times(1)).toLikeDtoFromEntity(likeForComment);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(commentId, result.getCommentId());
    }

    @Test
    @DisplayName("IllegalArgumentException if user already liked")
    public void NegativeAddLikeByComment() {
        long userId = 1L;
        long commentId = 100L;
        LikeDtoForComment likeDtoForComment = getLikeDtoForComment(userId, commentId);
        Comment comment = getComment(1L);

        UserDto userDto = getUserDto(1L);

        Like likeForComment = Like.builder()
                .userId(userId)
                .comment(comment)
                .build();

        when(userServiceClient.getUser(likeDtoForComment.getUserId())).thenReturn(userDto);
        doNothing().when(userDtoValidator).validateUserDto(userDto);
        when(likeRepository.findByCommentIdAndUserId(likeDtoForComment.getCommentId(),
                likeDtoForComment.getUserId())).thenReturn(Optional.ofNullable(likeForComment));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> likeService.addLikeByComment(likeDtoForComment));

        assertEquals("User has already liked this comment", exception.getMessage());

        verify(userServiceClient, times(1)).getUser(userId);
        verify(userDtoValidator, times(1)).validateUserDto(userDto);
        verify(likeRepository, times(1)).findByCommentIdAndUserId(likeDtoForComment.getCommentId(),
                likeDtoForComment.getUserId());
        verify(likeRepository, times(0)).save(any(Like.class));
    }

    @Test
    public void negativeTestDeleteLikeFromComment() {
        long userId = 1L;
        long commentId = 100L;
        LikeDtoForComment likeDtoForComment = getLikeDtoForComment(userId, commentId);

        when(commentService.isExits(likeDtoForComment.getCommentId())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> likeService.deleteLikeFromComment(likeDtoForComment));

        verify(commentService, times(1)).isExits(likeDtoForComment.getCommentId());

        assertEquals("Comment not found", exception.getMessage());

    }

    @Test
    public void positiveTestDeleteLikeFromComment() {
        long userId = 1L;
        long commentId = 100L;
        LikeDtoForComment likeDtoForComment = getLikeDtoForComment(userId, commentId);

        when(commentService.isExits(likeDtoForComment.getCommentId())).thenReturn(true);

        doNothing().when(likeRepository).deleteByCommentIdAndUserId(likeDtoForComment.getCommentId(),
                likeDtoForComment.getUserId());

        likeService.deleteLikeFromComment(likeDtoForComment);

        verify(commentService, times(1)).isExits(likeDtoForComment.getCommentId());
        verify(likeRepository, times(1)).deleteByCommentIdAndUserId(likeDtoForComment.getCommentId(),
                likeDtoForComment.getUserId());
    }

    @Test
    @DisplayName("IllegalArgumentException if User has already liked this post")
    public void negativeAddLikeByPost() {
        long userId = 1L;
        long postId = 100L;
        LikeDtoForPost likeDtoForPost = new LikeDtoForPost(userId, postId);

        UserDto userDto = getUserDto(1L);

        Like likeForPost = Like.builder()
                .userId(userId)
                .build();

        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        doNothing().when(userDtoValidator).validateUserDto(userDto);
        when(likeRepository.findByPostIdAndUserId(likeDtoForPost.getPostId(),
                likeDtoForPost.getUserId())).thenReturn(Optional.ofNullable(likeForPost));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> likeService.addLikeByPost(likeDtoForPost));

        verify(userServiceClient, times(1)).getUser(userId);
        verify(userDtoValidator, times(1)).validateUserDto(userDto);
        verify(likeRepository, times(1)).findByPostIdAndUserId(likeDtoForPost.getPostId(),
                likeDtoForPost.getUserId());

        assertEquals("User has already liked this post", exception.getMessage());
    }

    @Test
    public void positiveAddLikeByPost() throws IOException {
        long userId = 1L;
        long postId = 100L;
        LikeDtoForPost likeDtoForPost = new LikeDtoForPost(userId, postId);

        UserDto userDto = getUserDto(1L);

        Post post = Post.builder()
                .id(1L)
                .build();

        Like likeForPost = Like.builder()
                .post(post)
                .userId(userId)
                .build();

        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(postService.findPostById(likeDtoForPost.getPostId())).thenReturn(post);
        doNothing().when(userDtoValidator).validateUserDto(userDto);
        when(likeRepository.findByPostIdAndUserId(likeDtoForPost.getPostId(),
                likeDtoForPost.getUserId())).thenReturn(Optional.empty());

        when(likeRepository.save(any(Like.class))).thenReturn(likeForPost);

        ResponseLikeDto responseLikeDto = ResponseLikeDto.builder()
                .userId(userId)
                .postId(100L)
                .build();
        when(likeMapper.toLikeDtoFromEntity(likeForPost)).thenReturn(responseLikeDto);
        when(objectMapper.writeValueAsString(any(AnalyticsEventDto.class))).thenReturn("json-string");

        ResponseLikeDto result = likeService.addLikeByPost(likeDtoForPost);

        assertEquals(userId, result.getUserId());
        assertEquals(postId, result.getPostId());

        verify(userServiceClient, times(1)).getUser(userId);
        verify(postService, times(1)).findPostById(postId);
        verify(likeRepository, times(1)).findByPostIdAndUserId(postId, userId);
        verify(likeRepository, times(1)).save(any(Like.class));
        verify(likeMapper, times(1)).toLikeDtoFromEntity(likeForPost);
        verify(objectMapper, times(1)).writeValueAsString(any(AnalyticsEventDto.class));
        verify(likeEventPublisher, times(1)).send("json-string");
    }

    private static LikeDtoForComment getLikeDtoForComment(long userId, long commentId) {
        return LikeDtoForComment.builder()
                .userId(userId)
                .commentId(commentId)
                .build();
    }

    private static UserDto getUserDto(Long id) {
        return UserDto.builder()
                .id(id)
                .build();
    }

    private static Comment getComment(Long id) {
        return Comment.builder()
                .id(id)
                .build();
    }
}

