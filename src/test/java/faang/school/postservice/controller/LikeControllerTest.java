package faang.school.postservice.controller;

import faang.school.postservice.service.post.LikeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
public class LikeControllerTest {
    private final Long id = 1L;

    @InjectMocks
    private LikeController likeController;

    @Mock
    private LikeService likeService;

    @Test
    public void testGetUsersThatLikedThePost() {
        likeController.getUsersThatLikedThePost(id);
        verify(likeService, times(1)).getUsersThatLikedThePost(id);
    }

    @Test
    public void testGetUsersThatLikedTheComment() {
        likeController.getUsersThatLikedTheComment(id);
        verify(likeService, times(1)).getUsersThatLikedTheComment(id);
    }

}
