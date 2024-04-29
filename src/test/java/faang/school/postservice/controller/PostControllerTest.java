//package faang.school.postservice.controller;
//
//import faang.school.postservice.dto.post.PostDto;
//import faang.school.postservice.service.PostServiceImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//@ExtendWith(MockitoExtension.class)
//class PostControllerTest {
//    @InjectMocks
//    private PostController postController;
//    @Mock
//    private PostServiceImpl postServiceImpl;
//    private PostDto postDto;
//    private long ID = 1L;
//    private long NO_VALID_ID = -5;
//
//    @BeforeEach
//    void setUp() {
//        postDto = PostDto.builder()
//                .authorId(1L)
//                .build();
//    }
//
//    @Test
//    void testCreateDraftSuccessful() {
//        postDto.setContent("Hello, world!");
//        postController.createDraft(postDto);
//        Mockito.verify(postServiceImpl).createDraft(postDto);
//    }
//
//    @Test
//    void testCreateDraftFailed() {
//        DataValidationException exception = assertThrows(DataValidationException.class,
//                () -> postController.createDraft(postDto));
//        assertEquals("A post with this content cannot be created", exception.getMessage());
//    }
//
//    @Test
//    void testPublishPostSuccessful() {
//        postController.publishPost(ID);
//        Mockito.verify(postServiceImpl).publish(ID);
//    }
//
////    @Test
////    void testPublishPostFailed() {
////        DataValidationException exception = assertThrows(DataValidationException.class,
////                () -> postController.publishPost(NO_VALID_ID));
////        assertEquals("Invalid ID", exception.getMessage());
////    }
//
//    @Test
//    void testUpdatePostSuccessful() {
//        postDto.setId(5L);
//        postDto.setContent("Hello");
//        postController.updatePost(postDto);
//        Mockito.verify(postServiceImpl).update(postDto);
//    }
//
//    @Test
//    void testRemovePostSoftlySuccessful() {
//        postController.deletePost(ID);
//        Mockito.verify(postServiceImpl).deletePost(ID);
//    }
//
//    @Test
//    void testGetPostByIdSuccessful() {
//        postController.getPostById(ID);
//        Mockito.verify(postServiceImpl).getPostById(ID);
//    }
//
//    @Test
//    void testGetPostDraftsByAuthorIdSuccessful() {
//        postController.getDraftsByAuthorId(ID);
//        Mockito.verify(postServiceImpl).getDraftsByAuthorId(ID);
//    }
//
//    @Test
//    void testGetPostDraftsByProjectIdSuccessful() {
//        postController.getDraftsByProjectId(ID);
//        Mockito.verify(postServiceImpl).getDraftsByProjectId(ID);
//    }
//
//    @Test
//    void testGetPublishedPostsByAuthorId() {
//        postController.getPostsByAuthorId(ID);
//        Mockito.verify(postServiceImpl).getPostsByAuthorId(ID);
//    }
//
//    @Test
//    void testGetPublishedPostsByProjectId() {
//        postController.getPostsByProjectId(ID);
//        Mockito.verify(postServiceImpl).getPostsByProjectId(ID);
//    }
//}