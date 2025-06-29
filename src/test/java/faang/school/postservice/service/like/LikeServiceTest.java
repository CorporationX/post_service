package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.service.comment.CommentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private UserContext userContext;
    @Mock
    private PostService postService;
    @Mock
    private CommentService commentService;
    @InjectMocks
    private LikeServiceImpl likeService;

    @Test
    public void testLikeThePost() {
        Post post = new Post();
        post.setId(1L);
        UserDto mockUser = new UserDto(1L, "name", "email");

        when(userContext.getUserId()).thenReturn(mockUser.id());
        when(userServiceClient.getUser(mockUser.id())).thenReturn(mockUser);
        when(postService.getPostById(post.getId())).thenReturn(post);
        when(likeRepository.findByPostIdAndUserId(post.getId(), mockUser.id())).thenReturn(Optional.empty());

        Like saveLike = new Like();
        saveLike.setId(1L);
        saveLike.setPost(post);
        saveLike.setUserId(mockUser.id());

        when(likeRepository.save(any(Like.class))).thenReturn(saveLike);

        Like resultLike = likeService.likeThePost(post.getId());
        assertNotNull(resultLike);
        assertEquals(saveLike.getId(), resultLike.getId());
        verify(likeRepository).save(any(Like.class));
    }

    @Test
    public void testDeleteLikeThePost(){
        long postId= 1L;
        UserDto mockUser = new UserDto(1L, "name", "email");

        when(userContext.getUserId()).thenReturn(mockUser.id());
        when(userServiceClient.getUser(mockUser.id())).thenReturn(mockUser);

        likeService.deleteLikeThePost(postId);

        verify(likeRepository).deleteByPostIdAndUserId(postId, mockUser.id());
    }

    @Test
    public void testGetAllTheLikeForPost(){
        long postId = 1L;
        Post post = new Post();
        Like like = new Like();
        Like likeSecond = new Like();
        like.setId(1L);
        likeSecond.setId(2L);
        post.setLikes(Arrays.asList(like, likeSecond));

        when(postService.getPostById(postId)).thenReturn(post);

        List<Like> likes = likeService.getAllTheLikeForPost(postId);

        assertNotNull(likes);
        assertEquals(2, likes.size());
        assertEquals(1L, likes.get(0).getId());
        assertEquals(2L, likes.get(1).getId());
    }

    @Test
    public void testLikeTheComment() {
        Comment comment = new Comment();
        comment.setId(1L);
        UserDto mockUser = new UserDto(1L, "name", "email");

        when(userContext.getUserId()).thenReturn(mockUser.id());
        when(userServiceClient.getUser(mockUser.id())).thenReturn(mockUser);
        when(commentService.getComment(comment.getId())).thenReturn(comment);
        when(likeRepository.findByCommentIdAndUserId(comment.getId(), mockUser.id())).thenReturn(Optional.empty());

        Like saveLike = new Like();
        saveLike.setId(1L);
        saveLike.setComment(comment);
        saveLike.setUserId(mockUser.id());

        when(likeRepository.save(any(Like.class))).thenReturn(saveLike);

        Like resultLike = likeService.likeTheComment(comment.getId());
        assertNotNull(resultLike);
        assertEquals(saveLike.getId(), resultLike.getId());
        verify(likeRepository).save(any(Like.class));
    }
}
