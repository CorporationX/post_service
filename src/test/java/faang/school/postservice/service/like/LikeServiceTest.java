package faang.school.postservice.service.like;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.LikeMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.util.client.UserServiceClientAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {

    @Mock
    private UserContext userContext;
    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private UserServiceClientAdapter userServiceClientAdapter;
    @Spy
    private LikeMapperImpl likeMapper;
    @Captor
    private ArgumentCaptor<Like> likeArgumentCaptor;
    @InjectMocks
    private LikeServiceImpl likeService;

    @Test
    public void testAddLikeToPostAndUserNotFound() {
        final long postId = 5L;
        final long userId = 2L;

        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClientAdapter.getUserById(userId)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> likeService.addLikeToPost(postId));

        verify(userContext, times(1)).getUserId();
        verify(userServiceClientAdapter, times(1)).getUserById(userId);
        verify(postRepository, never()).findById(anyLong());
        verify(likeRepository, never()).save(any(Like.class));
        verify(likeMapper, never()).toLikeDto(any(Like.class));
    }

    @Test
    public void testAddLikeToPostAndPostNotFound() {
        final long postId = 5L;
        final long userId = 2L;
        final UserDto userDto = initUser(userId);

        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClientAdapter.getUserById(userId)).thenReturn(userDto);
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> likeService.addLikeToPost(postId));

        verify(userContext, times(1)).getUserId();
        verify(userServiceClientAdapter, times(1)).getUserById(userId);
        verify(postRepository, times(1)).findById(postId);
        verify(likeRepository, never()).save(any(Like.class));
        verify(likeMapper, never()).toLikeDto(any(Like.class));
    }

    @Test
    public void testAddLikeToPostSuccessful() {
        final long postId = 5L;
        final long userId = 2L;
        final long likeId = 1L;
        final UserDto userDto = initUser(userId);
        final Post currentPost = initPost(postId);

        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClientAdapter.getUserById(userId)).thenReturn(userDto);
        when(postRepository.findById(postId)).thenReturn(Optional.of(currentPost));
        when(likeRepository.save(likeArgumentCaptor.capture()))
                .thenAnswer(invocation -> {
                    Like like = likeArgumentCaptor.getValue();
                    like.setId(likeId);
                    like.setCreatedAt(LocalDateTime.now());
                    return like;
                });

        LikeDto likeDtoResult = likeService.addLikeToPost(postId);

        assertNotNull(likeDtoResult);
        assertEquals(likeId, likeDtoResult.id());
        assertEquals(userId, likeDtoResult.userId());
        assertEquals(postId, likeDtoResult.postId());
        assertNull(likeDtoResult.commentId());
        assertNotNull(likeDtoResult.createdAt());

        verify(userContext, times(1)).getUserId();
        verify(userServiceClientAdapter, times(1)).getUserById(userId);
        verify(postRepository, times(1)).findById(postId);
        verify(likeRepository, times(1)).save(likeArgumentCaptor.capture());
        verify(likeMapper, times(1)).toLikeDto(any(Like.class));
    }

    @Test
    public void testRemoveLikeFromPostAndLikeNotFound() {
        final long postId = 5L;
        final long userId = 2L;

        when(userContext.getUserId()).thenReturn(userId);
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> likeService.removeLikeFromPost(postId));

        verify(userContext, times(1)).getUserId();
        verify(likeRepository, times(1)).findByPostIdAndUserId(postId, userId);
        verify(likeRepository, never()).deleteByPostIdAndUserId(anyLong(), anyLong());
    }

    @Test
    public void testRemoveLikeFromPostSuccessful() {
        final long postId = 5L;
        final long userId = 2L;
        final Like currentLike = initLike(userId);

        when(userContext.getUserId()).thenReturn(userId);
        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.of(currentLike));
        doNothing().when(likeRepository).deleteByPostIdAndUserId(postId, userId);

        likeService.removeLikeFromPost(postId);

        verify(userContext, times(1)).getUserId();
        verify(likeRepository, times(1)).findByPostIdAndUserId(postId, userId);
        verify(likeRepository, times(1)).deleteByPostIdAndUserId(postId, userId);
    }

    @Test
    public void testAddLikeToCommentAndUserNotFound() {
        final long commentId = 7L;
        final long userId = 2L;

        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClientAdapter.getUserById(userId)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> likeService.addLikeToComment(commentId));

        verify(userContext, times(1)).getUserId();
        verify(userServiceClientAdapter, times(1)).getUserById(userId);
        verify(commentRepository, never()).findById(commentId);
        verify(likeRepository, never()).save(any(Like.class));
        verify(likeMapper, never()).toLikeDto(any(Like.class));
    }

    @Test
    public void testAddLikeToCommentAndCommentNotFound() {
        final long commentId = 7L;
        final long userId = 2L;
        final UserDto userDto = initUser(userId);

        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClientAdapter.getUserById(userId)).thenReturn(userDto);
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> likeService.addLikeToComment(commentId));

        verify(userContext, times(1)).getUserId();
        verify(userServiceClientAdapter, times(1)).getUserById(userId);
        verify(commentRepository, times(1)).findById(commentId);
        verify(likeRepository, never()).save(any(Like.class));
        verify(likeMapper, never()).toLikeDto(any(Like.class));
    }

    @Test
    public void testAddLikeToCommentSuccessful() {
        final long commentId = 7L;
        final long userId = 2L;
        final long likeId = 1L;
        final UserDto userDto = initUser(userId);
        final Comment currentComment = initComment(commentId);

        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClientAdapter.getUserById(userId)).thenReturn(userDto);
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(currentComment));
        when(likeRepository.save(likeArgumentCaptor.capture()))
                .thenAnswer(invocation -> {
                    Like like = likeArgumentCaptor.getValue();
                    like.setId(likeId);
                    like.setCreatedAt(LocalDateTime.now());
                    return like;
                });

        LikeDto likeDtoResult = likeService.addLikeToComment(commentId);

        assertNotNull(likeDtoResult);
        assertEquals(likeId, likeDtoResult.id());
        assertEquals(userId, likeDtoResult.userId());
        assertNull(likeDtoResult.postId());
        assertEquals(commentId, likeDtoResult.commentId());
        assertNotNull(likeDtoResult.createdAt());

        verify(userContext, times(1)).getUserId();
        verify(userServiceClientAdapter, times(1)).getUserById(userId);
        verify(commentRepository, times(1)).findById(commentId);
        verify(likeRepository, times(1)).save(likeArgumentCaptor.capture());
        verify(likeMapper, times(1)).toLikeDto(any(Like.class));
    }

    @Test
    public void testRemoveLikeFromCommentAndLikeNotFound() {
        final long commentId = 7L;
        final long userId = 2L;

        when(userContext.getUserId()).thenReturn(userId);
        when(likeRepository.findByCommentIdAndUserId(commentId, userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> likeService.removeLikeFromComment(commentId));

        verify(userContext, times(1)).getUserId();
        verify(likeRepository, times(1)).findByCommentIdAndUserId(commentId, userId);
        verify(likeRepository, never()).deleteByCommentIdAndUserId(commentId, userId);
    }

    @Test
    public void testRemoveLikeFromCommentSuccessful() {
        final long commentId = 5L;
        final long userId = 2L;
        final Like currentLike = initLike(userId);

        when(userContext.getUserId()).thenReturn(userId);
        when(likeRepository.findByCommentIdAndUserId(commentId, userId)).thenReturn(Optional.of(currentLike));
        doNothing().when(likeRepository).deleteByCommentIdAndUserId(commentId, userId);

        likeService.removeLikeFromComment(commentId);

        verify(userContext, times(1)).getUserId();
        verify(likeRepository, times(1)).findByCommentIdAndUserId(commentId, userId);
        verify(likeRepository, times(1)).deleteByCommentIdAndUserId(commentId, userId);
    }

    private UserDto initUser(long userId) {
        return UserDto.builder()
                .id(userId)
                .build();
    }

    private Like initLike(long userId) {
        return Like.builder()
                .userId(userId)
                .build();
    }

    private Post initPost(long postId) {
        return Post.builder()
                .id(postId)
                .build();
    }

    private Comment initComment(long commentId) {
        return Comment.builder()
                .id(commentId)
                .build();
    }
}
