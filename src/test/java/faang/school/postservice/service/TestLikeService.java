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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestLikeService {
    @InjectMocks
    private LikeService service;

    @Mock
    private LikeServiceValidator validator;
    @Mock
    private LikeRepository repository;
    @Mock
    private PostService postService;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private CommentService commentService;
    @Mock
    private LikeMapper mapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("Если юзера с таким id не существует")
    @Test
    public void testAddLikeToPostUserNotFound() {
        LikeDto likeDto = LikeDto.builder()
                .id(1L)
                .userId(1)
                .postId(1)
                .build();
        Post post = new Post();
        post.setId(1);
        when(postService.getPost(likeDto.getPostId())).thenReturn(post);
        when(userServiceClient.getUser(likeDto.getUserId()))
                .thenThrow(new FeignException.NotFound("User not found", null, null, null));

        assertThrows(FeignException.class, () -> service.addLikeToPost(likeDto));
        verify(repository, times(0)).save(new Like());
    }

    @DisplayName("Когда метод по добавлению лайка к посту отработал")
    @Test
    public void testAddLikeToPostWhenValid() {
        long userId = 1;
        LikeDto likeDto = LikeDto.builder()
                .id(1L)
                .userId(userId)
                .postId(1)
                .build();
        Post post = new Post();
        post.setId(1);
        Like like = new Like();

        when(postService.getPost(likeDto.getPostId())).thenReturn(post);
        when(userServiceClient.getUser(likeDto.getUserId()))
                .thenReturn(new UserDto(userId, "name", "email@google.com"));
        when(mapper.toLike(likeDto)).thenReturn(like);
        when(repository.findByPostIdAndUserId(post.getId(), anyLong())).thenReturn(Optional.empty());

        service.addLikeToPost(likeDto);
        verify(validator, times(1)).validDuplicateLike(Optional.empty());
        verify(post.getLikes(), times(1)).add(like);
        verify(repository, times(1)).save(like);
    }

    @DisplayName("Если попытка удалить лайк которого не существует")
    @Test
    public void testDeleteLikeFromPostLikeNotFound() {
        long postId = 1;
        long userId = 1;
        when(repository.findByPostIdAndUserId(postId, userId))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.deleteLikeFromPost(postId,userId));
        verify(repository, times(0)).deleteByPostIdAndUserId(postId, userId);
    }

    @DisplayName("Когда метод по удалению лайка с поста отработал")
    @Test
    public void testDeleteLikeFromPostWhenValid() {
        long postId = 1;
        long userId = 1;
        Like like = new Like();
        like.setId(1);
        List<Like> likes = Arrays.asList(like);
        Post post = new Post();
        post.setLikes(likes);

        when(repository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.of(like));
        when(postService.getPost(postId)).thenReturn(post);

        verify(post.getLikes(), times(1)).remove(like.getId());
        verify(repository, times(1)).deleteByPostIdAndUserId(postId, userId);
    }

    @DisplayName("Если юзера с таким id не существует")
    @Test
    public void testAddLikeToCommentUserNotFound() {
        LikeDto likeDto = LikeDto.builder()
                .id(1L)
                .userId(1)
                .postId(1)
                .build();
        Comment comment = new Comment();
        comment.setId(1);
        when(commentService.getComment(likeDto.getCommentId())).thenReturn(comment);
        when(userServiceClient.getUser(likeDto.getUserId()))
                .thenThrow(new FeignException.NotFound("User not found", null, null, null));

        assertThrows(FeignException.class, () -> service.addLikeToComment(likeDto));
        verify(repository, times(0)).save(new Like());
    }

    @DisplayName("Когда метод по добавлению лайка на комментарий отработал")
    @Test
    public void testAddLikeToCommentWhenValid() {
        long userId = 1;
        LikeDto likeDto = LikeDto.builder()
                .id(1L)
                .userId(userId)
                .postId(1)
                .build();
        Comment comment = new Comment();
        comment.setId(1);
        Like like = new Like();

        when(commentService.getComment(likeDto.getCommentId())).thenReturn(comment);
        when(userServiceClient.getUser(likeDto.getUserId()))
                .thenReturn(new UserDto(userId, "name", "email@google.com"));
        when(mapper.toLike(likeDto)).thenReturn(like);
        when(repository.findByCommentIdAndUserId(comment.getId(), anyLong())).thenReturn(Optional.empty());

        service.addLikeToComment(likeDto);
        verify(validator, times(1)).validDuplicateLike(Optional.empty());
        verify(comment.getLikes(), times(1)).add(like);
        verify(repository, times(1)).save(like);
    }

    @DisplayName("Если попытка удалить лайк которого не существует ")
    @Test
    public void testDeleteLikeFromCommentLikeNotFound() {
        long commentId = 1;
        long userId = 1;
        when(repository.findByCommentIdAndUserId(commentId, userId))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.deleteLikeFromComment(commentId, userId));
        verify(repository, times(0)).deleteByCommentIdAndUserId(commentId, userId);
    }

    @DisplayName("Когда метод по удалению лайка с комментария отработал")
    @Test
    public void testDeleteLikeFromCommentWhenValid() {
        long commentId = 1;
        long userId = 1;
        Like like = new Like();
        like.setId(1);
        List<Like> likes = Arrays.asList(like);
        Comment comment = new Comment();
        comment.setLikes(likes);

        when(repository.findByCommentIdAndUserId(commentId, userId)).thenReturn(Optional.of(like));
        when(commentService.getComment(commentId)).thenReturn(comment);

        verify(comment.getLikes(), times(1)).remove(like.getId());
        verify(repository, times(1)).deleteByCommentIdAndUserId(commentId, userId);
    }
}
