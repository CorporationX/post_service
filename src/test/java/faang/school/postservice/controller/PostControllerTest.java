package faang.school.postservice.controller;

import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.dto.post.ResponsePostDto;
import faang.school.postservice.service.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {
    private MockMvc mockMvc;

    @Mock
    private PostService postService;

    @InjectMocks
    private PostController postController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(postController).build();
    }

    @Test
    void createPostShouldReturnCreatedPost() throws Exception {
        CreatePostDto createPostDto = new CreatePostDto();
        createPostDto.setContent("Test content");
        createPostDto.setAuthorId(1L);
        createPostDto.setProjectId(1L);

        ResponsePostDto responsePostDto = new ResponsePostDto();
        responsePostDto.setContent("Test content");

        when(postService.create(any(CreatePostDto.class), anyList())).thenReturn(responsePostDto);

        MockMultipartFile createPostDtoPart = new MockMultipartFile(
                "createPostDto",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                new ObjectMapper().writeValueAsBytes(createPostDto)
        );

        MockMultipartFile filePart = new MockMultipartFile(
                "file",
                "test-image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "Test Image Content".getBytes()
        );

        mockMvc.perform(multipart("/posts")
                        .file(createPostDtoPart)
                        .file(filePart)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("Test content"));

        verify(postService, times(1)).create(any(CreatePostDto.class), anyList());
    }

    @Test
    void publishPostShouldReturnPublishedPost() throws Exception {
        Long postId = 1L;
        ResponsePostDto responsePostDto = new ResponsePostDto();
        responsePostDto.setContent("Test content");
        responsePostDto.setPublished(true);

        when(postService.publish(postId)).thenReturn(responsePostDto);

        mockMvc.perform(put("/posts/{postId}/publish", postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.published").value(true))
                .andExpect(jsonPath("$.content").value("Test content"));

        verify(postService, times(1)).publish(postId);
    }

    @Test
    void updatePostShouldReturnUpdatedPost() throws Exception {
        Long postId = 1L;
        UpdatePostDto updatePostDto = new UpdatePostDto();
        updatePostDto.setContent("Updated content");
        ResponsePostDto responsePostDto = new ResponsePostDto();
        responsePostDto.setContent("Updated content");

        when(postService.update(postId, updatePostDto)).thenReturn(responsePostDto);

        mockMvc.perform(put("/posts/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatePostDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Updated content"));

        verify(postService, times(1)).update(postId, updatePostDto);
    }

    @Test
    void deletePostShouldReturnNoContent() throws Exception {
        Long postId = 1L;

        doNothing().when(postService).delete(postId);

        mockMvc.perform(delete("/posts/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(postService, times(1)).delete(postId);
    }

    @Test
    void getPostByIdShouldReturnPostDto() throws Exception {
        Long postId = 1L;
        ResponsePostDto responsePostDto = new ResponsePostDto();
        responsePostDto.setId(postId);
        responsePostDto.setContent("Post content");

        when(postService.getById(postId)).thenReturn(responsePostDto);

        mockMvc.perform(get("/posts/{postId}", postId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(postId))
                .andExpect(jsonPath("$.content").value("Post content"));

        verify(postService, times(1)).getById(postId);
    }

    @Test
    void getDraftByUserIdShouldReturnPostDtos() throws Exception {
        Long userId = 1L;
        ResponsePostDto responsePostDto1 = new ResponsePostDto();
        responsePostDto1.setId(1L);
        responsePostDto1.setContent("Draft content 1");

        ResponsePostDto responsePostDto2 = new ResponsePostDto();
        responsePostDto2.setId(2L);
        responsePostDto2.setContent("Draft content 2");

        List<ResponsePostDto> responsePostDtos = Arrays.asList(responsePostDto1, responsePostDto2);

        when(postService.getDraftsByUserId(userId)).thenReturn(responsePostDtos);

        mockMvc.perform(get("/posts/draft/author/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].content").value("Draft content 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].content").value("Draft content 2"));

        verify(postService, times(1)).getDraftsByUserId(userId);
    }

    @Test
    void getDraftByProjectIdShouldReturnPostDtos() throws Exception {
        Long projectId = 1L;
        ResponsePostDto responsePostDto1 = new ResponsePostDto();
        responsePostDto1.setId(1L);
        responsePostDto1.setContent("Draft content 1");

        ResponsePostDto responsePostDto2 = new ResponsePostDto();
        responsePostDto2.setId(2L);
        responsePostDto2.setContent("Draft content 2");

        List<ResponsePostDto> responsePostDtos = Arrays.asList(responsePostDto1, responsePostDto2);

        when(postService.getDraftsByProjectId(projectId)).thenReturn(responsePostDtos);

        mockMvc.perform(get("/posts/draft/project/{projectId}", projectId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].content").value("Draft content 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].content").value("Draft content 2"));

        verify(postService, times(1)).getDraftsByProjectId(projectId);
    }

    @Test
    void getPublishedByUserIdShouldReturnPostDtos() throws Exception {
        Long userId = 1L;
        ResponsePostDto responsePostDto1 = new ResponsePostDto();
        responsePostDto1.setId(1L);
        responsePostDto1.setContent("Published content 1");

        ResponsePostDto responsePostDto2 = new ResponsePostDto();
        responsePostDto2.setId(2L);
        responsePostDto2.setContent("Published content 2");

        List<ResponsePostDto> responsePostDtos = Arrays.asList(responsePostDto1, responsePostDto2);

        when(postService.getPublishedByUserId(userId)).thenReturn(responsePostDtos);

        mockMvc.perform(get("/posts/published/author/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].content").value("Published content 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].content").value("Published content 2"));

        verify(postService, times(1)).getPublishedByUserId(userId);
    }

    @Test
    void getPublishedByProjectIdShouldReturnPostDtos() throws Exception {
        Long projectId = 1L;
        Long authorId = 1L;
        ResponsePostDto responsePostDto1 = new ResponsePostDto();
        responsePostDto1.setId(1L);
        responsePostDto1.setContent("Published content 1");

        ResponsePostDto responsePostDto2 = new ResponsePostDto();
        responsePostDto2.setId(2L);
        responsePostDto2.setContent("Published content 2");

        List<ResponsePostDto> responsePostDtos = Arrays.asList(responsePostDto1, responsePostDto2);

        when(postService.getPublishedByProjectId(projectId, authorId)).thenReturn(responsePostDtos);

        mockMvc.perform(get("/posts/published/project/{projectId}?authorId=1", projectId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].content").value("Published content 1"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].content").value("Published content 2"));

        verify(postService, times(1)).getPublishedByProjectId(projectId, authorId);
    }

    @Test
    void shouldReturnPostsForHashtag() throws Exception {
        String tag = "example";
        ResponsePostDto firstResponsePostDto = new ResponsePostDto();
        firstResponsePostDto.setHashtags(Set.of(tag));
        ResponsePostDto secondResponsePostDto = new ResponsePostDto();
        secondResponsePostDto.setHashtags(Set.of(tag));

        List<ResponsePostDto> mockPosts = List.of(firstResponsePostDto, secondResponsePostDto);

        when(postService.findByHashtags(tag)).thenReturn(mockPosts);

        mockMvc.perform(get("/posts/hashtag/{tag}", tag)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(mockPosts.size()));
    }

    @Test
    void testUpdateResources_shouldReturnOk() throws Exception {
        Long postId = 1L;

        List<MockMultipartFile> tooManyFiles = IntStream.rangeClosed(1, 5)
                .mapToObj(i -> new MockMultipartFile(
                        "files",
                        "test" + i + ".png",
                        "image/png",
                        ("some-test-data-" + i).getBytes()
                ))
                .toList();

        MockMultipartHttpServletRequestBuilder requestBuilder =
                (MockMultipartHttpServletRequestBuilder) MockMvcRequestBuilders.multipart("/posts/{postId}/resources", postId)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE);

        tooManyFiles.forEach(requestBuilder::file);

        mockMvc.perform(requestBuilder.with(req -> {
                            req.setMethod("PUT");
                            return req;
                        }))
                .andExpect(status().isOk());
    }
}