package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeResponseDto;
import faang.school.postservice.event.PostLikeEvent;
import faang.school.postservice.exception.AlreadyExistsException;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.mapper.LikeMapperImpl;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.LikeEventPublisher;
import faang.school.postservice.repository.LikeRepository;
import feign.FeignException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @InjectMocks
    private LikeService likeService;

    @Spy
    private LikeMapperImpl likeMapper;

    @Mock
    private LikeRepository likeRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private PostService postService;
    @Mock
    private CommentService commentService;
    @Mock
    private LikeEventPublisher likeEventPublisher;

    @Test
    void testAddLikeToPost() {
        long userId = 1L;
        long postId = 11L;
        Post post = Post.builder()
                .id(postId)
                .build();

        when(postService.getById(postId)).thenReturn(post);
        when(likeRepository.existsByPostIdAndUserId(postId, userId)).thenReturn(false);
        when(likeRepository.save(any(Like.class)))
                .then(invocationOnMock -> invocationOnMock.getArgument(0, Like.class));

        LikeResponseDto likeResponseDto = likeService.addLikeToPost(userId, postId);

        assertEquals(userId, likeResponseDto.getUserId());
        assertEquals(postId, likeResponseDto.getPostId());
        assertNull(likeResponseDto.getCommentId());
        verify(postService, times(1)).getById(postId);
        verify(userServiceClient, times(1)).getUser(userId);
        verify(likeRepository, times(1)).existsByPostIdAndUserId(postId, userId);
        verify(likeEventPublisher, times(1)).publish(any(PostLikeEvent.class));
        verify(likeRepository, times(1)).save(any(Like.class));
    }

    @Test
    void testAddLikeToPost_userNotExists_throws() {
        long userId = 1L;
        long postId = 11L;
        Post post = Post.builder()
                .id(postId)
                .build();

        FeignException.NotFound mockedException = Mockito.mock(FeignException.NotFound.class);
        when(postService.getById(postId)).thenReturn(post);
        when(userServiceClient.getUser(userId)).thenThrow(mockedException);

        assertThrows(NotFoundException.class, () -> likeService.addLikeToPost(userId, postId));
    }

    @Test
    void testAddLikeToPost_alreadyLiked_throws() {
        long userId = 1L;
        long postId = 11L;
        Post post = Post.builder()
                .id(postId)
                .build();

        when(postService.getById(postId)).thenReturn(post);
        when(likeRepository.existsByPostIdAndUserId(postId, userId)).thenReturn(true);

        assertThrows(AlreadyExistsException.class, () -> likeService.addLikeToPost(userId, postId));
    }

    @Test
    void testAddLikeToComment() {
        long userId = 1L;
        long commentId = 11L;
        Comment comment = Comment.builder()
                .id(commentId)
                .build();

        when(commentService.getById(commentId)).thenReturn(comment);
        when(likeRepository.existsByCommentIdAndUserId(commentId, userId)).thenReturn(false);
        when(likeRepository.save(any(Like.class)))
                .then(invocationOnMock -> invocationOnMock.getArgument(0, Like.class));

        LikeResponseDto likeResponseDto = likeService.addLikeToComment(userId, commentId);

        assertEquals(userId, likeResponseDto.getUserId());
        assertEquals(commentId, likeResponseDto.getCommentId());
        assertNull(likeResponseDto.getPostId());
        verify(commentService, times(1)).getById(commentId);
        verify(userServiceClient, times(1)).getUser(userId);
        verify(likeRepository, times(1)).existsByCommentIdAndUserId(commentId, userId);
        verify(likeRepository, times(1)).save(any(Like.class));
    }

    @Test
    void testAddLikeToComment_userNotExists_throws() {
        long userId = 1L;
        long commentId = 11L;
        Comment comment = Comment.builder()
                .id(commentId)
                .build();

        FeignException.NotFound mockedException = Mockito.mock(FeignException.NotFound.class);
        when(commentService.getById(commentId)).thenReturn(comment);
        when(userServiceClient.getUser(userId)).thenThrow(mockedException);

        assertThrows(NotFoundException.class, () -> likeService.addLikeToComment(userId, commentId));
    }

    @Test
    void testAddLikeToComment_alreadyLiked_throws() {
        long userId = 1L;
        long commentId = 11L;
        Comment comment = Comment.builder()
                .id(commentId)
                .build();

        when(commentService.getById(commentId)).thenReturn(comment);
        when(likeRepository.existsByCommentIdAndUserId(commentId, userId)).thenReturn(true);

        assertThrows(AlreadyExistsException.class, () -> likeService.addLikeToComment(userId, commentId));
    }

    @Test
    void testGetByPostIdAndUserId() {
        long userId = 1L;
        long postId = 11L;
        Like like = Like.builder()
                .userId(userId)
                .post(Post.builder()
                        .id(postId)
                        .build())
                .build();

        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.of(like));

        Like result = likeService.getByPostIdAndUserId(postId, userId);
        assertEquals(like, result);
        verify(likeRepository, times(1)).findByPostIdAndUserId(postId, userId);
    }

    @Test
    void testGetByPostIdAndUserId_notExists_throws() {
        long userId = 1L;
        long postId = 11L;

        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> likeService.getByPostIdAndUserId(postId, userId));
        verify(likeRepository, times(1)).findByPostIdAndUserId(postId, userId);
    }

    @Test
    void testGetByCommentIdAndUserId() {
        long userId = 1L;
        long commentId = 11L;
        Like like = Like.builder()
                .userId(userId)
                .comment(Comment.builder()
                        .id(commentId)
                        .build())
                .build();

        when(likeRepository.findByCommentIdAndUserId(commentId, userId)).thenReturn(Optional.of(like));

        Like result = likeService.getByCommentIdAndUserId(commentId, userId);
        assertEquals(like, result);
        verify(likeRepository, times(1)).findByCommentIdAndUserId(commentId, userId);
    }

    @Test
    void testGetByCommentIdAndUserId_notExists_throws() {
        long userId = 1L;
        long commentId = 11L;

        when(likeRepository.findByCommentIdAndUserId(commentId, userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> likeService.getByCommentIdAndUserId(commentId, userId));
        verify(likeRepository, times(1)).findByCommentIdAndUserId(commentId, userId);
    }

    @Test
    void testRemoveLikeFromPost() {
        long userId = 1L;
        long postId = 11L;
        Like like = Like.builder()
                .userId(userId)
                .post(Post.builder()
                        .id(postId)
                        .build())
                .build();

        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.of(like));

        LikeResponseDto responseDto = likeService.removeLikeFromPost(userId, postId);

        assertEquals(userId, responseDto.getUserId());
        assertEquals(postId, responseDto.getPostId());
        verify(likeRepository, times(1)).findByPostIdAndUserId(postId, userId);
        verify(likeRepository, times(1)).delete(like);
    }

    @Test
    void testRemoveLikeFromPost_notExists_throws() {
        long userId = 1L;
        long postId = 11L;

        when(likeRepository.findByPostIdAndUserId(postId, userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> likeService.getByPostIdAndUserId(postId, userId));
        verify(likeRepository, times(1)).findByPostIdAndUserId(postId, userId);
        verifyNoMoreInteractions(likeRepository);
    }

    @Test
    void testRemoveLikeFromComment() {
        long userId = 1L;
        long commentId = 11L;
        Like like = Like.builder()
                .userId(userId)
                .comment(Comment.builder()
                        .id(commentId)
                        .build())
                .build();

        when(likeRepository.findByCommentIdAndUserId(commentId, userId)).thenReturn(Optional.of(like));

        LikeResponseDto responseDto = likeService.removeLikeFromComment(userId, commentId);

        assertEquals(userId, responseDto.getUserId());
        assertEquals(commentId, responseDto.getCommentId());
        verify(likeRepository, times(1)).findByCommentIdAndUserId(commentId, userId);
        verify(likeRepository, times(1)).delete(like);
    }

    @Test
    void testRemoveLikeFromComment_notExists_throws() {
        long userId = 1L;
        long commentId = 11L;

        when(likeRepository.findByCommentIdAndUserId(commentId, userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> likeService.getByCommentIdAndUserId(commentId, userId));
        verify(likeRepository, times(1)).findByCommentIdAndUserId(commentId, userId);
        verifyNoMoreInteractions(likeRepository);
    }
}