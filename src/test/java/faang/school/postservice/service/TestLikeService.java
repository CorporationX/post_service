package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.validator.LikeServiceValidator;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestLikeService {
    @InjectMocks
    private LikeService likeService;

    @Mock
    private LikeServiceValidator likeServiceValidator;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private PostService postService;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private CommentService commentService;
    @Mock
    private LikeMapper likeMapper;

    private long postId;
    private long userId;
    private long commentId;
    private LikeDto likeDtoPost;
    private LikeDto likeDtoComment;
    private Post post;
    private Comment comment;
    private LikeDto likeDto;

    @BeforeEach
    void setUp() {
        postId = 1;
        userId = 1;
        commentId = 1;
        post = new Post();
        post.setId(1);
        post.setLikes(new ArrayList<>(Arrays.asList(new Like(), new Like())));
        comment = new Comment();
        comment.setId(1);
        comment.setLikes(new ArrayList<>(Arrays.asList(new Like(), new Like())));
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
    }

    @DisplayName("Если юзера с таким id не существует")
    @Test
    public void testAddLikeToPostUserNotFound() {
        Request request = Request.create(Request.HttpMethod.GET, "/users/1", Collections.emptyMap(), null, new RequestTemplate());
        FeignException.NotFound notFoundException = new FeignException.NotFound("User not found", request, null, Collections.emptyMap());

        when(postService.getPost(likeDtoPost.getPostId())).thenReturn(post);
        when(userServiceClient.getUser(likeDtoPost.getUserId()))
                .thenThrow(notFoundException);

        assertThrows(IllegalArgumentException.class, () -> likeService.addLikeToPost(likeDtoPost));
        verify(likeRepository, never()).save(new Like());
    }

    @DisplayName("Когда метод по добавлению лайка к посту отработал")
    @Test
    public void testAddLikeToPostWhenValid() {
        UserDto userDto = new UserDto(1L, "name", "email@google.com");
        Like like = new Like();

        when(postService.getPost(likeDtoPost.getPostId())).thenReturn(post);
        when(userServiceClient.getUser(likeDtoPost.getUserId())).thenReturn(userDto);
        when(likeRepository.findByPostIdAndUserId(post.getId(), userDto.getId())).thenReturn(Optional.empty());
        when(likeMapper.toEntity(likeDtoPost)).thenReturn(like);
        when(likeMapper.toLikeDto(like)).thenReturn(likeDto);

        likeService.addLikeToPost(likeDtoPost);

        verify(likeServiceValidator, times(1)).checkDuplicateLike(Optional.empty());
        verify(likeRepository, times(1)).save(like);
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

    @DisplayName("Если юзера с таким id не существует")
    @Test
    public void testAddLikeToCommentUserNotFound() {
        Request request = Request.create(Request.HttpMethod.GET, "/users/1", Collections.emptyMap(), null, new RequestTemplate());
        FeignException.NotFound notFoundException = new FeignException.NotFound("User not found", request, null, Collections.emptyMap());

        when(commentService.getComment(likeDtoComment.getCommentId())).thenReturn(comment);
        when(userServiceClient.getUser(likeDtoComment.getUserId())).thenThrow(notFoundException);

        assertThrows(IllegalArgumentException.class, () -> likeService.addLikeToComment(likeDtoComment));
        verify(likeRepository, never()).save(new Like());
    }

    @DisplayName("Когда метод по добавлению лайка на комментарий отработал")
    @Test
    public void testAddLikeToCommentWhenValid() {
        Like like = new Like();
        UserDto userDto = new UserDto(userId, "name", "email@google.com");

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