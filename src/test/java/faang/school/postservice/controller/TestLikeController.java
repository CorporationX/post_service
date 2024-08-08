package faang.school.postservice.controller;

import faang.school.postservice.controller.like.LikeController;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.LikeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TestLikeController {

    @InjectMocks
    private LikeController likeController;

    @Mock
    private LikeService likeService;

    private long postId;
    private long commentId;
    private long userId;

    @BeforeEach
    void setUp() {
        postId = 1;
        userId = 1;
        commentId = 1;
    }

    @DisplayName("Когда метод добавления лайка отработал")
    @Test
    public void testAddLikeToPostThenPostIdNegative() {
        LikeDto likeDto = LikeDto.builder()
                .id(1L)
                .userId(userId)
                .postId(postId)
                .build();

        likeController.addLikeToPost(likeDto);
        verify(likeService, times(1)).addLikeToPost(likeDto);
    }

    @DisplayName("Когда метод удалить лайк с поста отработал")
    @Test
    public void testDeleteLikeFromPostWhenValid() {
        likeController.deleteLikeFromPost(postId, userId);
        verify(likeService, times(1)).deleteLikeFromPost(postId, userId);
    }

    @DisplayName("Когда метод добавить лайк на коммент отработал")
    @Test
    public void testAddLikeToCommentWhenValid() {
        LikeDto likeDto = LikeDto.builder()
                .id(1L)
                .userId(userId)
                .commentId(commentId)
                .build();

        likeController.addLikeToComment(likeDto);
        verify(likeService, times(1)).addLikeToComment(likeDto);
    }

    @DisplayName("Когда тест удаления лайка с комментария отработал")
    @Test
    public void testDeleteLikeFromCommentWhenValid() {
        likeController.deleteLikeFromComment(commentId, userId);
        verify(likeService).deleteLikeFromComment(commentId, userId);
    }
}