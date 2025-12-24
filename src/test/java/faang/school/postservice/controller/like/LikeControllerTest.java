package faang.school.postservice.controller.like;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.like.LikeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class LikeControllerTest {

    private MockMvc mockMvc;
    @Mock
    private LikeServiceImpl likeService;
    @InjectMocks
    private LikeController likeController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(likeController).build();
    }

    @Test
    public void testAddLikeToPostSuccessful() throws Exception {
        final long postId = 5L;
        final long userId = 2L;
        final long likeId = 1L;
        final LikeDto responseLikeDto = initPostLike(postId, userId, likeId);

        when(likeService.addLikeToPost(postId)).thenReturn(responseLikeDto);

        mockMvc.perform(post("/api/v1/posts/{postId}/likes", postId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.userId", is(2)))
                .andExpect(jsonPath("$.postId", is(5)))
                .andExpect(jsonPath("$.commentId", nullValue()))
                .andExpect(jsonPath("$.createdAt", notNullValue()));

        verify(likeService, times(1)).addLikeToPost(postId);
    }

    @Test
    public void testRemoveLikeFromPostSuccessful() throws Exception {
        final long postId = 5L;

        doNothing().when(likeService).removeLikeFromPost(postId);

        mockMvc.perform(delete("/api/v1/posts/{postId}/likes", postId))
                .andExpect(status().isNoContent());

        verify(likeService, times(1)).removeLikeFromPost(postId);
    }

    @Test
    public void testAddLikeToCommentSuccessful() throws Exception {
        final long commentId = 7L;
        final long userId = 2L;
        final long likeId = 1L;
        final LikeDto responseLikeDto = initCommentLike(commentId, userId, likeId);

        when(likeService.addLikeToComment(commentId)).thenReturn(responseLikeDto);

        mockMvc.perform(post("/api/v1/comments/{commentId}/likes", commentId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.userId", is(2)))
                .andExpect(jsonPath("$.postId", nullValue()))
                .andExpect(jsonPath("$.commentId", is(7)))
                .andExpect(jsonPath("$.createdAt", notNullValue()));

        verify(likeService, times(1)).addLikeToComment(commentId);
    }

    @Test
    public void testRemoveLikeFromCommentSuccessful() throws Exception {
        final long commentId = 7L;

        doNothing().when(likeService).removeLikeFromComment(commentId);

        mockMvc.perform(delete("/api/v1/comments/{commentId}/likes", commentId))
                .andExpect(status().isNoContent());

        verify(likeService, times(1)).removeLikeFromComment(commentId);
    }

    @Test
    public void testGetLikesFromPostSuccessful() throws Exception {
        final long postId = 5L;
        final long userIdOffset = 2L;
        final List<LikeDto> responseLikeDtos = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            responseLikeDtos.add(initPostLike(postId, i + userIdOffset, i));
        }

        when(likeService.getLikesFromPost(postId)).thenReturn(responseLikeDtos);

        mockMvc.perform(get("/api/v1/posts/{postId}/likes", postId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(responseLikeDtos.size())))
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[0].userId", is(3)))
                .andExpect(jsonPath("$.[1].id", is(2)))
                .andExpect(jsonPath("$.[1].userId", is(4)));

        verify(likeService, times(1)).getLikesFromPost(postId);
    }

    private LikeDto initPostLike(long postId, long userId, long likeId) {
        return LikeDto.builder()
                .id(likeId)
                .userId(userId)
                .postId(postId)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private LikeDto initCommentLike(long commentId, long userId, long likeId) {
        return LikeDto.builder()
                .id(likeId)
                .userId(userId)
                .commentId(commentId)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
