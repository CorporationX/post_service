package faang.school.postservice.srvice;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.model.Like;
import faang.school.postservice.publisher.LikeEventPublisher;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.LikeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class LikeServiceTest {
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @InjectMocks
    private LikeService service;

    List<Like> likes = List.of(Like.builder().userId(1L).build(), Like.builder().userId(2L).build(), Like.builder().userId(3L).build());

    @Test
    public void getPostLikes_Test() {
        Mockito.when(likeRepository.findByPostId(1L)).thenReturn(likes);

        service.getPostLikes(1);

        Mockito.verify(likeRepository).findByPostId(1L);
    }

    @Test
    public void getCommentLikes_Test() {
        Mockito.when(likeRepository.findByCommentId(1L)).thenReturn(likes);

        service.getCommentLikes(1);

        Mockito.verify(likeRepository).findByCommentId(1L);
    }
}
