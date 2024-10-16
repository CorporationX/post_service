package faang.school.postservice.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import faang.school.postservice.controller.LikeController;
import faang.school.postservice.model.dto.like.LikeDto;
import faang.school.postservice.service.LikeService;
import faang.school.postservice.validator.LikeValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class LikeControllerTest {

    @Mock
    private LikeService likeService;
    @Mock
    private LikeValidator likeValidator;
    @InjectMocks
    private LikeController likeController;

    Long postId;
    Long commentId;
    LikeDto likeDto;
    List<Long> userIds;
    LikeDto expectedResult;

    @BeforeEach
    public void setUp() {
        postId = 1L;
        commentId = 1L;
        userIds = List.of(1L, 2L, 3L);
        likeDto = new LikeDto();
        likeDto.setUserId(1L);
        expectedResult = new LikeDto();
        expectedResult.setUserId(1L);
    }

    @Test
    public void testGetLikesForPost() {
        when(likeService.getLikesFromPost(postId)).thenReturn(userIds);
        List<Long> result = likeController.getLikesForPost(postId);
        assertEquals(userIds, result);
        verify(likeService, times(1)).getLikesFromPost(postId);
    }

    @Test
    public void testGetLikesForComment() {
        when(likeService.getLikesFromComment(commentId)).thenReturn(userIds);
        List<Long> result = likeController.getLikesForComment(commentId);
        assertEquals(userIds, result);
        verify(likeService, times(1)).getLikesFromComment(commentId);
    }

    @Test
    public void addLikeToPostTest() {
        when(likeService.addLikeToPost(postId, likeDto)).thenReturn(expectedResult);
        LikeDto result = likeController.addLikeToPost(postId, likeDto);
        assertEquals(expectedResult, result);
        verify(likeValidator, times(1)).likeValidation(likeDto);
        verify(likeService, times(1)).addLikeToPost(postId, likeDto);
    }

    @Test
    public void removeLikeFromPostTest() {
        when(likeService.removeLikeFromPost(postId, likeDto)).thenReturn(expectedResult);
        LikeDto result = likeController.removeLikeFromPost(postId, likeDto);
        assertEquals(expectedResult, result);
        verify(likeValidator, times(1)).likeValidation(likeDto);
        verify(likeService, times(1)).removeLikeFromPost(postId, likeDto);
    }

    @Test
    public void addLikeToCommentTest() {
        when(likeService.addLikeToComment(commentId, likeDto)).thenReturn(expectedResult);
        LikeDto result = likeController.addLikeToComment(commentId, likeDto);
        assertEquals(expectedResult, result);
        verify(likeValidator, times(1)).likeValidation(likeDto);
        verify(likeService, times(1)).addLikeToComment(commentId, likeDto);
    }

    @Test
    public void removeLikeFromCommentTest() {
        when(likeService.removeLikeFromComment(commentId, likeDto)).thenReturn(expectedResult);
        LikeDto result = likeController.removeLikeFromComment(commentId, likeDto);
        assertEquals(expectedResult, result);
        verify(likeValidator, times(1)).likeValidation(likeDto);
        verify(likeService, times(1)).removeLikeFromComment(commentId, likeDto);
    }
}