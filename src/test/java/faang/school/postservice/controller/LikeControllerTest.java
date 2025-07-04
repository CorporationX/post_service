package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.mapper.LikeMapper;
import faang.school.postservice.repository.post.PostRepository;
import faang.school.postservice.service.LikeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LikeControllerTest {

    @Mock
    private LikeService likeService;

    @Mock
    private LikeMapper likeMapper;

    @Mock
    private UserContext userContext;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private LikeDto likeDtoInvalidPostId;
    private LikeDto likeDtoInvalidCommentId;
    private LikeDto likeDtoValid;
    Long positiveUserId;

    @BeforeEach
    void setUp() {
        likeDtoInvalidPostId = new LikeDto(1L, 1L, 1L, -1L);
        likeDtoInvalidCommentId = new LikeDto(1L, 1L, -1L, 1L);
        likeDtoValid = new LikeDto(1L, 1L, 1L, 1L);
        positiveUserId = 1L;
    }

    @InjectMocks
    private LikeController likeController;

    @Test
    public void testLikePostWithValidSetLikeToDto() {
        Mockito.when(userContext.getUserId()).thenReturn(positiveUserId);
        likeController.setLikeToPost(likeDtoValid);
        Mockito.verify(likeService, Mockito.times(1)).setLikeToPost(Mockito.any());
    }

    @Test
    public void testUnsetLikeToPostWithValidSetLikeToDto() {
        Mockito.when(userContext.getUserId()).thenReturn(positiveUserId);
        likeController.unsetLikeToPost(likeDtoValid);
        Mockito.verify(likeService, Mockito.times(1)).unsetLikeToPost(Mockito.any());
    }

    @Test
    public void testLikeCommentWithValidSetLikeToDto() {
        Mockito.when(userContext.getUserId()).thenReturn(positiveUserId);
        likeController.setLikeToComment(likeDtoValid);
        Mockito.verify(likeService, Mockito.times(1)).setLikeToComment(Mockito.any());
    }

    @Test
    public void testUnsetLikeToCommentWithValidSetLikeToDto() {
        Mockito.when(userContext.getUserId()).thenReturn(positiveUserId);
        likeController.unsetLikeToComment(likeDtoValid);
        Mockito.verify(likeService, Mockito.times(1)).unsetLikeToComment(Mockito.any());
    }

    @Test
    public void testSetLikeToPostWithInvalidSetLikeToDto() {
        Mockito.when(userContext.getUserId()).thenReturn(positiveUserId);
        likeController.setLikeToPost(likeDtoValid);
        Mockito.verify(postRepository, Mockito.times(0)).findById(Mockito.any());
    }
}
