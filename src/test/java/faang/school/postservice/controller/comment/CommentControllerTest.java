package faang.school.postservice.controller.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.ResponseCommentDto;
import faang.school.postservice.service.comment.CommentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
@ContextConfiguration(classes = {CommentController.class})
class CommentControllerTest {
    private static final String COMMENT_URL_ADD = "/comments/{postId}";
    private static final String COMMENT_URL_UPDATE = "/comments/update";
    private static final String COMMENT_URL_GET = "/comments/get/{postId}";
    private static final String COMMENT_URL_DELETE = "/comments/delete/{id}";

    @Autowired
    private ObjectMapper MAPPER;

    @MockBean
    private CommentService commentService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Позитивный тест метода positiveAddComment")
    void testPositiveAddComment() throws Exception {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .authorId(1L)
                .postId(2L)
                .content("content")
                .likeIds(Collections.emptyList())
                .createdAt(LocalDateTime.of(2011, 10, 11, 11, 11))
                .build();
        ResponseCommentDto responseCommentDto = ResponseCommentDto.builder()
                .id(1L)
                .authorId(1L)
                .postId(2L)
                .content("content")
                .likeIds(Collections.emptyList())
                .createdAt(LocalDateTime.of(2011, 10, 11, 11, 11))
                .build();

        when(commentService.addComment(1L, commentDto)).thenReturn(responseCommentDto);

        mockMvc.perform(post(COMMENT_URL_ADD, "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(commentDto)))
                .andExpect(content().json(MAPPER.writeValueAsString(responseCommentDto)))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @MethodSource("getTestStreamDto")
    @DisplayName("Негативный тест метода positiveAddComment")
    void testNegativeAddComment(CommentDto commentDto) throws Exception {
        mockMvc.perform(post(COMMENT_URL_ADD, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(MAPPER.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("Тест метода updateComment")
    void testUpdateComment() throws Exception {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .authorId(1L)
                .postId(2L)
                .content("content")
                .likeIds(Collections.emptyList())
                .createdAt(LocalDateTime.of(2011, 10, 11, 11, 11))
                .build();
        ResponseCommentDto responseCommentDto = ResponseCommentDto.builder()
                .id(1L)
                .authorId(1L)
                .postId(2L)
                .content("content")
                .likeIds(Collections.emptyList())
                .createdAt(LocalDateTime.of(2011, 10, 11, 11, 11))
                .updatedAt(LocalDateTime.of(2011, 12, 11, 11, 11))
                .build();
        when(commentService.updateComment(commentDto)).thenReturn(responseCommentDto);
        mockMvc.perform(post(COMMENT_URL_UPDATE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(MAPPER.writeValueAsString(commentDto)))
                .andExpect(content().json(MAPPER.writeValueAsString(responseCommentDto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Тест метода getAllCommentsByPostId")
    void testGetAllCommentsByPostId() throws Exception {
        List<ResponseCommentDto> responseCommentDtoList = List.of(
                ResponseCommentDto.builder().id(1L).build(),
                ResponseCommentDto.builder().id(2L).build(),
                ResponseCommentDto.builder().id(3L).build(),
                ResponseCommentDto.builder().id(4L).build()
        );
        when(commentService.getCommentsByPostId(1L)).thenReturn(responseCommentDtoList);
        mockMvc.perform(get(COMMENT_URL_GET, "1")
        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(MAPPER.writeValueAsString(responseCommentDtoList)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Тест метода deleteComment")
    void testDeleteComment() throws Exception {
        mockMvc.perform(delete(COMMENT_URL_DELETE, "1"))
                .andExpect(status().isOk());
    }

    private static Stream<Object[]> getTestStreamDto() {
        return Stream.of(
                new Object[]{CommentDto.builder()
                        .id(1L)
                        .authorId(1L)
                        //вызов без контента
                        .postId(2L)
                        .build()},
                new Object[]{CommentDto.builder()
                        .id(1L)
                        .authorId(1L)
                        .content("content")
                        //вызов без id поста
                        .build()}
        );
    }
}