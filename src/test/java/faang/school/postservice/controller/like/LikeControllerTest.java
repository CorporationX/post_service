package faang.school.postservice.controller.like;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.like.LikeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Тест покрывает
 * <ul>
 *     <li>Успешное получение лайков поста</li>
 *     <li>Успешное получение лайков комментария</li>
 *     <li>Обработка пустых списков</li>
 *     <li>Проверка HTTP статуса 200 OK</li>
 *     <li>Проверка вызова сервиса</li>
 * </ul>
 *
 *
 *
 *
 */
@ExtendWith(MockitoExtension.class)
class LikeControllerTest {

    @Mock
    private LikeService likeService;

    @InjectMocks
    private LikeController likeController;

    @Test
    void getAllLikesFromUsersToPost_Success() {
        Long postId = 1L;
        UserDto userDto = UserDto.builder().id(1L).build();
        List<UserDto> expectedUsers = List.of(userDto);

        when(likeService.getUsersWhoLikedPost(postId)).thenReturn(expectedUsers);

        ResponseEntity<List<UserDto>> response = likeController.getAllLikesFromUsersToPost(postId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUsers, response.getBody());
        verify(likeService).getUsersWhoLikedPost(postId);
    }

    @Test
    void getAllLikesFromUsersToComment_Success() {
        Long commentId = 1L;
        UserDto userDto = UserDto.builder().id(1L).build();
        List<UserDto> expectedUsers = List.of(userDto);

        when(likeService.getUsersWhoLikedComment(commentId)).thenReturn(expectedUsers);

        ResponseEntity<List<UserDto>> response = likeController.getAllLikesFromUsersToComment(commentId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUsers, response.getBody());
        verify(likeService).getUsersWhoLikedComment(commentId);
    }

    @Test
    void getAllLikesFromUsersToPost_EmptyList() {
        Long postId = 1L;
        when(likeService.getUsersWhoLikedPost(postId)).thenReturn(List.of());

        ResponseEntity<List<UserDto>> response = likeController.getAllLikesFromUsersToPost(postId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void getAllLikesFromUsersToComment_EmptyList() {
        Long commentId = 1L;
        when(likeService.getUsersWhoLikedComment(commentId)).thenReturn(List.of());

        ResponseEntity<List<UserDto>> response = likeController.getAllLikesFromUsersToComment(commentId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }
}