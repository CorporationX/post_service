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

import static faang.school.postservice.LikeTestConstants.COMMENT_ID;
import static faang.school.postservice.LikeTestConstants.LIKE_ID;
import static faang.school.postservice.LikeTestConstants.POST_ID;
import static faang.school.postservice.LikeTestConstants.USERS_WHO_LIKED_THE_COMMENT;
import static faang.school.postservice.LikeTestConstants.USERS_WHO_LIKED_THE_POST;
import static faang.school.postservice.LikeTestConstants.USER_ID;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(MockitoExtension.class)
public class LikeControllerTest {

    private static final String DATE_TIME_STRING = "2025-02-01 12:00:00";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final LocalDateTime LOCAL_DATE_TIME_FROM_STRING = LocalDateTime.parse(DATE_TIME_STRING,
            DATE_TIME_FORMATTER);

    @Mock
    private LikeService likeService;

    @InjectMocks
    private LikeController likeController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(likeController).build();
    }

    @Test
    @DisplayName("The test should return LikeResponseDto when a like on a post does not exist")
    void testLikePostSuccessful() throws Exception {
        LikeResponseDto likeResponseDto = new LikeResponseDto(LIKE_ID, USER_ID, null, POST_ID,
                LOCAL_DATE_TIME_FROM_STRING);

        LikeRequestDto likeRequestDto = new LikeRequestDto(USER_ID);

        Mockito.when(likeService.likePost(POST_ID, likeRequestDto)).thenReturn(likeResponseDto);

        mockMvc.perform(post("/api/v1/likes/post/{postId}", POST_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(likeResponseDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(likeResponseDto.id()))
                .andExpect(jsonPath("$.userId").value(likeResponseDto.userId()))
                .andExpect(jsonPath("$.commentId").value(likeResponseDto.commentId()))
                .andExpect(jsonPath("$.postId").value(likeResponseDto.postId()))
                .andExpect(jsonPath("$.createdAt")
                        .value(likeResponseDto.createdAt().format(DATE_TIME_FORMATTER)));

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

        mockMvc.perform(post("/api/v1/likes/comment/{commentId}", COMMENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(likeResponseDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(likeResponseDto.id()))
                .andExpect(jsonPath("$.userId").value(likeResponseDto.userId()))
                .andExpect(jsonPath("$.commentId").value(likeResponseDto.commentId()))
                .andExpect(jsonPath("$.postId").value(likeResponseDto.postId()))
                .andExpect(jsonPath("$.createdAt")
                        .value(likeResponseDto.createdAt().format(DATE_TIME_FORMATTER)));

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

        mockMvc.perform(delete("/api/v1/likes/post/{postId}", POST_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(likeResponseDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(likeResponseDto.id()))
                .andExpect(jsonPath("$.userId").value(likeResponseDto.userId()))
                .andExpect(jsonPath("$.commentId").value(likeResponseDto.commentId()))
                .andExpect(jsonPath("$.postId").value(likeResponseDto.postId()))
                .andExpect(jsonPath("$.createdAt")
                        .value(likeResponseDto.createdAt().format(DATE_TIME_FORMATTER)));

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

        mockMvc.perform(delete("/api/v1/likes/comment/{commentId}", COMMENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(likeResponseDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(likeResponseDto.id()))
                .andExpect(jsonPath("$.userId").value(likeResponseDto.userId()))
                .andExpect(jsonPath("$.commentId").value(likeResponseDto.commentId()))
                .andExpect(jsonPath("$.postId").value(likeResponseDto.postId()))
                .andExpect(jsonPath("$.createdAt")
                        .value(likeResponseDto.createdAt().format(DATE_TIME_FORMATTER)));

        ArgumentCaptor<Long> postIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<LikeRequestDto> likeRequestDtoCaptor = ArgumentCaptor.forClass(LikeRequestDto.class);

        Mockito.verify(likeService, Mockito.times(1))
                .removeLikeFromComment(postIdCaptor.capture(), likeRequestDtoCaptor.capture());

        Assertions.assertEquals(COMMENT_ID, postIdCaptor.getValue());
        Assertions.assertEquals(likeRequestDto, likeRequestDtoCaptor.getValue());
    }

    @Test
    @DisplayName("The test should return a list of UserDto's when a post exists")
    void testGetUsersWhoLikedPostSuccessful() throws Exception {
        Mockito.when(likeService.getUsersWhoLikedPost(POST_ID)).thenReturn(USERS_WHO_LIKED_THE_POST);

        mockMvc.perform(get("/api/v1/likes/post/{postId}/users", POST_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(USERS_WHO_LIKED_THE_POST)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(USERS_WHO_LIKED_THE_POST.get(0).id()))
                .andExpect(jsonPath("$[0].username").value(USERS_WHO_LIKED_THE_POST.get(0).username()))
                .andExpect(jsonPath("$[0].email").value(USERS_WHO_LIKED_THE_POST.get(0).email()))
                .andExpect(jsonPath("$[1].id").value(USERS_WHO_LIKED_THE_POST.get(1).id()))
                .andExpect(jsonPath("$[1].username").value(USERS_WHO_LIKED_THE_POST.get(1).username()))
                .andExpect(jsonPath("$[1].email").value(USERS_WHO_LIKED_THE_POST.get(1).email()))
                .andExpect(jsonPath("$[2].id").value(USERS_WHO_LIKED_THE_POST.get(2).id()))
                .andExpect(jsonPath("$[2].username").value(USERS_WHO_LIKED_THE_POST.get(2).username()))
                .andExpect(jsonPath("$[2].email").value(USERS_WHO_LIKED_THE_POST.get(2).email()))
                .andExpect(jsonPath("$[3].id").value(USERS_WHO_LIKED_THE_POST.get(3).id()))
                .andExpect(jsonPath("$[3].username").value(USERS_WHO_LIKED_THE_POST.get(3).username()))
                .andExpect(jsonPath("$[3].email").value(USERS_WHO_LIKED_THE_POST.get(3).email()))
                .andExpect(jsonPath("$[4].id").value(USERS_WHO_LIKED_THE_POST.get(4).id()))
                .andExpect(jsonPath("$[4].username").value(USERS_WHO_LIKED_THE_POST.get(4).username()))
                .andExpect(jsonPath("$[4].email").value(USERS_WHO_LIKED_THE_POST.get(4).email()))
                .andExpect(jsonPath("$[5].id").value(USERS_WHO_LIKED_THE_POST.get(5).id()))
                .andExpect(jsonPath("$[5].username").value(USERS_WHO_LIKED_THE_POST.get(5).username()))
                .andExpect(jsonPath("$[5].email").value(USERS_WHO_LIKED_THE_POST.get(5).email()));

        ArgumentCaptor<Long> postIdCaptor = ArgumentCaptor.forClass(Long.class);

        Mockito.verify(likeService, Mockito.times(1))
                .getUsersWhoLikedPost(postIdCaptor.capture());

        Assertions.assertEquals(POST_ID, postIdCaptor.getValue());
    }

    @Test
    @DisplayName("The test should return a list of UserDto's when a comment exists")
    void testGetUsersWhoLikedCommentSuccessful() throws Exception {
        Mockito.when(likeService.getUsersWhoLikedComment(COMMENT_ID)).thenReturn(USERS_WHO_LIKED_THE_COMMENT);

        mockMvc.perform(get("/api/v1/likes/comment/{commentId}/users", COMMENT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(USERS_WHO_LIKED_THE_COMMENT)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(USERS_WHO_LIKED_THE_COMMENT.get(0).id()))
                .andExpect(jsonPath("$[0].username").value(USERS_WHO_LIKED_THE_COMMENT.get(0).username()))
                .andExpect(jsonPath("$[0].email").value(USERS_WHO_LIKED_THE_COMMENT.get(0).email()))
                .andExpect(jsonPath("$[1].id").value(USERS_WHO_LIKED_THE_COMMENT.get(1).id()))
                .andExpect(jsonPath("$[1].username").value(USERS_WHO_LIKED_THE_COMMENT.get(1).username()))
                .andExpect(jsonPath("$[1].email").value(USERS_WHO_LIKED_THE_COMMENT.get(1).email()))
                .andExpect(jsonPath("$[2].id").value(USERS_WHO_LIKED_THE_COMMENT.get(2).id()))
                .andExpect(jsonPath("$[2].username").value(USERS_WHO_LIKED_THE_COMMENT.get(2).username()))
                .andExpect(jsonPath("$[2].email").value(USERS_WHO_LIKED_THE_COMMENT.get(2).email()))
                .andExpect(jsonPath("$[3].id").value(USERS_WHO_LIKED_THE_COMMENT.get(3).id()))
                .andExpect(jsonPath("$[3].username").value(USERS_WHO_LIKED_THE_COMMENT.get(3).username()))
                .andExpect(jsonPath("$[3].email").value(USERS_WHO_LIKED_THE_COMMENT.get(3).email()))
                .andExpect(jsonPath("$[4].id").value(USERS_WHO_LIKED_THE_COMMENT.get(4).id()))
                .andExpect(jsonPath("$[4].username").value(USERS_WHO_LIKED_THE_COMMENT.get(4).username()))
                .andExpect(jsonPath("$[4].email").value(USERS_WHO_LIKED_THE_COMMENT.get(4).email()))
                .andExpect(jsonPath("$[5].id").value(USERS_WHO_LIKED_THE_COMMENT.get(5).id()))
                .andExpect(jsonPath("$[5].username").value(USERS_WHO_LIKED_THE_COMMENT.get(5).username()))
                .andExpect(jsonPath("$[5].email").value(USERS_WHO_LIKED_THE_COMMENT.get(5).email()));

        ArgumentCaptor<Long> commentIdCaptor = ArgumentCaptor.forClass(Long.class);

        Mockito.verify(likeService, Mockito.times(1))
                .getUsersWhoLikedComment(commentIdCaptor.capture());

        Assertions.assertEquals(COMMENT_ID, commentIdCaptor.getValue());
    }
}
