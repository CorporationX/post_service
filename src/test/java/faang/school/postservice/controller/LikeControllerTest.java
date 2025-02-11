package faang.school.postservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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

import static faang.school.postservice.LikeTestConstants.COMMENT_ID;
import static faang.school.postservice.LikeTestConstants.POST_ID;
import static faang.school.postservice.LikeTestConstants.USERS_WHO_LIKED_THE_COMMENT;
import static faang.school.postservice.LikeTestConstants.USERS_WHO_LIKED_THE_POST;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(MockitoExtension.class)
public class LikeControllerTest {

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
    @DisplayName("The test should return a list of UserDto's when a post exists")
    void testLikePostSuccessful() throws Exception {
        Mockito.when(likeService.getUsersWhoLikedPost(POST_ID)).thenReturn(USERS_WHO_LIKED_THE_POST);

        mockMvc.perform(get("/api/v1/likes/{postId}/post/users", POST_ID)
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
    void testLikeCommentSuccessful() throws Exception {
        Mockito.when(likeService.getUsersWhoLikedComment(COMMENT_ID)).thenReturn(USERS_WHO_LIKED_THE_COMMENT);

        mockMvc.perform(get("/api/v1/likes/{commentId}/comment/users", COMMENT_ID)
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
