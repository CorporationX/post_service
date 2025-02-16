package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        when(postRepository.findById(postId))
                        .thenReturn(Optional.of(post));
        likeService.addLikeToPost(postId, userId, commentId);
        verify(postRepository, times(1)).save(post);
        verify(likeRepository, times(1)).save(like);
    }

    @Test
    public void addLikeToPost_throwsUserNotFoundException_whenUserNotExists() {
        Long postId = 1L;
        Long userId = 1L;

        when(userServiceClient.getUser(userId)).thenThrow(FeignException.NotFound.class);

        Assertions.assertThrows(UserNotFoundException.class, () -> {
            likeService.addLikeToPost(postId, null, userId);
        });
    }

    @Test
    public void removeLikeFromPostTest(){
        Long postId = 1L;
        Long userId = 1L;
        likeService.removeLikeFromPost(postId, userId);
        verify(likeRepository, times(1)).deleteByUserIdAndPostId(postId,userId);
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
        when(commentRepository.findById(postId))
                .thenReturn(Optional.of(comment));
        likeService.addLikeToComment(postId, userId, commentId);
        verify(commentRepository, times(1)).save(comment);
        verify(likeRepository, times(1)).save(like);
    }

    @Test
    public void addLikeToComment_throwsUserNotFoundException_whenUserNotExists() {
        Long postId = 1L;
        Long userId = 1L;

        when(userServiceClient.getUser(userId)).thenThrow(FeignException.NotFound.class);

        Assertions.assertThrows(UserNotFoundException.class, () -> {
            likeService.addLikeToComment(postId, null, userId);
        });
    }

    @Test
    public void removeLikeFromCommentTest(){
        Long commentId = 1L;
        Long userId = 1L;
        likeService.removeLikeFromComment(commentId, userId);
        verify(likeRepository, times(1)).deleteByUserIdAndCommentId(commentId,userId);
    }

    @Test
    void testGetUsersWhoLikedPost() {
        Long postId = 1L;
        List<Long> userIds = Arrays.asList(1L, 2L);
        List<Like> likes = Arrays.asList(
                Like.builder().userId(1L).build(),
                Like.builder().userId(2L).build()
        );

        when(likeRepository.findByPostId(postId)).thenReturn(likes);

        List<UserDto> users = Arrays.asList(
                new UserDto(1L, "user1", "user1@example.com"),
                new UserDto(2L, "user2", "user2@example.com")
        );
        Page<UserDto> userPage = new PageImpl<>(users);

        when(userServiceClient.getUsersByIds(userIds, PageRequest.of(0, 100))).thenReturn(userPage);

        List<UserDto> result = likeService.getUsersWhoLikedPost(postId);

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).id());
        assertEquals("user1", result.get(0).username());
        assertEquals(2L, result.get(1).id());
        assertEquals("user2", result.get(1).username());

        verify(likeRepository, times(1)).findByPostId(postId);
        verify(userServiceClient, times(1))
                .getUsersByIds(userIds, PageRequest.of(0, 100));
    }

    @Test
    void testGetUsersWhoLikedPost_NoLikes() {
        Long postId = 1L;
        when(likeRepository.findByPostId(postId)).thenReturn(Collections.emptyList());

        List<UserDto> result = likeService.getUsersWhoLikedPost(postId);

        assertEquals(0, result.size());

        verify(likeRepository, times(1)).findByPostId(postId);
        verify(userServiceClient, never()).getUsersByIds(anyList(), any(Pageable.class));
    }

    @Test
    void testGetUsersWhoLikedComment() {
        Long commentId = 1L;
        List<Long> userIds = Arrays.asList(1L, 2L);
        List<Like> likes = Arrays.asList(
                Like.builder().userId(1L).build(),
                Like.builder().userId(2L).build()
        );

        when(likeRepository.findByCommentId(commentId)).thenReturn(likes);

        List<UserDto> users = Arrays.asList(
                new UserDto(1L, "user1", "user1@example.com"),
                new UserDto(2L, "user2", "user2@example.com")
        );
        Page<UserDto> userPage = new PageImpl<>(users);

        when(userServiceClient.getUsersByIds(userIds, PageRequest.of(0, 100))).thenReturn(userPage);

        List<UserDto> result = likeService.getUsersWhoLikedComment(commentId);

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).id());
        assertEquals("user1", result.get(0).username());
        assertEquals(2L, result.get(1).id());
        assertEquals("user2", result.get(1).username());

        verify(likeRepository, times(1)).findByCommentId(commentId);
        verify(userServiceClient, times(1))
                .getUsersByIds(userIds, PageRequest.of(0, 100));
    }

    @Test
    void testGetUsersWhoLikedComment_NoLikes() {
        Long commentId = 1L;
        when(likeRepository.findByCommentId(commentId)).thenReturn(Collections.emptyList());

        List<UserDto> result = likeService.getUsersWhoLikedComment(commentId);

        assertEquals(0, result.size());

        verify(likeRepository, times(1)).findByCommentId(commentId);
        verify(userServiceClient, never()).getUsersByIds(anyList(), any(Pageable.class));
    }
}