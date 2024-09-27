package faang.school.postservice.controller;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.like.LikeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LikeControllerTest {

    @InjectMocks
    LikeController likeController;

    @Mock
    LikeService likeService;

    LikeDto likeDto = new LikeDto();

    @BeforeEach
    void setUp() {
        likeDto.setPostId(1L);
        likeDto.setUserId(1L);
        likeDto.setCommentId(1L);
    }

    @Test
    void testLikeToPost() {
        likeController.likePost(likeDto);
        verify(likeService, times(1)).likePost(likeDto);
    }

    @Test
    void testUnlikeFromPost() {
        likeController.unlikePost(likeDto);
        verify(likeService, times(1)).unlikePost(likeDto);
    }

    @Test
    void testLikeToComment() {
        likeController.likeComment(likeDto);
        verify(likeService, times(1)).likeComment(likeDto);
    }

    @Test
    void testUnlikeFromComment() {
        likeController.unlikeComment(likeDto);
        verify(likeService, times(1)).unlikeComment(likeDto);
    }
}