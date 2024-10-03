package faang.school.postservice.service.like;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.model.Like;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.service.like.LikeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LikeServiceTest {

    @InjectMocks
    private LikeServiceImpl likeService;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private UserServiceClient userServiceClient;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    private static final Like like1 = Like.builder()
            .id(1L)
            .userId(1L)
            .build();
    private static final List<Like> likes = List.of(like1);

    @Test
    public void testGetUsersByPostId() {
        when(likeRepository.findByPostId(1L)).thenReturn(likes);

        likeService.getUsersLikedPost(1L);

        verify(likeRepository).findByPostId(1L);
        verify(userServiceClient).getUsersByIds(List.of(1L));
    }

    @Test
    public void testGetUsersByCommentId() {
        when(likeRepository.findByCommentId(1L)).thenReturn(likes);

        likeService.getUsersLikedComment(1L);

        verify(likeRepository).findByCommentId(1L);
        verify(userServiceClient).getUsersByIds(List.of(1L));
    }

}
