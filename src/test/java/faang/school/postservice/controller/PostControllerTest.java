package faang.school.postservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.post.PostService;
import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;




import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


@ExtendWith(MockitoExtension.class)
public class PostControllerTest {
    @InjectMocks
    private PostController controller;
    @Mock
    private PostService service;
    private PostDto postDto;
    private PostDto expectedPostDto;
    private List<PostDto> returnPostDtos;
    private List<PostDto> expectedPostDtos;
    private PostDto expectedCapturedPostDto;
    private PostDto returnPostDto;
    private MockMvc mockMvc;
    private final LocalDateTime now = LocalDateTime.now();
    private final long id = 1L;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    @Captor
    private ArgumentCaptor<PostDto> postDtoCaptor;
    @Captor
    private ArgumentCaptor<Long> longCaptor;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        postDto = createPostDto(id,1L, 0, now);
        expectedCapturedPostDto = createPostDto(id,1L, 0, now);
        expectedPostDto = createPostDto(id,1L, 0, now);
        returnPostDto = createPostDto(id,1L, 0, now);
        returnPostDtos = new ArrayList<>(
                List.of(
                        createPostDto(1L,1L, 0, now),
                        createPostDto(2L,1L, 0, now),
                        createPostDto(3L,1L, 0, now)
                )
        );
        expectedPostDtos = new ArrayList<>(
                List.of(
                        createPostDto(1L,1L, 0, now),
                        createPostDto(2L,1L, 0, now),
                        createPostDto(3L,1L, 0, now)
                )
        );
    }

    @Test
    public void testCreate_TwoAuthors() {
        // Arrange
        postDto = createPostDto(id,1L, 1L, now);

        // Act and Assert
        Exception exception = Assertions.assertThrows(DataValidationException.class,() -> controller.create(postDto));
        Assertions.assertEquals("Автор у поста может быть только один", exception.getMessage());
    }

    @Test
    public void testCreate_NoAuthors() {
        // Arrange
        postDto = createPostDto(id,0, 0, now);

        // Act and Assert
        Exception exception = Assertions.assertThrows(DataValidationException.class,() -> controller.create(postDto));
        Assertions.assertEquals("У поста должен быть автор", exception.getMessage());
    }

    @Test
    public void testCreate() throws Exception {
        // Arrange
        when(service.create(postDto)).thenReturn(returnPostDto);

        // Act and Assert
        mockMvc.perform(
                    post("/api/post/draft")
                            .content(objectMapper.writeValueAsString(postDto))
                            .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedPostDto)));

    }

    @Test
    public void testPublish() throws Exception {
        // Arrange
        when(service.publish(id)).thenReturn(returnPostDto);

        // Act and Assert
        mockMvc.perform(
                    put("/api/post/publishing/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(postDto))
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedPostDto)));

    }

    @Test
    public void testUpdate_WrongId() {
        // Arrange
        postDto = createPostDto(0, 1L, 0, now);

        // Act and Assert
        Exception exception = Assertions.assertThrows(DataValidationException.class, () -> controller.update(postDto));
        Assertions.assertEquals("Передано некорректное id для обновления:" + postDto.id(), exception.getMessage());
    }

    @Test
    public void testUpdate() throws Exception {
        // Arrange
        when(service.update(postDto)).thenReturn(returnPostDto);

        mockMvc.perform(
                    put("/api/post")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(postDto))
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedPostDto)));
        verify(service, times(1)).update(postDtoCaptor.capture());
        Assertions.assertEquals(expectedCapturedPostDto, postDtoCaptor.getValue());
    }

    @Test
    public void testSoftlyDelete() throws Exception {
        // Act and Assert
        mockMvc.perform(delete("/api/post/{id}", id))
                .andExpect(status().isOk());
        verify(service, times(1)).softlyDelete(longCaptor.capture());
        Assertions.assertEquals(1L, longCaptor.getValue());
    }

    @Test
    public void testGetPost() throws Exception{
        // Arrange
        when(service.getPost(id)).thenReturn(returnPostDto);

        // Act & Assert
        mockMvc.perform(get("/api/post/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedPostDto)));
    }

    @Test
    public void testGetDraftPostsForUser() throws Exception {
        // Arrange
        var authorId = 1L;
        when(service.getDraftPostsForUser(authorId)).thenReturn(returnPostDtos);

        // Act & Assert
        mockMvc.perform(get("/api/posts/draft/author/{authorId}", authorId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedPostDtos)));
    }

    @Test
    public void testGetDraftPostsForProject() throws Exception {
        // Arrange
        var projectId = 1L;
        when(service.getDraftPostsForProject(projectId)).thenReturn(returnPostDtos);

        // Act & Assert
        mockMvc.perform(get("/api/posts/draft/project/{projectId}", projectId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedPostDtos)));
    }

    @Test
    public void testGetPublishedPostsForUser() throws Exception {
        // Arrange
        var authorId = 1L;
        when(service.getPublishedPostsForUser(authorId)).thenReturn(returnPostDtos);

        // Act & Assert
        mockMvc.perform(get("/api/posts/author/{authorId}", authorId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedPostDtos)));
    }

    @Test
    public void testGetPublishedPostsForProject() throws Exception {
        // Arrange
        var projectId = 1L;
        when(service.getPublishedPostsForProject(projectId)).thenReturn(returnPostDtos);

        // Act & Assert
        mockMvc.perform(get("/api/posts/project/{projectId}", projectId))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedPostDtos)));
    }

    private PostDto createPostDto(long id, long authorId, long projectId, LocalDateTime publishedAt) {
        return new PostDto(
                id,
                "content",
                authorId,
                projectId,
                new ArrayList<>(List.of(1L, 2L)),
                new ArrayList<>(List.of(1L, 3L)),
                publishedAt
        );
    }
}
