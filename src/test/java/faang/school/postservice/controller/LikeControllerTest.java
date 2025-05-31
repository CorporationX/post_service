package faang.school.postservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.like.LikeCommentRequestDto;
import faang.school.postservice.dto.like.LikeDto;
import faang.school.postservice.dto.like.LikePostRequestDto;
import faang.school.postservice.mapper.LikeMapperImpl;
import faang.school.postservice.service.LikeService;
import faang.school.postservice.util.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class LikeControllerTest {
    private static final Long ID = 10L;
    private static final Long USER_ID = 20L;
    private static final Long POST_ID = 30L;
    private static final Long COMMENT_ID = 40L;

    private final Utils utils = new Utils();
    private MockMvc mockMvc;
    @Spy
    private ObjectMapper objectMapper;
    @Mock
    private LikeService likeService;
    @Spy
    private UserContext userContext;
    @Spy
    private LikeMapperImpl mapper;
    @InjectMocks
    private LikeController likeController;

    @BeforeEach
    public void setUp() {
        userContext.setUserId(USER_ID);
        mockMvc = MockMvcBuilders.standaloneSetup(likeController).build();
    }

    @AfterEach
    public void cleanUp() {
        userContext.clear();
    }

    @Test
    public void testAddLikePostSuccess() throws Exception {
        LikePostRequestDto requestDto = getPostRequestDto();
        LikeDto likeDto = getRequestLikePostDto();
        LikeDto resultLikeDto = getResponseLikePostDto();

        when(likeService.addPost(likeDto)).thenReturn(resultLikeDto);

        mockMvc.perform(post("/likes/post")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().is(201))
                .andExpect(jsonPath("$.*", hasSize(3)))
                .andExpect(jsonPath("$.id").value(ID))
                .andExpect(jsonPath("$.userId").value(USER_ID))
                .andExpect(jsonPath("$.postId").value(POST_ID));
    }

    @Test
    public void testDeleteLikePostSuccess() throws Exception {
        LikeDto likeDto = getRequestLikePostDto();

        doNothing().when(likeService).deletePost(likeDto);

        mockMvc.perform(delete(utils.format("/likes/post/{}", POST_ID)))
                .andDo(print())
                .andExpect(status().is(204))
                .andExpect(content().string(""));
    }

    @Test
    public void testAddUserCommentSuccess() throws Exception {
        LikeCommentRequestDto requestDto = getCommentRequestDto();
        LikeDto likeDto = getRequestLikeCommentDto();
        LikeDto resultLikeDto = getResponseLikeCommentDto();

        when(likeService.addComment(likeDto)).thenReturn(resultLikeDto);

        mockMvc.perform(post("/likes/comment")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().is(201))
                .andExpect(jsonPath("$.*", hasSize(3)))
                .andExpect(jsonPath("$.id").value(ID))
                .andExpect(jsonPath("$.userId").value(USER_ID))
                .andExpect(jsonPath("$.commentId").value(COMMENT_ID));
    }

    @Test
    public void testDeleteUserCommentSuccess() throws Exception {
        LikeDto likeDto = getRequestLikeCommentDto();

        doNothing().when(likeService).deleteComment(likeDto);

        mockMvc.perform(delete(utils.format("/likes/comment/{}", COMMENT_ID)))
                .andDo(print())
                .andExpect(status().is(204))
                .andExpect(content().string(""));
    }

    private LikePostRequestDto getPostRequestDto() {
        return LikePostRequestDto.builder()
                .postId(POST_ID)
                .build();
    }

    private LikeCommentRequestDto getCommentRequestDto() {
        return LikeCommentRequestDto.builder()
                .commentId(COMMENT_ID)
                .build();
    }

    private LikeDto getRequestLikePostDto() {
        return LikeDto.builder()
                .userId(USER_ID)
                .postId(POST_ID)
                .build();
    }

    private LikeDto getResponseLikePostDto() {
        return LikeDto.builder()
                .id(ID)
                .userId(USER_ID)
                .postId(POST_ID)
                .build();
    }

    private LikeDto getRequestLikeCommentDto() {
        return LikeDto.builder()
                .userId(USER_ID)
                .commentId(COMMENT_ID)
                .build();
    }

    private LikeDto getResponseLikeCommentDto() {
        return LikeDto.builder()
                .id(ID)
                .userId(USER_ID)
                .commentId(COMMENT_ID)
                .build();
    }
}