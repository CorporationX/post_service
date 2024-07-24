package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.validate.post.PostValidator;
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

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {

    @InjectMocks
    private PostController postController;

    @Mock
    private PostService postService;

    @Mock
    private PostValidator postValidator;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    private PostDto postDto;
    private List<PostDto> expectedPostsDto;
    private final long defaultId = 1;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(postController).build();

        postDto = PostDto.builder()
                .id(1L)
                .content("Some content")
                .authorId(1L)
                .published(false)
                .deleted(false)
                .build();

        expectedPostsDto = List.of(postDto);
    }

    @Test
    void testCreate() throws Exception {
        doNothing().when(postValidator).validateCreate(postDto);
        when(postService.create(postDto)).thenReturn(postDto);

        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.content", is("Some content")))
                .andExpect(jsonPath("$.authorId", is(1)))
                .andExpect(jsonPath("$.published", is(false)))
                .andExpect(jsonPath("$.deleted", is(false)));
    }

    @Test
    void testPublish() throws Exception {
        postDto.setPublished(true);

        when(postService.publish(defaultId)).thenReturn(postDto);

        mockMvc.perform(post("/posts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.content", is("Some content")))
                .andExpect(jsonPath("$.authorId", is(1)))
                .andExpect(jsonPath("$.published", is(true)))
                .andExpect(jsonPath("$.deleted", is(false)));
    }

    @Test
    void testUpdate() throws Exception {
        postDto.setContent("Changed content");
        when(postService.update(defaultId, postDto)).thenReturn(postDto);

        mockMvc.perform(put("/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.content", is("Changed content")))
                .andExpect(jsonPath("$.authorId", is(1)))
                .andExpect(jsonPath("$.published", is(false)))
                .andExpect(jsonPath("$.deleted", is(false)));
    }

    @Test
    void testSoftDelete() throws Exception {
        postDto.setDeleted(true);

        when(postService.softDelete(defaultId)).thenReturn(postDto);

        mockMvc.perform(delete("/posts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.content", is("Some content")))
                .andExpect(jsonPath("$.authorId", is(1)))
                .andExpect(jsonPath("$.published", is(false)))
                .andExpect(jsonPath("$.deleted", is(true)));
    }

    @Test
    void testGetById() throws Exception {
        when(postService.getById(1L)).thenReturn(postDto);

        mockMvc.perform(get("/posts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id", is(1)))
                .andExpect(jsonPath("content", is("Some content")))
                .andExpect(jsonPath("authorId", is(1)))
                .andExpect(jsonPath("published", is(false)))
                .andExpect(jsonPath("deleted", is(false)));
    }

    @Test
    void testGetAllPublishedPostsForAuthor() throws Exception {
        when(postService.getAllPublishedPostsForAuthor(defaultId)).thenReturn(expectedPostsDto);

        mockMvc.perform(get("/posts/published/author/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].content", is("Some content")))
                .andExpect(jsonPath("$[0].authorId", is(1)))
                .andExpect(jsonPath("$[0].published", is(false)))
                .andExpect(jsonPath("$[0].deleted", is(false)));
    }

    @Test
    void testGetAllPublishedPostsForProject() throws Exception {
        when(postService.getAllPublishedPostsForProject(defaultId)).thenReturn(expectedPostsDto);

        mockMvc.perform(get("/posts/published/project/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].content", is("Some content")))
                .andExpect(jsonPath("$[0].authorId", is(1)))
                .andExpect(jsonPath("$[0].published", is(false)))
                .andExpect(jsonPath("$[0].deleted", is(false)));
    }

    @Test
    void testGetAllUnPublishedPostsForAuthor() throws Exception {
        when(postService.getAllUnpublishedPostsForAuthor(defaultId)).thenReturn(expectedPostsDto);

        mockMvc.perform(get("/posts/unpublished/author/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].content", is("Some content")))
                .andExpect(jsonPath("$[0].authorId", is(1)))
                .andExpect(jsonPath("$[0].published", is(false)))
                .andExpect(jsonPath("$[0].deleted", is(false)));
    }

    @Test
    void testGetAllUnPublishedPostsForProject() throws Exception {
        when(postService.getAllUnpublishedPostsForProject(defaultId)).thenReturn(expectedPostsDto);

        mockMvc.perform(get("/posts/unpublished/project/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].content", is("Some content")))
                .andExpect(jsonPath("$[0].authorId", is(1)))
                .andExpect(jsonPath("$[0].published", is(false)))
                .andExpect(jsonPath("$[0].deleted", is(false)));
    }
}