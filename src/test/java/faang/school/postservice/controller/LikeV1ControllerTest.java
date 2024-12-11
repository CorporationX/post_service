package faang.school.postservice.controller;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.like.LikeCommentDto;
import faang.school.postservice.dto.like.LikePostDto;
import faang.school.postservice.service.LikeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class LikeV1ControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LikeService likeService;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private LikeV1Controller likeV1Controller;

    private long userId;
    private long postId;
    private long commentId;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(likeV1Controller).build();

        userId = 1L;
        postId = 5L;
        commentId = 6L;
    }

    @Test
    public void testLikePost() throws Exception {
        LikePostDto likePostDto = LikePostDto.builder()
                .id(2L)
                .postId(postId)
                .userId(userId)
                .build();
        when(userContext.getUserId()).thenReturn(userId);
        when(likeService.createLikePost(postId, userId)).thenReturn(likePostDto);

        mockMvc.perform(post("/like/post/{postId}", postId))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.postId", is(5)));

        verify(likeService, times(1)).createLikePost(postId, userId);
    }

    @Test
    public void testLikePostWhenUserWithNegativeId() {
        long nonExistentUser = -1;
        when(userContext.getUserId()).thenReturn(nonExistentUser);

        assertThrows(IllegalArgumentException.class,
                () -> likeV1Controller.likePost(postId));
    }

    @Test
    public void testLikeComment() throws Exception {
        LikeCommentDto likeCommentDto = LikeCommentDto.builder()
                .id(2L)
                .commentId(commentId)
                .userId(userId)
                .build();
        when(userContext.getUserId()).thenReturn(userId);
        when(likeService.createLikeComment(commentId, userId)).thenReturn(likeCommentDto);


        mockMvc.perform(post("/like/comment/{commentId}", commentId))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.commentId", is(6)));

        verify(likeService, times(1)).createLikeComment(commentId, userId);
    }

    @Test
    public void testLikeCommentWhenUserWithNegativeId() {
        long nonExistentUser = -1;
        when(userContext.getUserId()).thenReturn(nonExistentUser);

        assertThrows(IllegalArgumentException.class,
                () -> likeV1Controller.likeComment(commentId));
    }

    @Test
    public void testDeleteLikeFromPost() throws Exception {
        when(userContext.getUserId()).thenReturn(userId);
        doNothing().when(likeService).deleteLikeFromPost(postId, userId);

        mockMvc.perform(delete("/like/delete/post/{postId}", postId))
                .andExpect(status().isOk());

        verify(likeService, times(1)).deleteLikeFromPost(postId, userId);
    }

    @Test
    public void testDeletePostWhenUserWithNegativeId() {
        long nonExistentUser = -1;
        when(userContext.getUserId()).thenReturn(nonExistentUser);

        assertThrows(IllegalArgumentException.class,
                () -> likeV1Controller.deleteLikeFromPost(postId));
    }

    @Test
    public void testDeleteLikeFromComment() throws Exception {
        when(userContext.getUserId()).thenReturn(userId);
        doNothing().when(likeService).deleteLikeFromComment(commentId, userId);

        mockMvc.perform(delete("/like/delete/comment/{commentId}", commentId))
                .andExpect(status().isOk());

        verify(likeService, times(1)).deleteLikeFromComment(commentId, userId);
    }

    @Test
    public void testDeleteCommentWhenUserWithNegativeId() {
        long nonExistentUser = -1;
        when(userContext.getUserId()).thenReturn(nonExistentUser);

        assertThrows(IllegalArgumentException.class,
                () -> likeV1Controller.deleteLikeFromComment(commentId));
    }
}