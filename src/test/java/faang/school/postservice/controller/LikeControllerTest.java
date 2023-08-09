package faang.school.postservice.controller;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.exeption.DataValidationException;
import faang.school.postservice.service.LikeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LikeControllerTest {
    @Mock
    LikeService likeService;
    @InjectMocks
    LikeController likeController;
    @Mock
    LikeDto like;

    @Test
    void addLikeToPost() {
        likeController.addLikeToPost(11, like);
        verify(likeService, times(1)).addLikeToPost(11,like);
    }
    @Test
    void addLikeToPostValidate(){
        assertThrows(DataValidationException.class,
                () -> likeController.addLikeToPost(-22, like));
    }

    @Test
    void addLikeToComment() {
        likeController.addLikeToComment(11, like);
        verify(likeService, times(1)).addLikeToComment(11,like);
    }
    @Test
    void addLikeToCommentValidate(){
        assertThrows(DataValidationException.class,
                () -> likeController.addLikeToComment(-22, like));
    }

    @Test
    void deleteLikeFromPost() {
        likeController.deleteLikeFromPost(11, 22);
        verify(likeService, times(1)).deleteLikeFromPost(anyLong(),anyLong());
    }
    @Test
    void deleteLikeFromPostValidate(){
        assertThrows(DataValidationException.class,
                () -> likeController.deleteLikeFromPost(-22, 0));
    }

    @Test
    void deleteLikeFromComment() {
        likeController.deleteLikeFromComment(11, 22);
        verify(likeService, times(1)).deleteLikeFromComment(anyLong(),anyLong());
    }
    @Test
    void deleteLikeFromCommentValidate(){
        assertThrows(DataValidationException.class,
                () -> likeController.deleteLikeFromComment(-22, 0));
    }
}