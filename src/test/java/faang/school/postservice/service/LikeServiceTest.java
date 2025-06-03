package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.likesystem.LikeDto;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


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
    @InjectMocks
    private LikeService likeSystemService;

    @Test
    void addLikePostTestPostAlreadyHasLiked() {
        long id = 1L;
        long userId = 2L;
        Like like = createLikeWithUserId(userId);

        Post post = createPostWithLikes(List.of(like));

        when(postService.getPostById(any())).thenReturn(post);

        assertThrows(IllegalArgumentException.class, () -> likeSystemService.addLikePost(id, userId));
    }

    @Test
    void addLikePostTest() {
        long postId = 1L;
        long userId = 2L;
        long userIdNewLike = 3L;

        Like like = createLikeWithUserId(userId);

        Comment comment = createCommentWithLikes(List.of(like));
        Post post = createPostWithPostIdLikesAndComments(postId, new ArrayList<>(), List.of(comment));

        when(postService.getPostById(any())).thenReturn(post);

        LikeDto result = likeSystemService.addLikePost(postId, userIdNewLike);

        verify(likeRepository, times(1)).save(any());
        assertNotNull(result);
        assertEquals(postId, result.postId());
    }

    @Test
    void deleteLikePostLikeNotExist() {
        long id = -1L;

        when(likeRepository.findById(id))
                .thenThrow(new IllegalArgumentException("The like with id = " + id + " does not exist"));

        assertThrows(IllegalArgumentException.class, () -> likeSystemService.deleteLikePost(id));
    }

    @Test
    void deleteLikePostLikeExist() {
        long id = 1L;
        Post post = createPostWithLikes(new ArrayList<>());
        Like like = Like.builder()
                .id(id)
                .post(post)
                .build();
        post.getLikes().add(like);

        when(likeRepository.findById(any())).thenReturn(Optional.of(like));

        LikeDto result = likeSystemService.deleteLikePost(id);

        verify(likeRepository, times(1)).delete(any());
        assertNotNull(result);
        assertEquals(id, result.id());
    }

    @Test
    void addLikeCommentTestCommentAlreadyHasLiked() {
        long id = 1L;
        long userId = 2L;

        Like like = createLikeWithUserId(userId);
        Comment comment = createCommentWithLikes(List.of(like));

        when(commentService.getCommentById(id)).thenReturn(comment);

        assertThrows(IllegalArgumentException.class, () -> likeSystemService.addLikeComment(id, userId));
    }

    @Test
    void addLikeCommentTest() {
        long id = 1L;
        long userId = 2L;

        Comment comment = createCommentWithLikes(new ArrayList<>());
        Post post = createPostWithPostIdLikesAndComments(id, new ArrayList<>(), List.of(comment));
        comment.setPost(post);

        when(commentService.getCommentById(any())).thenReturn(comment);

        LikeDto result = likeSystemService.addLikeComment(id, userId);

        verify(likeRepository, times(1)).save(any());
        assertNotNull(result);
        assertEquals(userId, result.userId());
    }

    @Test
    void deleteLikeCommentLikeNotExist() {
        long id = -1L;

        when(likeRepository.findById(id))
                .thenThrow(new IllegalArgumentException("The like with id = " + id + " does not exist"));

        assertThrows(IllegalArgumentException.class, () -> likeSystemService.deleteLikeComment(id));
    }

    @Test
    void deleteLikeCommentLikeExist() {
        long id = 1L;
        Comment comment = createCommentWithLikes(new ArrayList<>());
        Like like = Like.builder()
                .id(id)
                .comment(comment)
                .build();
        comment.getLikes().add(like);

        when(likeRepository.findById(id)).thenReturn(Optional.of(like));

        LikeDto result = likeSystemService.deleteLikeComment(id);

        verify(likeRepository, times(1)).delete(any());
        assertNotNull(result);
        assertEquals(id, result.id());
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

    private Post createPostWithPostIdLikesAndComments(Long postId, List<Like> likes, List<Comment> comments) {
        return Post.builder()
                .id(postId)
                .likes(likes)
                .comments(comments)
                .build();
    }
}