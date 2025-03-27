package faang.school.postservice.likeService;

import faang.school.postservice.controller.LikeController;
import faang.school.postservice.like.LikeDto;
import faang.school.postservice.service.LikeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LikeControllerTest {
    @Mock
    private LikeService likeService;

    @InjectMocks
    private LikeController likeController;

    private final long USER_ID = 1L;
    private final long POST_ID = 2L;
    private final long COMMENT_ID = 3L;
    private LikeDto likeDto;

    @BeforeEach
    public void SetUp() {
        likeDto = LikeDto.builder().id(1L).userId(USER_ID).postId(POST_ID).build();
    }

    @Test
    public void testCreatedLikeForPost() {
        when(likeService.likePost(POST_ID, USER_ID)).thenReturn(likeDto);

        LikeDto result = likeController.likePost(POST_ID, USER_ID);

        assertNotNull(result);
        assertEquals(likeDto, result);
        verify(likeService).likePost(POST_ID, USER_ID);
    }

    @Test
    public void testCreatedLikeForComment() {
        when(likeService.likeComment(COMMENT_ID, USER_ID)).thenReturn(likeDto);

        LikeDto result = likeController.likeComment(COMMENT_ID, USER_ID);

        assertNotNull(result);
        assertEquals(likeDto, result);
        verify(likeService).likeComment(COMMENT_ID, USER_ID);
    }

    @Test
    public void testRemovedLikeForPost() {
        assertDoesNotThrow(() -> likeController.unlikePost(POST_ID, USER_ID));
        verify(likeService).unlikePost(POST_ID, USER_ID);
    }

    @Test
    public void testRemovedLikeForComment() {
        assertDoesNotThrow(() -> likeController.unlikeComment(COMMENT_ID, USER_ID));
        verify(likeService).unlikeComment(COMMENT_ID, USER_ID);
    }
}
