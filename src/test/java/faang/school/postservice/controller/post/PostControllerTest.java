//package faang.school.postservice.controller.post;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import faang.school.postservice.config.context.UserContext;
//import faang.school.postservice.dto.post.PostDto;
//import faang.school.postservice.service.post.PostService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.NullSource;
//import org.junit.jupiter.params.provider.ValueSource;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//
//import java.util.List;
//import java.util.function.Function;
//
//import static org.hamcrest.Matchers.hasSize;
//import static org.hamcrest.Matchers.is;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(PostController.class)
//class PostControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private UserContext userContext;
//
//    @MockBean
//    private PostService postService;
//
//    private final String invalidIdMsg = "cannot be less than 1";
//    private final String invalidContentMsg = "{\"content\":\"Content can not be blank or null\"}";
//    private final long postId = 1;
//    private final long authorId = 2;
//    private final long projectId = 3;
//    private final String content = "content";
//    private final PostDto postDto = PostDto.builder().build();
//    private final PostDto resultDto = PostDto.builder().id(postId).content(content).build();
//    private final List<PostDto> resultDtos = List.of(
//            PostDto.builder().id(1L).build(),
//            PostDto.builder().id(2L).build(),
//            PostDto.builder().id(3L).build());
//
//    @BeforeEach
//    public void setUp() {
//        postDto.setContent(content);
//        resultDto.setContent(content);
//    }
//
//    @Test
//    public void testCreatingDraftPost() throws Exception {
//        when(postService.createDraftPost(postDto, )).thenReturn(resultDto);
//        expectedOkAndReturnDto(MockMvcRequestBuilders::post, "/post");
//    }
//
//    @ParameterizedTest
//    @NullSource
//    @ValueSource(strings={"  "})
//    public void testNotCreatingDraftPostWhenBlankContent(String content) throws Exception {
//        postDto.setContent(content);
//        expectedBadRequest(MockMvcRequestBuilders::post, "/post", invalidContentMsg);
//    }
//
//    @Test
//    public void testPublishPost() throws Exception {
//        when(postService.publishPost(postId)).thenReturn(resultDto);
//        expectedOkAndReturnDto(MockMvcRequestBuilders::put, "/post/public/" + postId);
//    }
//
//    @Test
//    public void testNotPublishPostWhenInvalidId() throws Exception {
//        expectedBadRequest(MockMvcRequestBuilders::put, "/post/public/" + -postId,
//                "publishPost.postId: " + invalidIdMsg);
//    }
//
//    @Test
//    public void testUpdatePost() throws Exception {
//        when(postService.updatePost(postId, postDto)).thenReturn(resultDto);
//        expectedOkAndReturnDto(MockMvcRequestBuilders::put, "/post/" + postId);
//    }
//
//    @Test
//    public void testNotUpdatePostWhenInvalidId() throws Exception {
//        expectedBadRequest(MockMvcRequestBuilders::put, "/post/" + -postId,
//                "updatePost.postId: " + invalidIdMsg);
//    }
//
//    @Test
//    public void testDeletePost() throws Exception {
//        mockMvc.perform(delete("/post/" + postId)).andExpect(status().isOk());
//    }
//
//    @Test
//    public void testNotDeletePostWhenInvalidId() throws Exception {
//        expectedBadRequest(MockMvcRequestBuilders::delete, "/post/" + -postId,
//                "deletePost.postId: " + invalidIdMsg);
//    }
//
//    @Test
//    public void testGetPost() throws Exception {
//        when(postService.getPost(postId)).thenReturn(resultDto);
//        expectedOkAndReturnDto(MockMvcRequestBuilders::get, "/post/" + postId);
//    }
//
//    @Test
//    public void testNotGetPost() throws Exception {
//        expectedBadRequest(MockMvcRequestBuilders::get, "/post/" + -postId,
//                "getPost.postId: " + invalidIdMsg);
//    }
//
//    @Test
//    public void testGetAllAuthorDraftPosts() throws Exception {
//        when(postService.getAllAuthorDraftPosts(authorId)).thenReturn(resultDtos);
//        expectedOkAndReturnDtos("/post/authors/" + authorId + "/drafts");
//    }
//
//    @Test
//    public void testNotGetAllAuthorDraftPosts() throws Exception {
//        expectedBadRequest(MockMvcRequestBuilders::get, "/post/authors/0/drafts",
//                "getAllAuthorDraftPosts.authorId: " + invalidIdMsg);
//    }
//
//    @Test
//    public void testGetAllAuthorPosts() throws Exception {
//        when(postService.getAllAuthorPosts(authorId)).thenReturn(resultDtos);
//        expectedOkAndReturnDtos("/post/authors/" + authorId + "/public");
//    }
//
//    @Test
//    public void testNotGetAllAuthorPosts() throws Exception {
//        expectedBadRequest(MockMvcRequestBuilders::get, "/post/authors/0/public",
//                "getAllAuthorPosts.authorId: " + invalidIdMsg);
//    }
//
//    @Test
//    public void testGetAllProjectDraftPosts() throws Exception {
//        when(postService.getAllProjectDraftPosts(projectId)).thenReturn(resultDtos);
//        expectedOkAndReturnDtos("/post/projects/" + projectId + "/drafts");
//    }
//
//    @Test
//    public void testNotGetAllProjectDraftPosts() throws Exception {
//        expectedBadRequest(MockMvcRequestBuilders::get, "/post/projects/0/drafts",
//                "getAllProjectDraftPosts.projectId: " + invalidIdMsg);
//    }
//
//    @Test
//    public void testGetAllProjectPosts() throws Exception {
//        when(postService.getAllProjectPosts(projectId)).thenReturn(resultDtos);
//        expectedOkAndReturnDtos("/post/projects/" + projectId + "/public");
//    }
//
//    @Test
//    public void testNotGetAllProjectPosts() throws Exception {
//        expectedBadRequest(MockMvcRequestBuilders::get, "/post/projects/0/public",
//                "getAllProjectPosts.projectId: " + invalidIdMsg);
//    }
//
//    private void expectedOkAndReturnDtos(String path) throws Exception {
//        mockMvc.perform(get(path))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$", hasSize(3)))
//                .andExpect(jsonPath("$[0].id", is(1)))
//                .andExpect(jsonPath("$[1].id", is(2)))
//                .andExpect(jsonPath("$[2].id", is(3)));
//    }
//
//    private void expectedOkAndReturnDto(
//            Function<String, MockHttpServletRequestBuilder> method, String path) throws Exception {
//
//        mockMvc.perform(method.apply(path)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(new ObjectMapper().writeValueAsString(postDto)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.id", is(1)))
//                .andExpect(jsonPath("$.content", is("content")));
//    }
//
//    private void expectedBadRequest(
//            Function<String, MockHttpServletRequestBuilder> method, String path, String msg) throws Exception {
//
//        mockMvc.perform(method.apply(path)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(new ObjectMapper().writeValueAsString(postDto)))
//                .andExpect(status().isBadRequest())
//                .andExpect(content().string(msg))
//                .andReturn();
//    }
//}