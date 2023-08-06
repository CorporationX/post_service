package faang.school.postservice.controller;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.service.LikeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LikeControllerTest {
    @Mock
    LikeController likeController;
    @InjectMocks
    LikeService likeService;
    @Mock
    LikeDto like;

    @Test
    void addLikeToPost() {
        likeController.addLikeToPost(11, like);
        verify(likeService, times(1)).addLikeToPost(11,like);
    }

    @Test
    void addLikeToComment() {
    }

    @Test
    void deleteLikeFromPost() {
    }

    @Test
    void deleteLikeFromComment() {
    }
}