package faang.school.postservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.like.LikeCommentRequestDto;
import faang.school.postservice.dto.like.LikeCommentResponseDto;
import faang.school.postservice.dto.like.LikePostRequestDto;
import faang.school.postservice.dto.like.LikePostResponseDto;
import faang.school.postservice.service.LikeService;
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

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class LikeControllerTest {
    private static final Long ID = 10L;
    private static final Long USER_ID = 20L;
    private static final Long POST_ID = 30L;
    private static final Long COMMENT_ID = 40L;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private MockMvc mockMvc;

    @Mock
    private LikeService likeService;
    @InjectMocks
    private LikeController likeController;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(likeController).build();
    }

    @Test
    public void testLikePostSuccess() throws Exception {
        LikePostRequestDto requestDto = getPostRequestDto();
        LikePostResponseDto expectedDto = getPostResponseDto();

        Mockito.when(likeService.addPost(requestDto)).thenReturn(expectedDto);

        mockMvc.perform(post("/likes/post")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(3)))
                .andExpect(jsonPath("$.id").value(ID))
                .andExpect(jsonPath("$.userId").value(USER_ID))
                .andExpect(jsonPath("$.postId").value(POST_ID));
    }

    @Test
    public void testRemoveLikePostSuccess() throws Exception {
        LikePostRequestDto requestDto = getPostRequestDto();
        LikePostResponseDto expectedDto = getPostResponseDto();

        Mockito.when(likeService.deletePost(requestDto)).thenReturn(expectedDto);

        mockMvc.perform(delete("/likes/post")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(3)))
                .andExpect(jsonPath("$.id").value(ID))
                .andExpect(jsonPath("$.userId").value(USER_ID))
                .andExpect(jsonPath("$.postId").value(POST_ID));
    }

    @Test
    public void testAddUserCommentSuccess() throws Exception {
        LikeCommentRequestDto requestDto = getCommentRequestDto();
        LikeCommentResponseDto expectedDto = getCommentResponseDto();

        Mockito.when(likeService.addComment(requestDto)).thenReturn(expectedDto);

        mockMvc.perform(post("/likes/comment")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(3)))
                .andExpect(jsonPath("$.id").value(ID))
                .andExpect(jsonPath("$.userId").value(USER_ID))
                .andExpect(jsonPath("$.commentId").value(COMMENT_ID));
    }

    @Test
    public void testRemoveUserCommentSuccess() throws Exception {
        LikeCommentRequestDto requestDto = getCommentRequestDto();
        LikeCommentResponseDto expectedDto = getCommentResponseDto();

        Mockito.when(likeService.deleteComment(requestDto)).thenReturn(expectedDto);

        mockMvc.perform(delete("/likes/comment")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(3)))
                .andExpect(jsonPath("$.id").value(ID))
                .andExpect(jsonPath("$.userId").value(USER_ID))
                .andExpect(jsonPath("$.commentId").value(COMMENT_ID));
    }

    private LikePostResponseDto getPostResponseDto() {
        return LikePostResponseDto.builder()
                .id(ID)
                .userId(USER_ID)
                .postId(POST_ID)
                .build();
    }

    private LikePostRequestDto getPostRequestDto() {
        return LikePostRequestDto.builder()
                .userId(USER_ID)
                .postId(POST_ID)
                .build();
    }

    private LikeCommentResponseDto getCommentResponseDto() {
        return LikeCommentResponseDto.builder()
                .id(ID)
                .userId(USER_ID)
                .commentId(COMMENT_ID)
                .build();
    }

    private LikeCommentRequestDto getCommentRequestDto() {
        return LikeCommentRequestDto.builder()
                .userId(USER_ID)
                .commentId(COMMENT_ID)
                .build();
    }
}