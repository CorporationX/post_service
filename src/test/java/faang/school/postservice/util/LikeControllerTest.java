package faang.school.postservice.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import faang.school.postservice.controller.LikeController;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.like.LikeService;
import faang.school.postservice.validator.LikeValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

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

    @BeforeEach
    public void setUp() {
        postId = 1L;
        commentId = 1L;
        userIds = List.of(1L, 2L, 3L);
        likeDto = new LikeDto();
        likeDto.setUserId(1L);
        likeDto.setPostId(postId);
    }

    @Test
    public void testGetLikesForPost() {
        when(likeService.getLikesFromPost(postId)).thenReturn(userIds);
        ResponseEntity<List<Long>> response = likeController.getLikesForPost(postId);
        assertEquals(ResponseEntity.ok(userIds), response);
        verify(likeService, times(1)).getLikesFromPost(postId);
    }

    @Test
    public void testGetLikesForComment() {
        when(likeService.getLikesFromComment(commentId)).thenReturn(userIds);
        ResponseEntity<List<Long>> response = likeController.getLikesForComment(commentId);
        assertEquals(ResponseEntity.ok(userIds), response);
        verify(likeService, times(1)).getLikesFromComment(commentId);
    }

    @Test
    public void addLikeToPostTest() {
        ResponseEntity<Void> response = likeController.addLikeToPost(postId, likeDto);
        assertEquals(ResponseEntity.ok().build(), response);
        verify(likeValidator, times(1)).likeValidation(likeDto);
        verify(likeService, times(1)).addLikeToPost(postId, likeDto);
    }

    @Test
    public void removeLikeFromPostTest() {
        ResponseEntity<Void> response = likeController.removeLikeFromPost(postId, likeDto);
        assertEquals(ResponseEntity.ok().build(), response);
        verify(likeValidator, times(1)).likeValidation(likeDto);
        verify(likeService, times(1)).removeLikeFromPost(postId, likeDto);
    }

    @Test
    public void addLikeToCommentTest() {
        ResponseEntity<Void> response = likeController.addLikeToComment(commentId, likeDto);
        assertEquals(ResponseEntity.ok().build(), response);
        verify(likeValidator, times(1)).likeValidation(likeDto);
        verify(likeService, times(1)).addLikeToComment(commentId, likeDto);
    }

    @Test
    public void removeLikeFromCommentTest() {
        ResponseEntity<Void> response = likeController.removeLikeFromComment(commentId, likeDto);
        assertEquals(ResponseEntity.ok().build(), response);
        verify(likeValidator, times(1)).likeValidation(likeDto);
        verify(likeService, times(1)).removeLikeFromComment(commentId, likeDto);
    }
}