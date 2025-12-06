package faang.school.postservice.controller.like;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.like.LikeServiceImpl;
import faang.school.postservice.util.like.LikeValidator;
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
import static org.mockito.Mockito.never;
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
    private ObjectMapper objectMapper;
    @Mock
    private LikeServiceImpl likeService;
    @Mock
    private LikeValidator likeValidator;
    @InjectMocks
    private LikeController likeController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(likeController).build();

        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Test
    public void testAddLikeToPostInvalidUserId() throws Exception {
        final long postId = 5L;
        final LikeDto requestLikeDto = initPostLike(postId, null);

        mockMvc.perform(post("/api/v1/posts/{postId}/likes", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestLikeDto)))
                .andExpect(status().isBadRequest());

        verify(likeValidator, never()).validateLike(postId, requestLikeDto);
        verify(likeService, never()).addLikeToPost(postId, requestLikeDto);
    }

    @Test
    public void testAddLikeToPostSuccessful() throws Exception {
        final long postId = 5L;
        final long userId = 2L;
        final long likeId = 1L;
        final LikeDto requestLikeDto = initPostLike(postId, userId);
        final LikeDto responseLikeDto = initPostLike(postId, userId, likeId);

        doNothing().when(likeValidator).validateLike(postId, requestLikeDto);
        when(likeService.addLikeToPost(postId, requestLikeDto)).thenReturn(responseLikeDto);

        mockMvc.perform(post("/api/v1/posts/{postId}/likes", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestLikeDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.userId", is(2)))
                .andExpect(jsonPath("$.postId", is(5)))
                .andExpect(jsonPath("$.commentId", nullValue()))
                .andExpect(jsonPath("$.createdAt", notNullValue()));

        verify(likeValidator, times(1)).validateLike(postId, requestLikeDto);
        verify(likeService, times(1)).addLikeToPost(postId, requestLikeDto);
    }

    @Test
    public void testRemoveLikeFromPostSuccessful() throws Exception {
        final long postId = 5L;

        doNothing().when(likeService).removeLikeFromPost(postId);

        mockMvc.perform(delete("/api/v1/posts/{postId}/likes", postId))
                .andExpect(status().isOk());

        verify(likeService, times(1)).removeLikeFromPost(postId);
    }

    @Test
    public void testAddLikeToCommentInvalidUserId() throws Exception {
        final long commentId = 7L;
        final LikeDto requestLikeDto = initCommentLike(commentId, null);

        mockMvc.perform(post("/api/v1/comments/{commentId}/likes", commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestLikeDto)))
                .andExpect(status().isBadRequest());

        verify(likeValidator, never()).validateLike(commentId, requestLikeDto);
        verify(likeService, never()).addLikeToComment(commentId, requestLikeDto);
    }

    @Test
    public void testAddLikeToCommentSuccessful() throws Exception {
        final long commentId = 7L;
        final long userId = 2L;
        final long likeId = 1L;
        final LikeDto requestLikeDto = initCommentLike(commentId, userId);
        final LikeDto responseLikeDto = initCommentLike(commentId, userId, likeId);

        doNothing().when(likeValidator).validateLike(commentId, requestLikeDto);
        when(likeService.addLikeToComment(commentId, requestLikeDto)).thenReturn(responseLikeDto);

        mockMvc.perform(post("/api/v1/comments/{commentId}/likes", commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestLikeDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.userId", is(2)))
                .andExpect(jsonPath("$.postId", nullValue()))
                .andExpect(jsonPath("$.commentId", is(7)))
                .andExpect(jsonPath("$.createdAt", notNullValue()));

        verify(likeValidator, times(1)).validateLike(commentId, requestLikeDto);
        verify(likeService, times(1)).addLikeToComment(commentId, requestLikeDto);
    }

    @Test
    public void testRemoveLikeFromCommentSuccessful() throws Exception {
        final long commentId = 7L;

        doNothing().when(likeService).removeLikeFromComment(commentId);

        mockMvc.perform(delete("/api/v1/comments/{commentId}/likes", commentId))
                .andExpect(status().isOk());

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

    private LikeDto initPostLike(long postId, Long userId) {
        return LikeDto.builder()
                .userId(userId)
                .postId(postId)
                .build();
    }

    private LikeDto initPostLike(long postId, long userId, long likeId) {
        return LikeDto.builder()
                .id(likeId)
                .userId(userId)
                .postId(postId)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private LikeDto initCommentLike(long commentId, Long userId) {
        return LikeDto.builder()
                .userId(userId)
                .commentId(commentId)
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
