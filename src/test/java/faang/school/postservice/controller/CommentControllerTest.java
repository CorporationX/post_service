package faang.school.postservice.controller;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.service.CommentService;
import faang.school.postservice.validator.ControllerValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectWriter;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {
    private static final long VALID_ID = 3L;
    private static final String RANDOM_CONTENT = "Random content";
    private MockMvc mockMvc;
    private CommentDto dto;
    private ObjectWriter objectWriter;
    @Mock
    private CommentService service;
    @Mock
    private ControllerValidator validator;
    @InjectMocks
    private CommentController controller;

    @BeforeEach
    public void setUp() {
        //Arrange
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        dto = new CommentDto();
        dto.setId(VALID_ID);
        dto.setContent(RANDOM_CONTENT);
        dto.setAuthorId(VALID_ID);
        dto.setLikesId(List.of(VALID_ID));
        dto.setPostId(VALID_ID);
        ObjectMapper objectMapper = new ObjectMapper();
        objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
    }

    @Test
    public void testVerifyServiceAddComment() throws Exception {
        //Act
        Mockito.when(service.addComment(VALID_ID, dto)).thenReturn(dto);
        //Assert
        mockMvc.perform(post("/post/3/comment")
                        .contentType(MediaType.APPLICATION_JSON).content(objectWriter.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(VALID_ID))
                .andExpect(jsonPath("$.content").value(RANDOM_CONTENT))
                .andExpect(jsonPath("$.authorId").value(VALID_ID))
                .andExpect(jsonPath("$.likesId", hasSize(dto.getLikesId().size())))
                .andExpect(jsonPath("$.postId").value(VALID_ID));
    }

    @Test
    public void testVerifyServiceChangeComment() throws Exception {
        //Act
        Mockito.when(service.changeComment(VALID_ID, dto)).thenReturn(dto);
        //Assert
        mockMvc.perform(put("/post/3/comment")
                        .contentType(MediaType.APPLICATION_JSON).content(objectWriter.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(VALID_ID))
                .andExpect(jsonPath("$.content").value(RANDOM_CONTENT))
                .andExpect(jsonPath("$.authorId").value(VALID_ID))
                .andExpect(jsonPath("$.likesId", hasSize(dto.getLikesId().size())))
                .andExpect(jsonPath("$.postId").value(VALID_ID));
    }

    @Test
    public void testVerifyServiceGetAllCommentsOfPost() throws Exception {
        //Arrange
        List<CommentDto> comments = new ArrayList<>();
        comments.add(dto);
        //Act
        Mockito.when(service.getAllCommentsOfPost(VALID_ID)).thenReturn(comments);
        //Assert
        mockMvc.perform(get("/post/3/comments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(comments.size())));
    }

    @Test
    public void testVerifyServiceDeleteComment() throws Exception {
        //Act
        Mockito.when(service.deleteComment(VALID_ID, VALID_ID)).thenReturn(dto);
        //Assert
        mockMvc.perform(delete("/post/3/comment?commentId=3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(VALID_ID))
                .andExpect(jsonPath("$.content").value(RANDOM_CONTENT))
                .andExpect(jsonPath("$.authorId").value(VALID_ID))
                .andExpect(jsonPath("$.likesId", hasSize(dto.getLikesId().size())))
                .andExpect(jsonPath("$.postId").value(VALID_ID));
    }


}