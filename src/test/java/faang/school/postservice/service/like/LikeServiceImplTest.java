package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserViewDto;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.CommentRepository;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LikeServiceImplTest {
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private PostRepository postRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserServiceClient serviceClient;

    @InjectMocks
    private LikeServiceImpl likeService;

    @Test
    void getUsersWhoLikedPostSuccess() {
        Long postId = 1L;
        Long userId = 3L;
        Like like = Like.builder().userId(userId).build();
        UserViewDto userViewDto = UserViewDto.builder().id(userId).build();

        when(likeRepository.findByPostId(postId)).thenReturn(List.of(like));
        when(serviceClient.getUsersByIds(List.of(userId))).thenReturn(List.of(userViewDto));

        List<UserViewDto> result = likeService.getUsersWhoLikedPost(postId);

        assertEquals(1, result.size());
        assertEquals(userId, result.get(0).id());
        verify(likeRepository).findByPostId(postId);
        verify(serviceClient).getUsersByIds(List.of(userId));
    }

    @Test
    void getUsersWhoLikedCommentSuccess() {
        Long commentId = 2L;
        Long userId = 3L;
        Like like = Like.builder().userId(userId).build();
        UserViewDto userViewDto = UserViewDto.builder().id(userId).build();

        when(likeRepository.findByCommentId(commentId))
                .thenReturn(List.of(like));
        when(serviceClient.getUsersByIds(List.of(userId)))
                .thenReturn(List.of(userViewDto));

        List<UserViewDto> result = likeService.getUsersWhoLikedComment(commentId);

        assertEquals(1, result.size());
        assertEquals(userId, result.get(0).id());
    }
}