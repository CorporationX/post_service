package faang.school.postservice.controller.like;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.like.LikeCommentDto;
import faang.school.postservice.dto.like.LikePostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.like.LikeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = {LikeController.class})
class LikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LikeService likeService;

    @MockBean
    private UserServiceClient userServiceClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void likePost_shouldReturnLikePostDto_whenRequestDataIsValid() throws Exception {
        LikePostDto likePostDto = new LikePostDto(1L, 2L, 3L);
        when(likeService.likePost(any(LikePostDto.class))).thenReturn(likePostDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/likes/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(likePostDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").value(likePostDto.postId()))
                .andExpect(jsonPath("$.userId").value(likePostDto.userId()))
                .andExpect(jsonPath("$.id").value(likePostDto.id()));
    }

    @ParameterizedTest
    @MethodSource("invalidLikePostDto")
    void likePost_shouldReturnBadRequest_whenRequestDataIsNotValid(LikePostDto likePostDto) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/likes/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(likePostDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void unlikePost_shouldReturnStatusOk_whenRequestDataIsValid() throws Exception {
        Long userId = 1L;
        Long postId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/likes/posts/{postId}/users/{userId}", postId, userId))
                .andExpect(status().isOk());

        Mockito.verify(likeService).unlikePost(postId, userId);
    }

    @Test
    void likeComment_shouldReturnLikeCommentDto_whenRequestDataIsValid() throws Exception {
        LikeCommentDto likeCommentDto = new LikeCommentDto(1L, 2L, 3L, 4L);
        when(likeService.likeComment(any(LikeCommentDto.class))).thenReturn(likeCommentDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/likes/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(likeCommentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").value(likeCommentDto.postId()))
                .andExpect(jsonPath("$.userId").value(likeCommentDto.userId()))
                .andExpect(jsonPath("$.commentId").value(likeCommentDto.commentId()))
                .andExpect(jsonPath("$.id").value(likeCommentDto.id()));
    }

    @ParameterizedTest
    @MethodSource("invalidLikeCommentDto")
    void likeComment_shouldReturnBadRequest_whenRequestDataIsNotValid(LikeCommentDto likeCommentDto) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/likes/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(likeCommentDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void unlikeComment_shouldReturnStatusOk_whenRequestDataIsValid() throws Exception {
        Long userId = 1L;
        Long commentId = 1L;
        UserDto mockUserDto = new UserDto(userId, "testuser", "test@example.com", null, null, null);

        when(userServiceClient.getUser(userId)).thenReturn(mockUserDto);

        mockMvc.perform(MockMvcRequestBuilders.delete("/likes/comments/{commentId}/users/{userId}", commentId, userId))
                .andExpect(status().isOk());

        Mockito.verify(likeService).unlikeComment(commentId, userId);
    }

    static Stream<LikePostDto> invalidLikePostDto(){
        return Stream.of(
                LikePostDto.builder()
                        .id(null)
                        .userId(1L)
                        .build(),

                LikePostDto.builder()
                        .postId(1L)
                        .build()
        );
    }

    static Stream<LikeCommentDto> invalidLikeCommentDto(){
        return Stream.of(
                LikeCommentDto.builder().userId(1L).build(),
                        LikeCommentDto.builder().postId(1L).build(),
                        LikeCommentDto.builder().commentId(1L).build()
        );
    }
}
