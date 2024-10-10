package faang.school.postservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import faang.school.postservice.dto.filter.PostFilterDto;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.service.PostService;
import faang.school.postservice.util.container.PostContainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
    private PostController controller;
    @Mock
    private PostService service;
    private final PostContainer container = new PostContainer();
    private ObjectMapper objectMapper;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testCreate() throws Exception {
        // given
        String uri = "/post/";
        PostDto requestDto = PostDto.builder()
                .content(container.content())
                .build();

        PostDto responseDto = PostDto.builder()
                .id(container.postId())
                .content(container.content())
                .build();

        when(service.create(requestDto)).thenReturn(responseDto);

        // then
        mockMvc.perform(post(uri)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.content").value(responseDto.getContent()));
    }

    @Test
    void testPublish() throws Exception {
        // given
        Long postId = container.postId();
        String uri = "/post/{postId}/publish/";

        PostDto responseDto = PostDto.builder()
                .id(postId)
                .published(container.published())
                .build();

        when(service.publish(postId)).thenReturn(responseDto);

        // then
        mockMvc.perform(put(uri, postId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.published").value(responseDto.isPublished()));
    }

    @Test
    void testUpdate() throws Exception {
        // given
        String uri = "/post/";

        PostDto requestDto = PostDto.builder()
                .id(container.postId())
                .content(container.content() + " update")
                .build();

        PostDto responseDto = PostDto.builder()
                .id(container.postId())
                .content(container.content() + " update")
                .build();

        when(service.update(requestDto)).thenReturn(responseDto);

        // then
        mockMvc.perform(put(uri)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.content").value(responseDto.getContent()));
    }

    @Test
    void testDelete() throws Exception {
        // given
        Long postId = container.postId();
        String uri = "/post/" + postId;

        // then
        mockMvc.perform(delete(uri))
                .andExpect(status().isOk());
        verify(service, times(1)).delete(postId);
    }

    @Test
    void testGetPost() throws Exception {
        // given
        Long postId = container.postId();
        String uri = "/post/" + postId;

        PostDto responseDto = PostDto.builder()
                .id(postId)
                .content(container.content())
                .build();

        when(service.getPost(postId)).thenReturn(responseDto);

        // then
        mockMvc.perform(get(uri))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.content").value(responseDto.getContent()));
    }

    @Test
    void testGetFilteredPosts() throws Exception {
        // given
        List<PostDto> responseList = prepareDtoList();
        PostFilterDto requestFilters = container.filters();
        String uri = "/post/filtered";

        when(service.getFilteredPosts(any(PostFilterDto.class))).thenReturn(responseList);

        // then
        mockMvc.perform(get(uri)
                        .content(objectMapper.writeValueAsString(requestFilters))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(responseList.get(0).getId()))
                .andExpect(jsonPath("$[1].id").value(responseList.get(1).getId()));
    }

    private List<PostDto> prepareDtoList() {
        PostDto firstDto = PostDto.builder()
                .id(container.postId())
                .build();

        PostDto secondDto = PostDto.builder()
                .id(container.postId() + 1)
                .build();

        return List.of(firstDto, secondDto);
    }
}