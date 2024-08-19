package faang.school.postservice.controller.like;

import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.like.LikeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class LikeControllerTest {

    @Mock
    private LikeService likeService;

    @InjectMocks
    private LikeController likeController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(likeController).build();
    }

    @Test
    void testGetUsersByPostIdSuccess() throws Exception {
        Long postId = 1L;
        List<UserDto> users = List.of(
                new UserDto(1L, "John", "john@example.com"),
                new UserDto(2L, "Jane", "jane@example.com")
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
                new UserDto(1L, "John", "john@example.com"),
                new UserDto(2L, "Jane", "jane@example.com")
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
}

