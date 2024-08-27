package faang.school.postservice.controller.like;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.like.LikeService;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
public class LikeControllerTest {

    @Mock
    private LikeService likeService;

    @InjectMocks
    private LikeController likeController;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static LocalDateTime localDateTime = LocalDateTime.of(2024, Month.AUGUST, 24, 0, 0);

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(likeController).build();
    }

    @Test
    void testGetUsersByPostIdSuccess() throws Exception {
        Long postId = 1L;
        List<UserDto> users = List.of(
                new UserDto(1L, "John", "john@example.com", List.of(1L)),
                new UserDto(2L, "Jane", "jane@example.com",  List.of(2L))
        );
        when(likeService.getUsersByPostId(postId)).thenReturn(users);

        mockMvc.perform(get("http://localhost:8081/likes/post/{postId}", postId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[{\"id\":1,\"username\":\"John\",\"email\":\"john@example.com\"},{\"id\":2,\"username\":\"Jane\",\"email\":\"jane@example.com\"}]"));
    }

    @Test
    void testGetUsersByPostIdEmptyList() throws Exception {
        Long postId = 1L;
        when(likeService.getUsersByPostId(postId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("http://localhost:8081/likes/post/{postId}", postId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[]"));
    }

    @Test
    void testGetUsersByCommentIdSuccess() throws Exception {
        Long commentId = 1L;
        List<UserDto> users = List.of(
                new UserDto(1L, "John", "john@example.com", List.of(1L)),
                new UserDto(2L, "Jane", "jane@example.com", List.of(1L))
        );
        when(likeService.getUsersByCommentId(commentId)).thenReturn(users);

        mockMvc.perform(get("http://localhost:8081/likes/comment/{commentId}", commentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[{\"id\":1,\"username\":\"John\",\"email\":\"john@example.com\"},{\"id\":2,\"username\":\"Jane\",\"email\":\"jane@example.com\"}]"));
    }

    @Test
    void testGetUsersByCommentIdEmptyList() throws Exception {
        Long commentId = 1L;
        when(likeService.getUsersByCommentId(commentId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("http://localhost:8081/likes/comment/{commentId}", commentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[]"));
    }

    @Test
    void testAddLikeToPost() throws Exception {
        when(likeService.addLikeToPost(1L, getLikeDto())).thenReturn(getLikeDto());

        mockMvc.perform(post("/likes/post/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(getLikeDto()))
                .header("x-user-id", 1)
                .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.userId").value(1L))
            .andExpect(jsonPath("$.commentId").value(1L))
            .andExpect(jsonPath("$.postId").value(1L));
    }

    @Test
    void testAddLikeToComment() throws Exception {
        when(likeService.addLikeToComment(1L, getLikeDto())).thenReturn(getLikeDto());

        mockMvc.perform(post("/likes/comment/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(getLikeDto()))
                .header("x-user-id", 1)
                .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.userId").value(1L))
            .andExpect(jsonPath("$.commentId").value(1L))
            .andExpect(jsonPath("$.postId").value(1L));
    }

    @Test
    void testRemoveLikeFromPost() throws Exception {
        when(likeService.removeLikeFromPost(1L, getLikeDto())).thenReturn(getLikeDto());

        mockMvc.perform(delete("/likes/post/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(getLikeDto()))
                .header("x-user-id", 1)
                .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.userId").value(1L))
            .andExpect(jsonPath("$.commentId").value(1L))
            .andExpect(jsonPath("$.postId").value(1L));

    }

    @Test
    void testRemoveLikeFromComment() throws Exception {
        when(likeService.removeLikeFromComment(1L, getLikeDto())).thenReturn(getLikeDto());

        mockMvc.perform(delete("/likes/comment/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(getLikeDto()))
                .header("x-user-id", 1)
                .accept(MediaType.APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(1L))
            .andExpect(jsonPath("$.userId").value(1L))
            .andExpect(jsonPath("$.commentId").value(1L))
            .andExpect(jsonPath("$.postId").value(1L));

    }

    private static LikeDto getLikeDto() {
        return LikeDto.builder()
            .id(1L)
            .userId(1L)
            .commentId(1L)
            .postId(1L)
            .build();
    }

}

