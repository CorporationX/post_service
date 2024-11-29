package faang.school.postservice.controller.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.TestContainersConfig;
import faang.school.postservice.dto.post.CreatePostRequestDto;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.ErrorResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashMap;
import java.util.Map;

//@SpringBootTest
//@AutoConfigureMockMvc
//@ActiveProfiles("test")
//public class PostControllerTest extends TestContainersConfig {
//
//    @MockBean
//    UserServiceClient userServiceClient;
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Test
//    public void testCreatePost() throws Exception {
//        String userHeader = "x-user-id";
//        Long userId = 1L;
//        UserDto userDto = new UserDto(userId, "name", "email");
//        CreatePostRequestDto request = new CreatePostRequestDto();
//        request.setContent("content");
//        request.setAuthorId(userId);
//        request.setProjectId(1L);
//        String requestBodyAsJson = objectMapper.writeValueAsString(request);
//
//        Mockito.when(userServiceClient.getUser(userId)).thenReturn(userDto);
//
//        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
//                        .post("/posts")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header(userHeader, userId)
//                        .content(requestBodyAsJson))
//                .andExpect(MockMvcResultMatchers.status().isCreated())
//                .andReturn();
//
//        String responseJson = result.getResponse().getContentAsString();
//        PostResponseDto response = objectMapper.readValue(responseJson, PostResponseDto.class);
//
//        Assertions.assertEquals(request.getContent(), response.getContent());
//        Assertions.assertEquals(request.getAuthorId(), response.getAuthorId());
//        Assertions.assertEquals(request.getProjectId(), response.getProjectId());
//    }
//
//    @Test
//    public void testCreatePostShouldThrowsExceptionOnNullRequestDtoField() throws Exception {
//        CreatePostRequestDto request = new CreatePostRequestDto();
//        request.setContent("content");
//
//        Map<String, String> errorFields = new HashMap<>();
//        String projectFieldKey = "projectId";
//        String authorFieldKey = "authorId";
//        String errorFieldValue = "must not be null";
//        errorFields.put(projectFieldKey, errorFieldValue);
//        errorFields.put(authorFieldKey, errorFieldValue);
//
//        String requestBodyAsJson = objectMapper.writeValueAsString(request);
//        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
//                        .post("/posts")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(requestBodyAsJson))
//                .andExpect(MockMvcResultMatchers.status().isBadRequest())
//                .andReturn();
//
//        String responseJson = result.getResponse().getContentAsString();
//        ErrorResponse response = objectMapper.readValue(responseJson, ErrorResponse.class);
//        Assertions.assertNull(response.getErrorFields());
//        Assertions.assertEquals("AuthorId or projectId must be filled in", response.getMessage());
//    }
//}
