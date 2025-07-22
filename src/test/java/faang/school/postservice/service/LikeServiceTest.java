package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.kafka.KafkaProducerService;
import faang.school.postservice.dto.likesystem.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.LikeMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.LongStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private CommentService commentService;
    @Mock
    private PostService postService;
    @Mock
    private UserServiceClient userServiceClient;
    @Spy
    private LikeMapperImpl likeMapper;
    @Mock
    private KafkaProducerService kafkaProducerService;
    @InjectMocks
    private LikeService likeSystemService;

    @Test
    void addLikePostTestPostAlreadyHasLiked() {
        final long id = 1L;
        final long userId = 2L;
        final Like like = createLikeWithUserId(userId);

        final Post post = createPostWithLikes(List.of(like));
        when(postService.getPostById(any())).thenReturn(post);

        assertThrows(IllegalArgumentException.class, () -> likeSystemService.addLikePost(id, userId));
    }

    @Test
    void addLikePostTest() {
        final long postId = 1L;
        final long userId = 2L;
        final long authorId = 3L;
        final long userIdNewLike = 3L;

        final Like like = createLikeWithUserId(userId);
        final Comment comment = createCommentWithLikes(List.of(like));
        final Post post = createPostWithPostIdLikesCommentsAndAuthor(postId,
                new ArrayList<>(), List.of(comment), authorId);

        when(postService.getPostById(any())).thenReturn(post);

        final LikeDto result = likeSystemService.addLikePost(postId, userIdNewLike);
        assertNotNull(result);
        assertEquals(postId, result.postId());
        verify(likeRepository, times(1)).save(any());
    }

    @Test
    void deleteLikePostLikeNotExist() {
        final long id = -1L;

        when(likeRepository.findById(id))
                .thenThrow(new IllegalArgumentException("The like with id = " + id + " does not exist"));

        assertThrows(IllegalArgumentException.class, () -> likeSystemService.deleteLikePost(id));
    }

    @Test
    void deleteLikePostLikeExist() {
        final long id = 1L;
        final Post post = createPostWithLikes(new ArrayList<>());
        final Like like = Like.builder()
                .id(id)
                .post(post)
                .build();
        post.getLikes().add(like);

        when(likeRepository.findById(any())).thenReturn(Optional.of(like));

        final LikeDto result = likeSystemService.deleteLikePost(id);
        assertNotNull(result);
        assertEquals(id, result.id());
        verify(likeRepository, times(1)).delete(any());
    }

    @Test
    void addLikeCommentTestCommentAlreadyHasLiked() {
        final long id = 1L;
        final long userId = 2L;

        final Like like = createLikeWithUserId(userId);
        final Comment comment = createCommentWithLikes(List.of(like));
        when(commentService.getCommentById(id)).thenReturn(comment);

        assertThrows(IllegalArgumentException.class, () -> likeSystemService.addLikeComment(id, userId));
    }

    @Test
    void addLikeCommentTest() {
        final long id = 1L;
        final long userId = 2L;
        final long authorId = 3L;

        final Comment comment = createCommentWithLikes(new ArrayList<>());
        final Post post = createPostWithPostIdLikesCommentsAndAuthor(id, new ArrayList<>(), List.of(comment), authorId);
        comment.setPost(post);

        when(commentService.getCommentById(any())).thenReturn(comment);

        final LikeDto result = likeSystemService.addLikeComment(id, userId);
        assertNotNull(result);
        assertEquals(userId, result.userId());
        verify(likeRepository, times(1)).save(any());
    }

    @Test
    void deleteLikeCommentLikeNotExist() {
        final long id = -1L;

        when(likeRepository.findById(id))
                .thenThrow(new IllegalArgumentException("The like with id = " + id + " does not exist"));

        assertThrows(IllegalArgumentException.class, () -> likeSystemService.deleteLikeComment(id));
    }

    @Test
    void deleteLikeCommentLikeExist() {
        final long id = 1L;
        final Comment comment = createCommentWithLikes(new ArrayList<>());
        final Like like = Like.builder()
                .id(id)
                .comment(comment)
                .build();
        comment.getLikes().add(like);

        when(likeRepository.findById(id)).thenReturn(Optional.of(like));

        final LikeDto result = likeSystemService.deleteLikeComment(id);
        assertNotNull(result);
        assertEquals(id, result.id());
        verify(likeRepository, times(1)).delete(any());
    }

    @Test
    void getUsersWhoLikedPost_batchesCorrectly() {
        final List<Like> likes = LongStream.rangeClosed(1, 150)
                .mapToObj(id -> Like.builder().userId(id).build())
                .toList();

        when(likeRepository.findByPostId(42L)).thenReturn(likes);

        final List<UserDto> batch1 = List.of(
                new UserDto(1L, "User1", "u1@test.com"),
                new UserDto(2L, "User2", "u2@test.com")
        );
        final List<UserDto> batch2 = List.of(
                new UserDto(101L, "User101", "u101@test.com")
        );

        when(userServiceClient.getUsersByIds(likes.subList(0, 100)
                .stream()
                .map(Like::getUserId)
                .toList()))
                .thenReturn(batch1);
        when(userServiceClient.getUsersByIds(likes.subList(100, 150)
                .stream()
                .map(Like::getUserId)
                .toList()))
                .thenReturn(batch2);

        final List<UserDto> result = likeSystemService.getUsersWhoLikedPost(42L);
        assertEquals(3, result.size());
        verify(likeRepository, times(1)).findByPostId(42L);
        verify(userServiceClient, times(1))
                .getUsersByIds(likes.subList(0, 100)
                        .stream()
                        .map(Like::getUserId)
                        .toList());
        verify(userServiceClient, times(1))
                .getUsersByIds(likes.subList(100, 150)
                .stream()
                .map(Like::getUserId)
                .toList());
    }

    @Test
    void getUsersWhoLikedComment_returnsUsers() {
        final List<Like> likes = List.of(
                Like.builder().userId(10L).build(),
                Like.builder().userId(20L).build()
        );
        when(likeRepository.findByCommentId(99L))
                .thenReturn(likes);

        final List<UserDto> users = List.of(
                new UserDto(10L, "Alice", "a@test.com"),
                new UserDto(20L, "Bob", "b@test.com")
        );
        when(userServiceClient.getUsersByIds(List.of(10L, 20L)))
                .thenReturn(users);

        final List<UserDto> result = likeSystemService.getUsersWhoLikedComment(99L);
        assertEquals(2, result.size());
        assertEquals("Alice", result.get(0).username());
        assertEquals("Bob", result.get(1).username());
        verify(likeRepository, times(1))
                .findByCommentId(99L);
        verify(userServiceClient, times(1))
                .getUsersByIds(List.of(10L, 20L));
    }

    private Like createLikeWithUserId(Long userId) {
        return Like.builder()
                .userId(userId)
                .build();
    }

    private Comment createCommentWithLikes(List<Like> likes) {
        return Comment.builder()
                .likes(likes)
                .build();
    }

    private Post createPostWithLikes(List<Like> likes) {
        return Post.builder()
                .likes(likes)
                .build();
    }

    private Post createPostWithPostIdLikesCommentsAndAuthor(Long postId,
                                                            List<Like> likes,
                                                            List<Comment> comments,
                                                            Long authorId) {
        return Post.builder()
                .id(postId)
                .likes(likes)
                .comments(comments)
                .authorId(authorId)
                .build();
    }
}
