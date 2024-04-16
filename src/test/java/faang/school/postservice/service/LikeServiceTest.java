package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.like.LikeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {
    @Mock
    private PostRepository postRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private CommentRepository commentRepository;
    @InjectMocks
    private LikeService likeService;

    @Test
    void test_GetUsersLikedPost_NotFoundedPost() {
        Long postId = 1L;

        when(postRepository.findById(postId)).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> likeService.getUsersLikedPost(postId));
        verify(postRepository).findById(postId);
    }

    @Test
    void test_GetUsersLikedPost_ReturnUsers() {
        Long postId = 1L;
        Post post = Post.builder().likes(getLikes()).build();
        List<Long> ids = List.of(1L, 2L);
        List<UserDto> usersExpected = getUsers();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userServiceClient.getUsersByIds(ids)).thenReturn(usersExpected);

        List<UserDto> userActual = likeService.getUsersLikedPost(postId);


        Assertions.assertEquals(usersExpected, userActual);
        verify(postRepository).findById(postId);
        verify(userServiceClient).getUsersByIds(ids);
    }

    @Test
    void test_GetUsersLikedComment_NotFoundedPost() {
        Long commentId = 1L;

        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> likeService.getUsersLikedComment(commentId));
        verify(commentRepository).findById(commentId);
    }

    @Test
    void test_GetUsersLikedComment_ReturnUsers() {
        Long postId = 1L;
        Comment comment = Comment.builder().likes(getLikes()).build();
        List<Long> ids = List.of(1L, 2L);
        List<UserDto> usersExpected = getUsers();

        when(commentRepository.findById(postId)).thenReturn(Optional.of(comment));
        when(userServiceClient.getUsersByIds(ids)).thenReturn(usersExpected);

        List<UserDto> userActual = likeService.getUsersLikedComment(postId);


        Assertions.assertEquals(usersExpected, userActual);
        verify(commentRepository).findById(postId);
        verify(userServiceClient).getUsersByIds(ids);
    }

    private List<Like> getLikes() {
        return new ArrayList<>(List.of(
                Like.builder().id(1).userId(1L).build(),
                Like.builder().id(2).userId(2L).build()
        ));
    }

    private List<UserDto> getUsers(){
        return new ArrayList<>(List.of(
                UserDto.builder().id(1L).build(),
                UserDto.builder().id(2L).build()
        ));
    }
}
