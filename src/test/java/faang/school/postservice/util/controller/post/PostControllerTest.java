package faang.school.postservice.util.controller.post;

import java.util.List;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.controller.post.PostController;
import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.dto.post.PostDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.time.LocalDateTime;
import java.time.Month;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(MockitoExtension.class)
public class PostControllerTest {
    private MockMvc mockMvc;

    @Mock
    private UserContext userContext;

    @Mock
    private PostService postService;

    @InjectMocks
    private PostController postController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(postController).build();
    }

    @Test
    public void testGetPostByIdGetPost() throws Exception {
        long postId = 1;
        PostDto postDto = createFirstPostDtoForTest();
        when(postService.getPostById(postId)).thenReturn(postDto);

        mockMvc.perform(get("/api/v1/posts/{id}", postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

//    @Test
//    public void testGetAllUnpublishedPostsByAuthorGetPosts() throws Exception {
//                userContext.setUserId(1);
//        when(postService.getAllUnpublishedPostsByAuthor(userContext.getUserId()))
//                .thenReturn(List.of(createFirstPostDtoForTest(), createFirstPostDtoForTest()));
//
//        mockMvc.perform(get("/api/posts/unpublished/byauthor"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].id", is(1)));
//    }
//
//    @Test
//    public void testGetAllUnpublishedPostsByProjectGetPosts() throws Exception {
//        long projectId = 1;
//        when(postService.getAllUnpublishedPostsByProject(projectId))
//                .thenReturn(List.of(createFirstPostDtoForTest(), createFirstPostDtoForTest()));
//
//        mockMvc.perform(get("/api/posts/unpublished/byproject?project=1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].id", is(1)));
//    }
//
//    @Test
//    public void testGetAllPublishedPostsByAuthorGetPosts() throws Exception {
//        long authorId = 1;
//        when(postService.getAllPublishedPostsByAuthor(authorId))
//                .thenReturn(List.of(createFirstPostDtoForTest(), createFirstPostDtoForTest()));
//
//        mockMvc.perform(get("/api/posts/published/byauthor?id=1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].id", is(1)));
//
//    }
//
//    @Test
//    public void testGetAllPublishedPostsByProjectGetPosts() throws Exception {
//        long projectId = 1;
//        when(postService.getAllPublishedPostsByProject(projectId))
//                .thenReturn(List.of(createFirstPostDtoForTest(), createFirstPostDtoForTest()));
//
//        mockMvc.perform(get("/api/posts/published/byproject?project=1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].id", is(1)));
//    }

    private PostDto createFirstPostDtoForTest() {
        return new PostDto(1L,
                "1",
                1L,
                1L,
                List.of(1L, 2L),
                List.of(1L, 2L),
                List.of(1L, 2L),
                1L,
                List.of("1", "2"),
                false,
                LocalDateTime.of(2026, Month.JANUARY, 3, 21, 0),
                false,
                LocalDateTime.of(2026, Month.JANUARY, 3, 21,0),
                LocalDateTime.of(2026, Month.JANUARY, 3, 21,0));
    }

    private PostDto createSecondPostDtoForTest() {
        return new PostDto(2L,
                "2",
                2L,
                2L,
                List.of(3L, 2L),
                List.of(3L, 2L),
                List.of(3L, 2L),
                2L,
                List.of("3", "2"),
                true,
                LocalDateTime.of(2026, Month.JANUARY, 3, 21, 0),
                false,
                LocalDateTime.of(2026, Month.JANUARY, 3, 21,0),
                LocalDateTime.of(2026, Month.JANUARY, 3, 21,0));
    }

    private CreatePostDto createCreatePostDtoForTest() {
        return new CreatePostDto(
                "2",
                2L,
                List.of(3L, 2L),
                2L,
                List.of("3", "2"));
    }
}
