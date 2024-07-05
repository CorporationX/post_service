package faang.school.postservice.service;

import faang.school.postservice.repository.LikeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @Mock
    private LikeRepository likeRepository;

    @InjectMocks
    private LikeService likeService;

    @Test
    public void testGetAllLikedPostUsers() {
        likeService.getAllLikedPost(1L);
        verify(likeRepository, times(1)).findByPostId(anyLong());
    }

    @Test
    public void testGetAllLikedPostUser() {
        likeService.getAllLikedComment(1L);
        verify(likeRepository, times(1)).findByCommentId(anyLong());
    }
}