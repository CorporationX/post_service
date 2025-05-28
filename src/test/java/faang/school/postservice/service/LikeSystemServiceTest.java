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
class LikeSystemServiceTest {
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
    private LikeSystemService likeSystemService;

    @Test
    void addLikePostTestPostAlreadyHasLiked() {
        long id = 1L;
        Like like = Like.builder()
                .userId(id)
                .build();

        LikeDto dto = LikeDto.builder()
                .userId(id)
                .postId(id)
                .build();

        Post post = Post.builder()
                .id(id)
                .likes(List.of(like))
                .build();

        when(postService.getPostById(dto.postId())).thenReturn(post);

        assertThrows(IllegalArgumentException.class, () -> likeSystemService.addLikePost(dto));
    }

    @Test
    void addLikePostTestCommentWillHaveLikesByUser() {
        long id = 1L;
        long userId = 2L;
        Like like = Like.builder()
                .userId(userId)
                .build();
        LikeDto dto = LikeDto.builder()
                .userId(userId)
                .postId(id)
                .build();
        Comment comment = Comment.builder()
                .likes(List.of(like))
                .build();
        Post post = Post.builder()
                .id(id)
                .likes(new ArrayList<>())
                .comments(List.of(comment))
                .build();

        when(postService.getPostById(dto.postId())).thenReturn(post);

        assertThrows(IllegalArgumentException.class, () -> likeSystemService.addLikePost(dto));
    }

    @Test
    void addLikePostTest() {
        long id = 1L;
        long userId = 2L;
        long userIdNewLike = 3L;
        Like like = Like.builder()
                .userId(userId)
                .build();
        LikeDto dto = LikeDto.builder()
                .userId(userIdNewLike)
                .postId(id)
                .build();
        Comment comment = Comment.builder()
                .likes(List.of(like))
                .build();
        Post post = Post.builder()
                .id(id)
                .likes(new ArrayList<>())
                .comments(List.of(comment))
                .build();

        when(postService.getPostById(dto.postId())).thenReturn(post);

        LikeDto result = likeSystemService.addLikePost(dto);

        verify(likeRepository, times(1)).save(any());
        assertNotNull(result);
        assertEquals(id, result.postId());
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
        Post post = Post.builder()
                .likes(new ArrayList<>())
                .build();
        Like like = Like.builder()
                .id(id)
                .post(post)
                .build();
        post.getLikes().add(like);

        when(likeRepository.findById(id)).thenReturn(Optional.of(like));

        LikeDto result = likeSystemService.deleteLikePost(id);

        verify(likeRepository, times(1)).delete(any());
        assertNotNull(result);
        assertEquals(id, result.id());
    }

    @Test
    void addLikeCommentTestCommentAlreadyHasLiked() {
        long id = 1L;
        Like like = Like.builder()
                .userId(id)
                .build();
        LikeDto dto = LikeDto.builder()
                .userId(id)
                .commentId(id)
                .build();
        Comment comment = Comment.builder()
                .likes(List.of(like))
                .build();

        when(commentService.getCommentById(dto.commentId())).thenReturn(comment);

        assertThrows(IllegalArgumentException.class, () -> likeSystemService.addLikeComment(dto));
    }

    @Test
    void addLikeCommentTestPostWillHaveLikesByUser() {
        long id = 1L;
        long userId = 2L;
        Like like = Like.builder()
                .userId(userId)
                .build();
        LikeDto dto = LikeDto.builder()
                .userId(userId)
                .commentId(id)
                .build();
        Comment comment = Comment.builder()
                .likes(new ArrayList<>())
                .build();
        Post post = Post.builder()
                .likes(List.of(like))
                .comments(List.of(comment))
                .build();
        comment.setPost(post);
        when(commentService.getCommentById(any())).thenReturn(comment);

        assertThrows(IllegalArgumentException.class, () -> likeSystemService.addLikeComment(dto));
    }

    @Test
    void addLikeCommentTest() {
        long id = 1L;

        LikeDto dto = LikeDto.builder()
                .userId(id)
                .commentId(id)
                .build();

        Comment comment = Comment.builder()
                .likes(new ArrayList<>())
                .build();

        Post post = Post.builder()
                .id(id)
                .likes(new ArrayList<>())
                .comments(new ArrayList<>(List.of(comment)))
                .build();

        comment.setPost(post);

        when(commentService.getCommentById(any())).thenReturn(comment);

        LikeDto result = likeSystemService.addLikeComment(dto);

        verify(likeRepository, times(1)).save(any());
        assertNotNull(result);
        assertEquals(id, result.userId());
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
        Comment comment = Comment.builder()
                .likes(new ArrayList<>())
                .build();
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
}