package faang.school.postservice.controller;

import faang.school.postservice.dto.like.LikeRequestDto;
import faang.school.postservice.dto.like.LikeResponseDto;
import faang.school.postservice.service.LikeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class LikeControllerTest {

    private static final long LIKE_ID = 1L;
    private static final long USER_ID = 2L;
    private static final long POST_ID = 3L;
    private static final long COMMENT_ID = 4L;

    private static final String DATE_TIME_STRING = "2025-02-01 12:00:00";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final LocalDateTime LOCAL_DATE_TIME_FROM_STRING = LocalDateTime.parse(DATE_TIME_STRING,
            DATE_TIME_FORMATTER);

    @Mock
    private LikeService likeService;

    @InjectMocks
    private LikeController likeController;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(likeController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("The test should return LikeResponseDto when a like on a post does not exist")
    void testLikePostSuccessful() throws Exception {
        LikeResponseDto likeResponseDto = new LikeResponseDto(LIKE_ID, USER_ID, null, POST_ID,
                LOCAL_DATE_TIME_FROM_STRING);

        LikeRequestDto likeRequestDto = new LikeRequestDto(USER_ID);

        Mockito.when(likeService.likePost(POST_ID, likeRequestDto)).thenReturn(likeResponseDto);

        mockMvc.perform(post("/api/v1/likes/{postId}/post", POST_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(likeResponseDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(likeResponseDto.getId()))
                .andExpect(jsonPath("$.userId").value(likeResponseDto.getUserId()))
                .andExpect(jsonPath("$.commentId").value(likeResponseDto.getCommentId()))
                .andExpect(jsonPath("$.postId").value(likeResponseDto.getPostId()))
                .andExpect(jsonPath("$.createdAt")
                        .value(likeResponseDto.getCreatedAt().format(DATE_TIME_FORMATTER)));

        ArgumentCaptor<Long> postIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<LikeRequestDto> likeRequestDtoCaptor = ArgumentCaptor.forClass(LikeRequestDto.class);

        Mockito.verify(likeService, Mockito.times(1))
                .likePost(postIdCaptor.capture(), likeRequestDtoCaptor.capture());

        Assertions.assertEquals(POST_ID, postIdCaptor.getValue());
        Assertions.assertEquals(likeRequestDto, likeRequestDtoCaptor.getValue());
    }

    @Test
    @DisplayName("The test should return LikeResponseDto when a like on a comment does not exist")
    void testLikeCommentSuccessful() throws Exception {
        LikeResponseDto likeResponseDto = new LikeResponseDto(LIKE_ID, USER_ID, COMMENT_ID, null,
                LOCAL_DATE_TIME_FROM_STRING);

        LikeRequestDto likeRequestDto = new LikeRequestDto(USER_ID);

        Mockito.when(likeService.likeComment(COMMENT_ID, likeRequestDto)).thenReturn(likeResponseDto);

        mockMvc.perform(post("/api/v1/likes/{commentId}/comment", COMMENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(likeResponseDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(likeResponseDto.getId()))
                .andExpect(jsonPath("$.userId").value(likeResponseDto.getUserId()))
                .andExpect(jsonPath("$.commentId").value(likeResponseDto.getCommentId()))
                .andExpect(jsonPath("$.postId").value(likeResponseDto.getPostId()))
                .andExpect(jsonPath("$.createdAt")
                        .value(likeResponseDto.getCreatedAt().format(DATE_TIME_FORMATTER)));

        ArgumentCaptor<Long> commentIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<LikeRequestDto> likeRequestDtoCaptor = ArgumentCaptor.forClass(LikeRequestDto.class);

        Mockito.verify(likeService, Mockito.times(1))
                .likeComment(commentIdCaptor.capture(), likeRequestDtoCaptor.capture());

        Assertions.assertEquals(COMMENT_ID, commentIdCaptor.getValue());
        Assertions.assertEquals(likeRequestDto, likeRequestDtoCaptor.getValue());
    }

    @Test
    @DisplayName("The test should return LikeResponseDto when a like on a post exists")
    void testRemoveLikeFromPostSuccessful() throws Exception {
        LikeResponseDto likeResponseDto = new LikeResponseDto(LIKE_ID, USER_ID, null, POST_ID,
                LOCAL_DATE_TIME_FROM_STRING);

        LikeRequestDto likeRequestDto = new LikeRequestDto(USER_ID);

        Mockito.when(likeService.removeLikeFromPost(POST_ID, likeRequestDto)).thenReturn(likeResponseDto);

        mockMvc.perform(delete("/api/v1/likes/{postId}/post/remove", POST_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(likeResponseDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(likeResponseDto.getId()))
                .andExpect(jsonPath("$.userId").value(likeResponseDto.getUserId()))
                .andExpect(jsonPath("$.commentId").value(likeResponseDto.getCommentId()))
                .andExpect(jsonPath("$.postId").value(likeResponseDto.getPostId()))
                .andExpect(jsonPath("$.createdAt")
                        .value(likeResponseDto.getCreatedAt().format(DATE_TIME_FORMATTER)));

        ArgumentCaptor<Long> postIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<LikeRequestDto> likeRequestDtoCaptor = ArgumentCaptor.forClass(LikeRequestDto.class);

        Mockito.verify(likeService, Mockito.times(1))
                .removeLikeFromPost(postIdCaptor.capture(), likeRequestDtoCaptor.capture());

        Assertions.assertEquals(POST_ID, postIdCaptor.getValue());
        Assertions.assertEquals(likeRequestDto, likeRequestDtoCaptor.getValue());
    }

    @Test
    @DisplayName("The test should return LikeResponseDto when a like on a comment exists")
    void testRemoveLikeFromCommentSuccessful() throws Exception {
        LikeResponseDto likeResponseDto = new LikeResponseDto(LIKE_ID, USER_ID, COMMENT_ID, null,
                LOCAL_DATE_TIME_FROM_STRING);

        LikeRequestDto likeRequestDto = new LikeRequestDto(USER_ID);

        Mockito.when(likeService.removeLikeFromComment(COMMENT_ID, likeRequestDto)).thenReturn(likeResponseDto);

        mockMvc.perform(delete("/api/v1/likes/{commentId}/comment/remove", COMMENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(likeResponseDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(likeResponseDto.getId()))
                .andExpect(jsonPath("$.userId").value(likeResponseDto.getUserId()))
                .andExpect(jsonPath("$.commentId").value(likeResponseDto.getCommentId()))
                .andExpect(jsonPath("$.postId").value(likeResponseDto.getPostId()))
                .andExpect(jsonPath("$.createdAt")
                        .value(likeResponseDto.getCreatedAt().format(DATE_TIME_FORMATTER)));

        ArgumentCaptor<Long> postIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<LikeRequestDto> likeRequestDtoCaptor = ArgumentCaptor.forClass(LikeRequestDto.class);

        Mockito.verify(likeService, Mockito.times(1))
                .removeLikeFromComment(postIdCaptor.capture(), likeRequestDtoCaptor.capture());

        Assertions.assertEquals(COMMENT_ID, postIdCaptor.getValue());
        Assertions.assertEquals(likeRequestDto, likeRequestDtoCaptor.getValue());
    }
}
