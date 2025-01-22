package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.exception.UserNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import feign.FeignException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {

    @InjectMocks
    private LikeService likeService;

    @Mock
    private LikeRepository likeRepository;

    @Mock
    private LikeValidationService likeValidationService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private CommentRepository commentRepository;

    @Test
    public void addLikeToPostTest(){
        Long postId = 1L;
        Long userId = 1L;
        Long commentId = 1L;
        Post post = new Post();
        Like like = Like.builder()
                .userId(userId)
                .post(post)
                .build();
        Mockito.when(postRepository.findById(postId))
                        .thenReturn(Optional.of(post));
        likeService.addLikeToPost(postId, userId, commentId);
        Mockito.verify(postRepository, Mockito.times(1)).save(post);
        Mockito.verify(likeRepository, Mockito.times(1)).save(like);
    }

    @Test
    public void addLikeToPost_throwsUserNotFoundException_whenUserNotExists() {
        Long postId = 1L;
        Long userId = 1L;

        Mockito.when(userServiceClient.getUser(userId)).thenThrow(FeignException.NotFound.class);

        Assertions.assertThrows(UserNotFoundException.class, () -> {
            likeService.addLikeToPost(postId, null, userId);
        });
    }

    @Test
    public void removeLikeFromPostTest(){
        Long postId = 1L;
        Long userId = 1L;
        likeService.removeLikeFromPost(postId, userId);
        Mockito.verify(likeRepository, Mockito.times(1)).deleteByUserIdAndPostId(postId,userId);
    }

    @Test
    public void addLikeToCommentTest(){
        Long postId = 1L;
        Long userId = 1L;
        Long commentId = 1L;
        Comment comment = new Comment();
        Like like = Like.builder()
                .userId(userId)
                .comment(comment)
                .build();
        Mockito.when(commentRepository.findById(postId))
                .thenReturn(Optional.of(comment));
        likeService.addLikeToComment(postId, userId, commentId);
        Mockito.verify(commentRepository, Mockito.times(1)).save(comment);
        Mockito.verify(likeRepository, Mockito.times(1)).save(like);
    }

    @Test
    public void addLikeToComment_throwsUserNotFoundException_whenUserNotExists() {
        Long postId = 1L;
        Long userId = 1L;

        Mockito.when(userServiceClient.getUser(userId)).thenThrow(FeignException.NotFound.class);

        Assertions.assertThrows(UserNotFoundException.class, () -> {
            likeService.addLikeToComment(postId, null, userId);
        });
    }

    @Test
    public void removeLikeFromCommentTest(){
        Long commentId = 1L;
        Long userId = 1L;
        likeService.removeLikeFromComment(commentId, userId);
        Mockito.verify(likeRepository, Mockito.times(1)).deleteByUserIdAndCommentId(commentId,userId);
    }

}