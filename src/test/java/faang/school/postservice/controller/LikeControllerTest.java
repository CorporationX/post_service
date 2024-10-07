package faang.school.postservice.controller;

import faang.school.postservice.controller.like.LikeController;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.like.LikeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LikeControllerTest {

    @InjectMocks
    private LikeController likeController;

    @Mock
    private LikeService likeService;

    private static final long POST_ID_ONE = 1L;
    private static final long COMMENT_ID_ONE = 1L;
    private List<UserDto> usersLiked;

    @BeforeEach
    void setUp() {
        usersLiked = List.of(UserDto.builder().build(),
                UserDto.builder().build());
    }

    @Nested
    class ControllerTest {
        @Test
        @DisplayName("Verifies controller calls service.getAllUsersByPostId then return UserDto list")
        public void whenServiceCalledGetAllUsersByPostIdThenReturnList() {
            when(likeService.getAllUsersByPostId(POST_ID_ONE)).thenReturn(usersLiked);
            List<UserDto> usersResultList = likeController.getAllUsersByPost(POST_ID_ONE);
            verify(likeService).getAllUsersByPostId(POST_ID_ONE);
            assertEquals(usersLiked, usersResultList);

        }

        @Test
        @DisplayName("Verifies controller calls service.getAllUsersByCommentId then return UserDto list")
        public void whenServiceCalledGetAllUsersByCommentIdThenReturnList() {
            when(likeService.getAllUsersByCommentId(COMMENT_ID_ONE)).thenReturn(usersLiked);
            List<UserDto> usersResultList = likeController.getAllUsersByComment(COMMENT_ID_ONE);
            verify(likeService).getAllUsersByCommentId(COMMENT_ID_ONE);
            assertEquals(usersLiked, usersResultList);
        }
    }
}
