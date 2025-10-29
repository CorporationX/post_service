package faang.school.postservice.controller;

import faang.school.postservice.controller.like.LikeController;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.ResourceNotFoundException;
import faang.school.postservice.service.like.LikeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(MockitoExtension.class)
public class LikeControllerTest {
    private final static long DEFAULT_ID = 1L;
    private final static long NOT_EXIST_ID = 123L;
    private final static int LIKES_COUNT = 25;
    private final static int ZERO = 0;

    private final long postId = DEFAULT_ID;
    private final long commentId = DEFAULT_ID;
    private final long userId = DEFAULT_ID;
    private final int likesCount = LIKES_COUNT;
    private final long notExistPostId = NOT_EXIST_ID;
    private final long notExistCommentId = NOT_EXIST_ID;
    private final int zeroLikeCount = ZERO;
    private final LikeDto likeOnPostDto = LikeDto.builder()
            .userId(userId)
            .postId(postId)
            .build();
    private final LikeDto likeOnCommentDto = LikeDto.builder()
            .userId(userId)
            .commentId(commentId)
            .build();

    private MockMvc mockMvc;

    @InjectMocks
    private LikeController likeController;

    @Mock
    private LikeServiceImpl likeService;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(likeController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    public void testSuccessfullyLikeOnPostSet() throws Exception {
        when(likeService.setLikeOnPost(postId)).thenReturn(likeOnPostDto);
        mockMvc.perform(post("/likes/set/post/{postId}",postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").value(postId))
                .andExpect(jsonPath("$.userId").value(userId));
        verify(likeService, times(1)).setLikeOnPost(postId);
    }

    @Test
    public void testSuccessfullyLikeOnPostUnset() throws Exception {
        mockMvc.perform(delete("/likes/unset/post/{postId}", postId))
                .andExpect(status().isOk());
        verify(likeService, times(1)).unsetLikeOnPost(postId);
    }

    @Test
    public void testSuccessfullyLikeOnCommentSet() throws Exception {
        when(likeService.setLikeOnComment(commentId)).thenReturn(likeOnCommentDto);
        mockMvc.perform(post("/likes/set/comment/{commentId}",commentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentId").value(commentId))
                .andExpect(jsonPath("$.userId").value(userId));
        verify(likeService, times(1)).setLikeOnComment(commentId);
    }

    @Test
    public void testSuccessfullyLikeOnCommentUnset() throws Exception {
        mockMvc.perform(delete("/likes/unset/comment/{commentId}", commentId))
                .andExpect(status().isOk());
        verify(likeService, times(1)).unsetLikeOnComment(commentId);
    }

    @Test
    public void testPostLikesCountGet() throws Exception {
        when(likeService.getPostLikesCount(postId)).thenReturn(likesCount);
        mockMvc.perform(get("/likes/count/{postId}", postId))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(likesCount)));
        verify(likeService, times(1)).getPostLikesCount(postId);
    }

    @Test
    public void testFailLikeOnPostSetWhenPostDoesNotExist() throws Exception {
        when(likeService.setLikeOnPost(notExistPostId))
                .thenThrow(new ResourceNotFoundException("Post doesn't exist"));
        mockMvc.perform(post("/likes/set/post/{postId}", notExistPostId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Post doesn't exist"));
    }

    @Test
    public void testFailLikeOnPostSetWhenLikeAlreadySet() throws Exception {
        when(likeService.setLikeOnPost(postId))
                .thenThrow(new DataValidationException("User has already set a like for this post"));
        mockMvc.perform(post("/likes/set/post/{postId}", postId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User has already set a like for this post"));
    }

    @Test
    public void testFailLikeOnCommentSetWhenCommentDoesNotExist() throws Exception {
        when(likeService.setLikeOnComment(notExistCommentId))
                .thenThrow(new ResourceNotFoundException("Comment doesn't exist"));
        mockMvc.perform(post("/likes/set/comment/{comemntId}", notExistCommentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Comment doesn't exist"));
    }

    @Test
    public void testFailLikeOnCommentSetWhenLikeAlreadySet() throws Exception {
        when(likeService.setLikeOnComment(commentId))
                .thenThrow(new DataValidationException("User has already set a like for the comment"));
        mockMvc.perform(post("/likes/set/comment/{commentId}", commentId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User has already set a like for the comment"));
    }

    @Test void testLikesCountGetWhenPostNotFound() throws Exception {
        when(likeService.getPostLikesCount(postId)).thenReturn(zeroLikeCount);
        mockMvc.perform(get("/likes/count/{postId}", postId))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(zeroLikeCount)));
    }
}
