package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.repository.LikeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LikeServiceTest {

    @InjectMocks
    private LikeService likeService;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private CommentService commentService;
    @Mock
    private PostService postService;

    @Mock
    private UserServiceClient userServiceClient;

    @Test
    void testExistIdAndGetLikesByPostId() {
        likeService.getLikesByPostId(anyLong());
        verify(postService, Mockito.times(1)).getPostById(anyLong());
        verify(likeRepository, Mockito.times(1)).findByPostId(anyLong());
    }

    @Test
    void testExistIdAndGetLikesByCommentId() {
        likeService.getLikesByCommentId(anyLong());
        verify(commentService, Mockito.times(1)).checkCommentExists(anyLong());
        verify(likeRepository, Mockito.times(1)).findByCommentId(anyLong());
    }
}