package faang.school.postservice.controller;

import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.service.like.LikeService;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class LikeControllerTest {
    private static final long VALID_ID = 3L;

    private MockMvc mockMvc;
    private LikeDto dto;
    private ObjectWriter objectWriter;
    @Mock
    private LikeService service;
    @InjectMocks
    private LikeController controller;

    @BeforeEach
    public void setUp() {
        //Arrange
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        dto = new LikeDto();
        dto.setId(VALID_ID);
        dto.setUserId(VALID_ID);
        ObjectMapper objectMapper = new ObjectMapper();
        objectWriter = objectMapper.writer().withDefaultPrettyPrinter();
    }

    @Test
    public void testServiceAddPostLike() throws Exception {
        //Act
        Mockito.when(service.addPostLike(VALID_ID, dto)).thenReturn(dto);
        //Assert
        mockMvc.perform(put("/api/post/3/like")
                        .contentType(MediaType.APPLICATION_JSON).content(objectWriter.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(VALID_ID))
                .andExpect(jsonPath("$.userId").value(VALID_ID));
    }

//    @Test
//    public void testServiceDeletePostLike() throws Exception {
//        //Act
//        Mockito.when(service.deletePostLike(VALID_ID, dto)).thenReturn(dto);
//        //Assert
//        mockMvc.perform(delete("/api/post/3/unlike")
//                        .contentType(MediaType.APPLICATION_JSON).content(objectWriter.writeValueAsString(dto)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(VALID_ID))
//                .andExpect(jsonPath("$.userId").value(VALID_ID));
//    }

    @Test
    public void testServiceAddCommentLike() throws Exception {
        //Act
        Mockito.when(service.addCommentLike(VALID_ID, VALID_ID, dto)).thenReturn(dto);
        //Assert
        mockMvc.perform(put("/api/post/3/comment/3/like")
                        .contentType(MediaType.APPLICATION_JSON).content(objectWriter.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(VALID_ID))
                .andExpect(jsonPath("$.userId").value(VALID_ID));
    }

//    @Test
//    public void testServiceDeleteCommentLike() throws Exception {
//        //Act
//        Mockito.when(service.deleteCommentLike(VALID_ID, VALID_ID, dto)).thenReturn(dto);
//        //Assert
//        mockMvc.perform(delete("/api/post/3/comment/3/unlike")
//                        .contentType(MediaType.APPLICATION_JSON).content(objectWriter.writeValueAsString(dto)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id").value(VALID_ID))
//                .andExpect(jsonPath("$.userId").value(VALID_ID));
//    }
}