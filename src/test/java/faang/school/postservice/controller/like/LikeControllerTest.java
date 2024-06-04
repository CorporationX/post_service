package faang.school.postservice.controller.like;

import faang.school.postservice.service.like.LikeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LikeControllerTest {
    @InjectMocks
    private LikeController likeController;

    @Mock
    private LikeService likeService;

    @DisplayName("Should call likeService.getUsersLikedPostByPostId()")
    @Test
    void getUsersLikedPostByPostId() {
        likeController.getUsersLikedPostByPostId(anyLong());

        verify(likeService).getUsersLikedPostByPostId(anyLong());
    }

    @DisplayName("Should call likeService.getUsersLikedCommentByCommentId()")
    @Test
    void getUsersLikedCommentByCommentId() {
        likeController.getUsersLikedCommentByCommentId(anyLong());

        verify(likeService).getUsersLikedCommentByCommentId(anyLong());
    }
}