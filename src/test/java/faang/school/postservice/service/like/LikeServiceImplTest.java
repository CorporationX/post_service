package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LikeServiceImplTest {
    @Mock private LikeRepository likeRepository;
    @Mock private PostRepository postRepository;
    @Mock private CommentRepository commentRepository;
    @Mock private UserServiceClient serviceClient;

    @InjectMocks private LikeServiceImpl likeService;

    @Test
    void getUsersWhoLikedCommentNullCommentIdThrowsException() {
        DataValidationException exception = assertThrows(DataValidationException.class,
                () -> likeService.getUsersWhoLikedComment(1L, null));

        assertEquals("Id комментария не может быть null", exception.getMessage());

        verifyNoInteractions(postRepository);
        verifyNoInteractions(commentRepository);
        verifyNoInteractions(likeRepository);
        verifyNoInteractions(serviceClient);
    }

    @Test
    void getUsersWhoLikedPostSuccess() {
        Long postId = 1L;
        Long userId = 3L;
        Post post = Post.builder().id(postId).build();
        Like like = Like.builder().userId(userId).build();
        UserDto userDto = UserDto.builder().id(userId).build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(likeRepository.getLikesByPost(post)).thenReturn(List.of(like));
        when(serviceClient.getUser(userId)).thenReturn(userDto);

        List<UserDto> result = likeService.getUsersWhoLikedPost(postId);

        assertEquals(1, result.size());
        assertEquals(userId, result.get(0).id());
        verify(postRepository).findById(postId);
        verify(likeRepository).getLikesByPost(post);
        verify(serviceClient).getUser(userId);
    }

    @Test
    void getUsersWhoLikedCommentSuccess() {
        Long postId = 1L;
        Long commentId = 2L;
        Long userId = 3L;
        Post post = Post.builder().id(postId).build();
        Comment comment = Comment.builder().id(commentId).build();
        Like like = Like.builder().userId(userId).build();
        UserDto userDto = UserDto.builder().id(userId).build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(likeRepository.getLikesByPostAndComment(post, comment))
                .thenReturn(List.of(like));
        when(serviceClient.getUser(userId)).thenReturn(userDto);

        List<UserDto> result = likeService.getUsersWhoLikedComment(postId, commentId);

        assertEquals(1, result.size());
        assertEquals(userId, result.get(0).id());
    }

    @Test
    void getUsersWhoLikedCommentNullPostIdThrowsException() {
        assertThrows(DataValidationException.class,
                () -> likeService.getUsersWhoLikedComment(null, 1L));

        verifyNoInteractions(postRepository);
        verifyNoInteractions(commentRepository);
        verifyNoInteractions(likeRepository);
        verifyNoInteractions(serviceClient);
    }

    @Test
    void getUsersWhoLikedCommentBothIdsNullThrowsExceptionForPostFirst() {
        assertThrows(DataValidationException.class,
                () -> likeService.getUsersWhoLikedComment(null, null));

        verifyNoInteractions(postRepository);
        verifyNoInteractions(commentRepository);
        verifyNoInteractions(likeRepository);
        verifyNoInteractions(serviceClient);
    }
}