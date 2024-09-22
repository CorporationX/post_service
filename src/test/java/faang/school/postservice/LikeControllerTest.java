package faang.school.postservice;

import faang.school.postservice.conroller.LikeController;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.LikeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class LikeControllerTest {

   // private MockMvc mockMvc;
    @Mock
    private LikeService likeService;
    @InjectMocks
    private LikeController likeController;
    private long postId;
    private LikeDto likeDto;
    private long commentId;

    @BeforeEach
    void setUp() {
       // mockMvc = MockMvcBuilders.standaloneSetup(likeController).build();
        postId = 3L;
        commentId = 3L;
        likeDto = new LikeDto(1L, 2L);
    }

    @Test
    void TestAddLikeToPost() {
        likeController.addLikeToPost(likeDto, postId);

        verify(likeService, times(1)).addLikeToPost(likeDto, postId);
    }

    @Test
    void TestDeleteLikeFromPost() {
        likeController.deleteLikeFromPost(likeDto, postId);

        verify(likeService, times(1)).deleteLikeFromPost(likeDto, postId);
    }

    @Test
    void TestAddLikeToComment() {
        likeController.addLikeToComment(likeDto, commentId);

        verify(likeService, times(1)).addLikeToComment(likeDto, commentId);
    }

    @Test
    void deleteLikeFromComment() {
        likeController.deleteLikeFromComment(likeDto, commentId);

        verify(likeService, times(1)).deleteLikeFromComment(likeDto, commentId);
    }

    @Test
    void findLikesOfPublishedPost() {
        likeController.findLikesOfPublishedPost(postId);

        verify(likeService, times(1)).findLikesOfPublishedPost(postId);
    }
}
