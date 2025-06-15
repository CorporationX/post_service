package faang.school.postservice.controller;

import faang.school.postservice.dto.LikeDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.LikeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LikeControllerTest {
    @Mock
    private LikeService likeService;
    @InjectMocks
    private LikeController likeController;

    @Test
    void testAddLikeToPost() {
        LikeDto likeDto = createLikeDto(4444L);
        when(likeService.addLikeToPost(anyLong(), anyLong())).thenReturn(likeDto);

        assertEquals(likeDto, likeController.addLikeToPost(1L, 1L));
        verify(likeService).addLikeToPost(any(), anyLong());
    }

    @Test
    void testAddNullLikeToPost() {
        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () ->
                likeController.addLikeToPost(null, null));
        assertEquals("Negative or null id! Use valid Id!", dataValidationException.getMessage());
    }

    @Test
    void testRemoveLikeFromPost() {
        when(likeService.removeLikeFromPost(anyLong(), anyLong())).thenReturn(true);

        assertEquals(HttpStatus.NO_CONTENT, likeController.removeLikeFromPost(1L, 1L).getStatusCode());
        verify(likeService).removeLikeFromPost(anyLong(), anyLong());
    }

    @Test
    void testFailedRemoveFromPost() {
        when(likeService.removeLikeFromPost(anyLong(), anyLong())).thenReturn(false);

        assertEquals(HttpStatus.NOT_FOUND, likeController.removeLikeFromPost(1L, 1L).getStatusCode());
        verify(likeService).removeLikeFromPost(anyLong(), anyLong());
    }

    @Test
    void testAddLikeToComment() {
        LikeDto likeDto = createLikeDto(5555L);
        when(likeService.addLikeToComment(any(), anyLong())).thenReturn(likeDto);

        assertEquals(likeDto, likeService.addLikeToComment(1L, 1l));
        verify(likeService).addLikeToComment(anyLong(), anyLong());
    }

    @Test
    void testAddNullLikeToComment() {
        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () ->
                likeController.addLikeToComment(null, null));
        assertEquals("Negative or null id! Use valid Id!", dataValidationException.getMessage());
    }

    @Test
    void testRemoveLikeFromComment() {
        when(likeService.removeLikeFromComment(anyLong(), anyLong())).thenReturn(true);

        assertEquals(HttpStatus.NO_CONTENT, likeController.removeLikeFromComment(1L, 1L).getStatusCode());
        verify(likeService).removeLikeFromComment(anyLong(), anyLong());
    }

    @Test
    void testFailedRemoveLikeFromComment() {
        when(likeService.removeLikeFromComment(anyLong(), anyLong())).thenReturn(false);

        assertEquals(HttpStatus.NOT_FOUND, likeController.removeLikeFromComment(1L, 1L).getStatusCode());
        verify(likeService).removeLikeFromComment(anyLong(), anyLong());
    }

    private LikeDto createLikeDto(Long userId) {
        return LikeDto.builder()
                .id(231423L)
                .commentId(2433L)
                .userId(userId)
                .postId(34356L)
                .build();
    }
}