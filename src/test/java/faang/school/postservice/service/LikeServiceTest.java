package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeCommentRequest;
import faang.school.postservice.dto.like.LikePostRequest;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @InjectMocks
    private LikeService likeService;

    private Post post;
    private Comment comment;
    private UserDto userDto;

    @BeforeEach
    public void init() {
        post = Post.builder()
                .id(1L)
                .authorId(13L)
                .createdAt(LocalDateTime.now())
                .build();
        comment = Comment.builder()
                .id(1L)
                .authorId(144L)
                .createdAt(LocalDateTime.now())
                .build();
        userDto = UserDto.builder()
                .id(1L)
                .username("Bob")
                .build();
    }
    @Test
    public void toggleLikePost_SuccessLike() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(userServiceClient.getUser(1L)).thenReturn(userDto);

        ResponseEntity<?> response = likeService.toggleLikePost(new LikePostRequest(1L, 1L));

        Assertions.assertTrue(response.getStatusCode().is2xxSuccessful());

        ArgumentCaptor<Like> captor = ArgumentCaptor.forClass(Like.class);
        verify(likeRepository).save(captor.capture());
        Like newLike = captor.getValue();

        Assertions.assertTrue(newLike.getUserId() == userDto.id());
        Assertions.assertTrue(newLike.getPost().getId() == post.getId());
    }

    @Test
    public void toggleLikePost_SuccessUnLike() {
        post.setLikes(List.of(
                Like.builder().userId(userDto.id()).build())
        );

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(userServiceClient.getUser(1L)).thenReturn(userDto);

        ResponseEntity<?> response = likeService.toggleLikePost(new LikePostRequest(1L, 1L));

        Assertions.assertTrue(response.getStatusCode().is2xxSuccessful());

        ArgumentCaptor<Long> postIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> userIdCaptor = ArgumentCaptor.forClass(Long.class);
        verify(likeRepository).deleteByPostIdAndUserId(postIdCaptor.capture(), userIdCaptor.capture());

        Assertions.assertEquals(postIdCaptor.getValue(), post.getId());
        Assertions.assertEquals(userIdCaptor.getValue(), userDto.id());
    }

    @Test
    public void toggleLikeComment_SuccessLike() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(userServiceClient.getUser(1L)).thenReturn(userDto);

        ResponseEntity<?> response = likeService.toggleLikeComment(new LikeCommentRequest(1L, 1L));

        Assertions.assertTrue(response.getStatusCode().is2xxSuccessful());

        ArgumentCaptor<Like> captor = ArgumentCaptor.forClass(Like.class);
        verify(likeRepository).save(captor.capture());
        Like newLike = captor.getValue();

        Assertions.assertTrue(newLike.getUserId() == userDto.id());
        Assertions.assertTrue(newLike.getComment().getId() == comment.getId());
    }

    @Test
    public void toggleLikeComment_SuccessUnLike() {
        comment.setLikes(List.of(
                Like.builder().userId(userDto.id()).build())
        );

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(userServiceClient.getUser(1L)).thenReturn(userDto);

        ResponseEntity<?> response = likeService.toggleLikeComment(new LikeCommentRequest(1L, 1L));

        Assertions.assertTrue(response.getStatusCode().is2xxSuccessful());

        ArgumentCaptor<Long> commentIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Long> userIdCaptor = ArgumentCaptor.forClass(Long.class);
        verify(likeRepository).deleteByCommentIdAndUserId(commentIdCaptor.capture(), userIdCaptor.capture());

        Assertions.assertEquals(commentIdCaptor.getValue(), comment.getId());
        Assertions.assertEquals(userIdCaptor.getValue(), userDto.id());
    }

    @Test
    public void toggleLikePost_WrongPostId() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = likeService.toggleLikePost(new LikePostRequest(1L, 1L));

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void toggleLikeComment_WrongCommentId() {
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<?> response = likeService.toggleLikeComment(new LikeCommentRequest(1L, 1L));

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void toggleLikePost_WrongUserId() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(userServiceClient.getUser(1L)).thenReturn(null);

        ResponseEntity<?> response = likeService.toggleLikePost(new LikePostRequest(1L, 1L));

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void toggleLikeComment_WrongUserId() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(userServiceClient.getUser(1L)).thenReturn(null);

        ResponseEntity<?> response = likeService.toggleLikeComment(new LikeCommentRequest(1L, 1L));

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
