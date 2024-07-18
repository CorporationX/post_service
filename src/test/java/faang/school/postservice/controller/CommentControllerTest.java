//package faang.school.postservice.controller;
//
//import faang.school.postservice.config.context.UserContext;
//import faang.school.postservice.dto.comment.CommentDto;
//import faang.school.postservice.service.CommentService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MockMvcBuilder;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import java.util.Collections;
//
//import static org.hamcrest.Matchers.hasSize;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@ExtendWith(MockitoExtension.class)
//class CommentControllerTest {
//
//    private MockMvc mockMvc;
//
//    @Mock
//    UserContext userContext;
//    @Mock
//    private CommentService commentService;
//    @InjectMocks
//    private CommentController commentController;
//    CommentDto commentDto;
//
//    @BeforeEach
//    public void setUp(){
//         commentDto = CommentDto.builder()
//                 .id(7L)
//                 .likesIds(Collections.emptyList())
//                 .authorId(1L)
//                 .content("content string")
//                 .postId(1L)
//                 .build();
//        mockMvc = MockMvcBuilders.standaloneSetup(commentController).build();
//    }
//
//    @Test
//    public void testCreation() throws Exception{
//        Mockito.when(commentService.create(commentDto, commentDto.getAuthorId())).thenReturn(commentDto);
//        mockMvc.perform(post("/posts"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].name", is ("John")))
//    }
//
//}