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
        likeController.likeToPost(likeDto);
        verify(likeService, times(1)).likeToPost(likeDto);
    }

    @Test
    void testUnlikeFromPost() {
        likeController.unlikeFromPost(likeDto);
        verify(likeService, times(1)).unlikeFromPost(likeDto);
    }

    @Test
    void testLikeToComment() {
        likeController.likeToComment(likeDto);
        verify(likeService, times(1)).likeToComment(likeDto);
    }

    @Test
    void testUnlikeFromComment() {
        likeController.unlikeFromComment(likeDto);
        verify(likeService, times(1)).unlikeFromComment(likeDto);
    }
}