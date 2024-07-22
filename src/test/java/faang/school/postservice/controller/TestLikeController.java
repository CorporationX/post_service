package faang.school.postservice.controller;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.LikeService;
import faang.school.postservice.validator.LikeControllerValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class TestLikeController {

    @InjectMocks
    private LikeController controller;

    @Mock
    private LikeControllerValidator validator;

    @Mock
    private LikeService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddLikeToPostThenPostIdNegative() {
        LikeDto likeDto = LikeDto.builder()
                .id(1L)
                .userId(1)
                .postId(0)
                .build();

        controller.addLikeToPost(likeDto);
        validator.validAddLikeToPost(likeDto.getPostId());
        verify(service, times(0)).addLikeToPost(likeDto);
    }

    @Test
    public void testAddLikeToPostWhenValid() {
        LikeDto likeDto = LikeDto.builder()
                .id(1L)
                .userId(1)
                .postId(1)
                .build();

        controller.addLikeToPost(likeDto);
        validator.validAddLikeToPost(likeDto.getPostId());
        verify(service, times(1)).addLikeToPost(likeDto);
    }

    @Test
    public void testDeleteLikeFromPostWhenPostIdNegative() {
        long postId = 0;
        long userId = 1;

        controller.deleteLikeFromPost(postId, userId);
        verify(service, times(0)).deleteLikeFromPost(postId, userId);
    }

    @Test
    public void testDeleteLikeFromPostWhenUserIdNegative() {
        long postId = 1;
        long userId = 0;

        controller.deleteLikeFromPost(postId, userId);
        verify(service, times(0)).deleteLikeFromPost(postId, userId);
    }

    @Test
    public void testDeleteLikeFromPostWhenValid() {
        long postId = 1;
        long userId = 1;

        controller.deleteLikeFromPost(postId, userId);
        verify(service, times(1)).deleteLikeFromPost(postId, userId);
    }

    @Test
    public void testAddLikeToCommentThenPostIdBlank() {
        LikeDto likeDto = LikeDto.builder()
                .id(1L)
                .userId(1)
                .commentId(0)
                .build();

        controller.addLikeToComment(likeDto);
        validator.validAddLikeToComment(likeDto.getCommentId());
        verify(service, times(0)).addLikeToComment(likeDto);
    }

    @Test
    public void testAddLikeToCommentWhenValid() {
        LikeDto likeDto = LikeDto.builder()
                .id(1L)
                .userId(1)
                .commentId(1)
                .build();

        controller.addLikeToComment(likeDto);
        validator.validAddLikeToComment(likeDto.getCommentId());
        verify(service, times(1)).addLikeToComment(likeDto);
    }

    @Test
    public void testDeleteLikeFromCommentWhenPCommentIdNegative() {
        long commentId = 0;
        long userId = 1;

        controller.deleteLikeFromComment(commentId, userId);
        verify(service, times(0)).deleteLikeFromComment(commentId, userId);
    }

    @Test
    public void testDeleteLikeFromCommentWhenUserIdNegative() {
        long commentId = 1;
        long userId = 0;

        controller.deleteLikeFromComment(commentId, userId);
        verify(service, times(0)).deleteLikeFromComment(commentId, userId);
    }

    @Test
    public void testDeleteLikeFromCommentWhenValid() {
        long commentId = 1;
        long userId = 1;

        controller.deleteLikeFromComment(commentId, userId);
        verify(service, times(1)).deleteLikeFromComment(commentId, userId);
    }
}
