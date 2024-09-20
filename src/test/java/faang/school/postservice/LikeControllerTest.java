package faang.school.postservice;

import faang.school.postservice.conroller.LikeController;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.LikeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class LikeControllerTest {
    @Mock
    private LikeService likeService;
    @InjectMocks
    private LikeController likeController;
    @Test
    void addLikeToPost() {
        LikeDto likeDto = new LikeDto(1L,2L);
        long postId = 3L;

        Mockito.verify(likeService,times(1)).addLikeToPost(likeDto, postId);
    }
}
