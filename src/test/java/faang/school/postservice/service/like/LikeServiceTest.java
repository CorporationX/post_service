package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.event.LikeEvent;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.event.EventType;
import faang.school.postservice.publisher.KafkaLikePublisher;
import faang.school.postservice.publisher.LikePublisher;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {

    @InjectMocks
    LikeService likeService;

    @Mock
    LikeRepository likeRepository;

    @Mock
    PostRepository postRepository;

    @Mock
    CommentRepository commentRepository;

    @Mock
    UserServiceClient userServiceClient;

    @Mock
    LikePublisher likePublisher;

    @Mock
    KafkaLikePublisher kafkaLikePublisher;

    @Captor
    ArgumentCaptor<LikeEvent> likeEventCaptor;



    private final long userId = 1L;
    private final long postId = 2L;
    private final long commentId = 3L;
    private final long authorId = 1L;
    private final UserDto userDto = UserDto.builder().id(userId).build();
    private final Post post = Post.builder().id(postId).authorId(authorId).build();
    private final Like likePost = Like.builder()
            .userId(userId)
            .post(post)
            .build();
    private final Optional<Like> likePostOptional = Optional.of(Like.builder().userId(userId).post(post).build());
    private final Optional<Post> postOptional = Optional.of(post);
    private final List<Like> likesComment = List.of(
            Like.builder().userId(userId).build(),
            Like.builder().userId(11L).build()
    );
    private final Comment comment = Comment.builder().id(commentId).post(post).likes(likesComment).build();
    private final Like likeComment = Like.builder()
            .userId(userId)
            .comment(comment)
            .build();
    private final Optional<Comment> commentOptional = Optional.of(comment);
    private final Optional<Like> likeCommentOptional = Optional.of(Like.builder().userId(userId).comment(comment).build());
    private final List<Comment> likedComment = List.of(
            comment,
            Comment.builder().build()
    );


    @Test
    void likeThePost_shouldLikePost() {
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(postRepository.findById(postId)).thenReturn(postOptional);
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.empty());
        when(commentRepository.findAllByPostId(postId)).thenReturn(List.of());

        likeService.likeThePost(postId, userId);

        verify(likeRepository, times(1)).save(any(Like.class));
        verify(kafkaLikePublisher, times(1)).publish(likeEventCaptor.capture());
        LikeEvent actualEvent = likeEventCaptor.getValue();

        assertEquals(userId, actualEvent.getUserId());
        assertEquals(postId, actualEvent.getPostId());
        assertEquals(authorId, actualEvent.getAuthorId());
        assertEquals(EventType.LIKED_POST, actualEvent.getType());
        assertNotNull(actualEvent.getLikedAt());

    }

    @Test
    void likeThePost_shouldThrowExceptionWhenUserNotFindById() {
        when(userServiceClient.getUser(userId)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> likeService.likeThePost(postId, userId));
    }

    @Test
    void likeThePost_shouldThrowExceptionWhenPostNotFindById() {
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> likeService.likeThePost(postId, userId));
    }

    @Test
    void likeThePost_shouldThrowExceptionWhenPostWasLiked() {
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(postRepository.findById(postId)).thenReturn(postOptional);
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(likePostOptional);

        assertThrows(EntityExistsException.class, () -> likeService.likeThePost(postId, userId));
    }

    @Test
    void likeThePost_shouldThrowExceptionWhenCommentWasLiked() {
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(postRepository.findById(postId)).thenReturn(postOptional);
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.empty());
        when(commentRepository.findAllByPostId(postId)).thenReturn(likedComment);
        when(likeRepository.findByCommentIdAndUserId(comment.getId(), userId)).thenReturn(likeCommentOptional);

        assertThrows(EntityExistsException.class, () -> likeService.likeThePost(postId, userId));
    }

    @Test
    void likeThePost_shouldLikeComment() {
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(commentRepository.findById(commentId)).thenReturn(commentOptional);
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.empty());
        when(likeRepository.findByCommentIdAndUserId(comment.getId(), userId)).thenReturn(Optional.empty());

        likeService.likeTheComment(commentId, userId);

        Mockito.verify(likeRepository, Mockito.times(1)).save(likeComment);
    }

    @Test
    void likeTheComment_shouldThrowExceptionWhenUserNotFindById() {
        when(userServiceClient.getUser(userId)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> likeService.likeTheComment(commentId, userId));
    }

    @Test
    void likeTheComment_shouldThrowExceptionWhenCommentNotFindById() {
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> likeService.likeTheComment(commentId, userId));
    }

    @Test
    void likeTheComment_shouldThrowExceptionWhenPostWasLiked() {
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(commentRepository.findById(commentId)).thenReturn(commentOptional);
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(likePostOptional);

        assertThrows(EntityExistsException.class, () -> likeService.likeTheComment(commentId, userId));
    }

    @Test
    void likeTheComment_shouldThrowExceptionWhenCommentWasLiked() {
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(commentRepository.findById(commentId)).thenReturn(commentOptional);
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.empty());
        when(likeRepository.findByCommentIdAndUserId(comment.getId(), userId)).thenReturn(likeCommentOptional);

        assertThrows(EntityExistsException.class, () -> likeService.likeTheComment(commentId, userId));
    }

    @Test
    void removeLikeFromPost_shouldRemoveLike() {
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(postRepository.findById(postId)).thenReturn(postOptional);

        likeService.removeLikeFromPost(postId, userId);

        Mockito.verify(likeRepository, Mockito.times(1)).deleteByPostIdAndUserId(postId, userId);
    }

    @Test
    void removeLikeFromPost_shouldThrowExceptionWhenUserNotFindById() {
        when(userServiceClient.getUser(userId)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> likeService.removeLikeFromPost(postId, userId));
    }

    @Test
    void removeLikeFromPost_shouldThrowExceptionWhenPostNotFindById() {
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> likeService.removeLikeFromPost(postId, userId));
    }

    @Test
    void removeLikeFromComment_shouldRemoveLike() {
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(commentRepository.findById(commentId)).thenReturn(commentOptional);

        likeService.removeLikeFromComment(commentId, userId);

        Mockito.verify(likeRepository, Mockito.times(1)).deleteByCommentIdAndUserId(commentId, userId);
    }

    @Test
    void removeLikeFromComment_shouldThrowExceptionWhenUserNotFindById() {
        when(userServiceClient.getUser(userId)).thenReturn(null);

        assertThrows(EntityNotFoundException.class, () -> likeService.removeLikeFromComment(commentId, userId));
    }

    @Test
    void removeLikeFromComment_shouldThrowExceptionWhenCommentNotFindById() {
        when(userServiceClient.getUser(userId)).thenReturn(userDto);
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> likeService.removeLikeFromComment(commentId, userId));
    }
}
