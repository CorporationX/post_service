package faang.school.postservice.controller;

import faang.school.postservice.service.LikeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LikeControllerTest {
    @Mock
    private LikeServiceImpl likeService;
    @InjectMocks
    private LikeController likeController;

    @Test
    void getUsersLikedPost_whenOk() {
        long id = 10L;
        likeController.getUsersLikedPost(id, 1L);

        Mockito.verify(likeService, Mockito.times(1))
                .getUsersLikedPost(id);
    }

    @Test
    void getUsersLikedComm_whenOk() {
        long id = 10L;
        likeController.getUsersLikedComm(id, 1L);

        Mockito.verify(likeService, Mockito.times(1))
                .getUsersLikedComm(id);
    }
}
