package faang.school.postservice.servise;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.likes.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepositoryAdapter;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.LikeRepositoryAdapter;
import faang.school.postservice.repository.PostRepositoryAdapter;
import faang.school.postservice.service.impl.LikeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LikeServiceImplTest {
    @InjectMocks
    private LikeServiceImpl likeService;
    @Mock
    private PostRepositoryAdapter postRepositoryAdapter;
    @Mock
    private LikeRepositoryAdapter likeRepositoryAdapter;
    @Mock
    private CommentRepositoryAdapter commentRepositoryAdapter;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private LikeMapper likeMapper;
    @Mock
    private UserServiceClient userServiceClient;

    private static final long USER_ID = 1L;
    private static final long POST_ID = 1L;
    private static final long COMMENT_ID = 1L;

    @Test
    public void likePost_shouldSaveLike() {
        Post post = new Post();
        post.setId(POST_ID);
        Like like = new Like();
        like.setUserId(USER_ID);
        like.setPost(post);

        Like savedLike = new Like();

        when(postRepositoryAdapter.findById(POST_ID)).thenReturn(post);
        when(likeRepositoryAdapter.findLikeByPostIdAndUserId(USER_ID, POST_ID)).thenReturn(null);
        when(likeRepository.save(like)).thenReturn(savedLike);
        when(likeMapper.toDto(savedLike)).thenReturn(new LikeDto());

        LikeDto likeDto = likeService.likePost(USER_ID, POST_ID);

        assertNotNull(likeDto);
        verify(likeRepository, times(1)).save(like);
    }

    @Test
    public void deletePostLike_shouldDeleteLike() {
        Like like = new Like();

        when(likeRepositoryAdapter.findLikeByPostIdAndUserId(USER_ID, POST_ID)).thenReturn(like);

        likeService.deletePostLike(USER_ID, POST_ID);

        verify(likeRepository, times(1)).delete(like);
    }

    @Test
    public void likeComment_shouldSaveLike() {
        Comment comment = new Comment();
        comment.setId(COMMENT_ID);
        Like like = new Like();
        like.setUserId(USER_ID);
        like.setComment(comment);

        Like savedLike = new Like();

        when(commentRepositoryAdapter.findById(COMMENT_ID)).thenReturn(comment);
        when(likeRepositoryAdapter.findLikeByCommentIdAndUserId(USER_ID, COMMENT_ID)).thenReturn(null);
        when(likeRepository.save(like)).thenReturn(savedLike);
        when(likeMapper.toDto(savedLike)).thenReturn(new LikeDto());

        LikeDto likeDto = likeService.likeComment(USER_ID, COMMENT_ID);

        assertNotNull(likeDto);
        verify(likeRepository, times(1)).save(like);
    }

    @Test
    public void deleteCommentLike_shouldDeleteLike() {
        Like like = new Like();

        when(likeRepositoryAdapter.findLikeByCommentIdAndUserId(USER_ID, COMMENT_ID)).thenReturn(like);

        likeService.deleteCommentLike(USER_ID, COMMENT_ID);

        verify(likeRepository).delete(like);
    }

    @Test
    void testUsersByPostId() {
        List<Like> likes = Arrays.asList(new Like(), new Like());
        List<UserDto> userDtos = Arrays.asList(
                new UserDto(1L, "test", "test"),
                new UserDto(2L, "test2", "test2"));

        when(likeRepository.findLikesByPostId(POST_ID)).thenReturn(likes);
        when(userServiceClient.getUsersByIds(anyList())).thenReturn(userDtos);

        List<UserDto> result = likeService.usersByPostId(POST_ID);

        assertEquals(2, result.size());
        verify(likeRepository, times(1)).findLikesByPostId(POST_ID);
        verify(userServiceClient, times(1)).getUsersByIds(anyList());
    }

    @Test
    void testUsersByPostId_EmptyLikes() {
        when(likeRepository.findLikesByPostId(POST_ID)).thenReturn(Collections.emptyList());

        List<UserDto> result = likeService.usersByPostId(POST_ID);

        assertTrue(result.isEmpty());
        verify(likeRepository, times(1)).findLikesByPostId(POST_ID);
        verify(userServiceClient, never()).getUsersByIds(anyList());
    }

    @Test
    void testUsersByCommentId() {
        List<Like> likes = Arrays.asList(new Like(), new Like());
        List<UserDto> userDtos = Arrays.asList(
                new UserDto(1L, "User1", "test1"),
                new UserDto(2L, "User2", "test2"));

        when(likeRepository.findLikesByCommentId(COMMENT_ID)).thenReturn(likes);
        when(userServiceClient.getUsersByIds(anyList())).thenReturn(userDtos);

        List<UserDto> result = likeService.usersByCommentId(COMMENT_ID, );

        assertEquals(2, result.size());
        verify(likeRepository, times(1)).findLikesByCommentId(COMMENT_ID);
        verify(userServiceClient, times(1)).getUsersByIds(anyList());
    }

    @Test
    void testUsersByCommentId_EmptyLikes() {
        when(likeRepository.findLikesByCommentId(COMMENT_ID)).thenReturn(Collections.emptyList());

        List<UserDto> result = likeService.usersByCommentId(COMMENT_ID, );

        assertTrue(result.isEmpty());
        verify(likeRepository, times(1)).findLikesByCommentId(COMMENT_ID);
        verify(userServiceClient, never()).getUsersByIds(anyList());
    }
}
