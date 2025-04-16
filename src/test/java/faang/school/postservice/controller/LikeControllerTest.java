package faang.school.postservice.controller;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.service.LikeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
public class LikeControllerTest {

    private static final Long POST_ID = 2L;
    private static final Long COMMENT_ID = 3L;
    private static final Long USER_ID = 1L;
    private static final LocalDateTime TIMESTAMP = LocalDateTime.now();

    private MockMvc mockMvc;
    private UserDto user;
    private LikeDto likeDtoPost;
    private LikeDto likeDtoComment;

    @Mock
    private LikeService likeService;
    @InjectMocks
    private LikeController likeController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(likeController).build();
        user = new UserDto(1L, "Join", "join@mail.ru");
        likeDtoPost = new LikeDto(USER_ID, POST_ID, null, TIMESTAMP);
        likeDtoComment = new LikeDto(USER_ID, null, COMMENT_ID, TIMESTAMP);
    }

    @Test
    public void testGetAllUsersWhoLikedPost() throws Exception {
        when(likeService.getAllUsersWhoLikedPost(POST_ID)).thenReturn(List.of(user));

        mockMvc.perform(get("/likes/posts/2").header("X-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username", is("Join")))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].email", is("join@mail.ru")));
    }

    @Test
    public void testGetAllUsersWhoLikedComment() throws Exception {
        when(likeService.getAllUsersWhoLikedComment(COMMENT_ID)).thenReturn(List.of(user));

        mockMvc.perform(get("/likes/comments/3").header("X-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username", is("Join")))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].email", is("join@mail.ru")));
    }

    @Test
    public void testLikePost() throws Exception {

        when(likeService.likePost(POST_ID)).thenReturn(likeDtoPost);

        mockMvc.perform(post("/likes/posts/{postId}/likes", POST_ID).header("X-User-Id", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(USER_ID.intValue())))
                .andExpect(jsonPath("$.postId", is(POST_ID.intValue())))
                .andExpect(jsonPath("$.commentId").doesNotExist())
                .andExpect(jsonPath("$.timestamp", matchesPattern("^" + TIMESTAMP.toString().substring(0, 23) + ".*$")));
    }

    @Test
    public void testRemoveLikeOnPost() throws Exception {

        when(likeService.removeLikeOnPost(POST_ID)).thenReturn(likeDtoPost);

        mockMvc.perform(delete("/likes/posts/{postId}/likes", POST_ID).header("X-User-Id", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(USER_ID.intValue())))
                .andExpect(jsonPath("$.postId", is(POST_ID.intValue())))
                .andExpect(jsonPath("$.commentId").doesNotExist())
                .andExpect(jsonPath("$.timestamp", matchesPattern("^" + TIMESTAMP.toString().substring(0, 23) + ".*$")));
    }

    @Test
    public void testLikeComment() throws Exception {

        when(likeService.likeComment(COMMENT_ID)).thenReturn(likeDtoComment);

        mockMvc.perform(post("/likes/comments/{commentId}/likes", COMMENT_ID).header("X-User-Id", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(USER_ID.intValue())))
                .andExpect(jsonPath("$.commentId", is(COMMENT_ID.intValue())))
                .andExpect(jsonPath("$.postId").doesNotExist())
                .andExpect(jsonPath("$.timestamp", matchesPattern("^" + TIMESTAMP.toString().substring(0, 23) + ".*$")));
    }

    @Test
    public void testRemoveLikeOnComment() throws Exception {

        when(likeService.removeLikeOnComment(COMMENT_ID)).thenReturn(likeDtoComment);

        mockMvc.perform(delete("/likes/comments/{commentId}/likes", COMMENT_ID).header("X-User-Id", USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(USER_ID.intValue())))
                .andExpect(jsonPath("$.commentId", is(COMMENT_ID.intValue())))
                .andExpect(jsonPath("$.postId").doesNotExist())
                .andExpect(jsonPath("$.timestamp", matchesPattern("^" + TIMESTAMP.toString().substring(0, 23) + ".*$")));
    }

    @Test
    public void testGetCountLikesPost() throws Exception {

        Long authorId = 10L;
        Long projectId = 20L;
        Long likes = 15L;
        PostDto postDto = new PostDto(POST_ID, authorId, projectId, likes);
        when(likeService.countLikesPost(POST_ID)).thenReturn(postDto);

        mockMvc.perform(get("/likes/posts/{postId}/countLikes", POST_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(POST_ID.intValue())))
                .andExpect(jsonPath("$.authorId", is(authorId.intValue())))
                .andExpect(jsonPath("$.projectId", is(projectId.intValue())))
                .andExpect(jsonPath("$.likes", is(likes.intValue())));
    }
}
