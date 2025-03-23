package faang.school.postservice.controller.like;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.exception.LikeAlreadyExistException;
import faang.school.postservice.exception.LikeNotFoundException;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.service.like.interfaces.LikeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(LikeController.class)
class LikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserContext userContext;

    @MockBean
    private LikeService likeService;

    LikeDto postLikeDto;
    LikeDto commentLikeDto;

    @BeforeEach
    void setUp() {
        postLikeDto = LikeDto.builder().postId(1L).userId(1L).build();
        commentLikeDto = LikeDto.builder().commentId(1L).userId(1L).build();
    }

    @Test
    void testLikePostWhenLikeCreated() throws Exception {
        when(likeService.likePost(postLikeDto.getPostId(), postLikeDto.getUserId())).thenReturn(postLikeDto);

        mockMvc.perform(post("/posts/" + postLikeDto.getPostId() + "/like")
                        .header("x-user-id", postLikeDto.getUserId()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.postId").value(postLikeDto.getPostId()))
                .andExpect(jsonPath("$.userId").value(postLikeDto.getUserId()));

        verify(likeService, times(1))
                .likePost(postLikeDto.getPostId(), postLikeDto.getUserId());
    }

    @Test
    void testLikePostWhenPostNotFound() throws Exception {
        when(likeService.likePost(postLikeDto.getPostId(), postLikeDto.getUserId()))
                .thenThrow(new PostNotFoundException("Post not found"));

        mockMvc.perform(post("/posts/" + postLikeDto.getPostId() + "/like")
                        .header("x-user-id", postLikeDto.getUserId()))
                .andExpect(status().isNotFound()).andExpect(status().isNotFound())
                .andExpect(content().string("Post not found"));

        verify(likeService, times(1))
                .likePost(postLikeDto.getPostId(), postLikeDto.getUserId());
    }

    @Test
    void testLikePostWhenLikeAlreadyExist() throws Exception {
        when(likeService.likePost(postLikeDto.getPostId(), postLikeDto.getUserId()))
                .thenThrow(new LikeAlreadyExistException("Like already exist"));

        mockMvc.perform(post("/posts/" + postLikeDto.getPostId() + "/like")
                        .header("x-user-id", postLikeDto.getUserId()))
                .andExpect(status().isConflict())
                .andExpect(content().string("Like already exist"));

        verify(likeService, times(1))
                .likePost(postLikeDto.getPostId(), postLikeDto.getUserId());
    }

    @Test
    void testUnlikePostWhenUnliked() throws Exception {

        mockMvc.perform(delete("/posts/" + postLikeDto.getPostId() + "/like")
                        .header("x-user-id", postLikeDto.getUserId()))
                .andExpect(status().isNoContent());

        verify(likeService, times(1))
                .unlikePost(postLikeDto.getPostId(), postLikeDto.getUserId());
    }

    @Test
    void testUnlikePostWhenLikeNotFound() throws Exception {
        doThrow(new LikeNotFoundException("Like not found"))
                .when(likeService).unlikePost(postLikeDto.getPostId(), postLikeDto.getUserId());

        mockMvc.perform(delete("/posts/" + postLikeDto.getPostId() + "/like")
                        .header("x-user-id", postLikeDto.getUserId()))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Like not found"));

        verify(likeService, times(1))
                .unlikePost(postLikeDto.getPostId(), postLikeDto.getUserId());
    }

    @Test
    void testLikeCommentWhenLikeCreated() throws Exception {
        when(likeService.likeComment(commentLikeDto.getCommentId(), commentLikeDto.getUserId()))
                .thenReturn(commentLikeDto);

        mockMvc.perform(post("/comments/" + commentLikeDto.getCommentId() + "/like")
                        .header("x-user-id", commentLikeDto.getUserId()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.commentId").value(commentLikeDto.getCommentId()))
                .andExpect(jsonPath("$.userId").value(commentLikeDto.getUserId()));

        verify(likeService, times(1))
                .likeComment(commentLikeDto.getCommentId(), commentLikeDto.getUserId());
    }

    @Test
    void testLikeCommentWhenCommentNotFound() throws Exception {
        when(likeService.likeComment(commentLikeDto.getCommentId(), commentLikeDto.getUserId()))
                .thenThrow(new PostNotFoundException("Comment not found"));

        mockMvc.perform(post("/comments/" + commentLikeDto.getCommentId() + "/like")
                        .header("x-user-id", commentLikeDto.getUserId()))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Comment not found"));

        verify(likeService, times(1))
                .likeComment(commentLikeDto.getCommentId(), commentLikeDto.getUserId());
    }

    @Test
    void testLikeCommentWhenLikeAlreadyExist() throws Exception {
        when(likeService.likeComment(commentLikeDto.getCommentId(), commentLikeDto.getUserId()))
                .thenThrow(new LikeAlreadyExistException("Like already exist"));

        mockMvc.perform(post("/comments/" + commentLikeDto.getCommentId() + "/like")
                        .header("x-user-id", commentLikeDto.getUserId()))
                .andExpect(status().isConflict())
                .andExpect(content().string("Like already exist"));

        verify(likeService, times(1))
                .likeComment(commentLikeDto.getCommentId(), commentLikeDto.getUserId());
    }

    @Test
    void testUnlikeCommentWhenUnliked() throws Exception {
        mockMvc.perform(delete("/comments/" + commentLikeDto.getCommentId() + "/like")
                        .header("x-user-id", commentLikeDto.getUserId()))
                .andExpect(status().isNoContent());

        verify(likeService, times(1))
                .unlikeComment(commentLikeDto.getCommentId(), commentLikeDto.getUserId());
    }

    @Test
    void testUnlikeCommentWhenLikeNotFound() throws Exception {
        doThrow(new LikeNotFoundException("Like not found"))
                .when(likeService).unlikeComment(commentLikeDto.getCommentId(), commentLikeDto.getUserId());

        mockMvc.perform(delete("/comments/" + commentLikeDto.getCommentId() + "/like")
                        .header("x-user-id", commentLikeDto.getUserId()))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Like not found"));

        verify(likeService, times(1))
                .unlikeComment(commentLikeDto.getCommentId(), commentLikeDto.getUserId());
    }
}