package faang.school.postservice.controller;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.impl.LikeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LikeControllerTest {
    @InjectMocks
    private LikeController likeController;
    @Mock
    private LikeServiceImpl likeService;
    private long postId;
    private LikeDto likeDto;
    private long commentId;
    @BeforeEach
    void setUp() {
        postId = 3L;
        commentId = 3L;
        likeDto = new LikeDto(1L, 2L, LocalDateTime.now());
    }
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

