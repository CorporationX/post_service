package faang.school.postservice.controller.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.util.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {
    @InjectMocks
    private PostController postController;
    @Mock
    private PostService postService;
    @Spy
    private TestData testData;

    private PostDto postDto;

    @BeforeEach
    void setUp() {
        postDto = testData.returnPostDto();
    }

    @Nested
    class PositiveTests {
        @DisplayName("should create test when authorId is present but projectId not")
        @Test
        void createPostByAuthorTest() {
            assertDoesNotThrow(() -> postController.createPost(postDto));

            verify(postService).createPost(postDto);
        }

        @DisplayName("should create test when projectId is present but authorId not")
        @Test
        void createPostByProjectTest() {
            postDto.setAuthorId(null);
            postDto.setProjectId(1L);

            assertDoesNotThrow(() -> postController.createPost(postDto));

            verify(postService).createPost(postDto);
        }

        @Test
        void publishPostTest() {
            assertDoesNotThrow(() -> postController.publishPost(anyLong()));

            verify(postService).publishPost(anyLong());
        }

        @DisplayName("should update post when authorId is present but projectId not and dto has id")
        @Test
        void updatePostByAuthorTest() {
            assertDoesNotThrow(() -> postController.updatePost(postDto));

            verify(postService).updatePost(postDto);
        }

        @DisplayName("should update post when projectId is present but authorId not and dto has id")
        @Test
        void updatePostByProjectTest() {
            postDto.setAuthorId(null);
            postDto.setProjectId(1L);

            assertDoesNotThrow(() -> postController.updatePost(postDto));

            verify(postService).updatePost(postDto);
        }

        @Test
        void deletePostTest() {
            assertDoesNotThrow(() -> postController.deletePost(anyLong()));

            verify(postService).deletePost(anyLong());
        }

        @Test
        void getPostByIdTest() {
            assertDoesNotThrow(() -> postController.getPostById(anyLong()));

            verify(postService).getPostById(anyLong());
        }

        @Test
        void getDraftsOfUserTest() {
            assertDoesNotThrow(() -> postController.getDraftsOfUser(anyLong()));

            verify(postService).getDraftsOfUser(anyLong());
        }

        @Test
        void getDraftsOfProjectTest() {
            assertDoesNotThrow(() -> postController.getDraftsOfProject(anyLong()));

            verify(postService).getDraftsOfProject(anyLong());
        }

        @Test
        void getPostsOfUserTest() {
            assertDoesNotThrow(() -> postController.getPostsOfUser(anyLong()));

            verify(postService).getPostsOfUser(anyLong());
        }

        @Test
        void getPostsOfProjectTest() {
            assertDoesNotThrow(() -> postController.getPostsOfProject(anyLong()));

            verify(postService).getPostsOfProject(anyLong());
        }
    }

    @Nested
    class NegativeTests {
        @DisplayName("should throw DataValidationException when projectId and authorId aren't present in dto during creation")
        @Test
        void createPostWithoutAuthorsTest() {
            postDto.setAuthorId(null);

            assertThrows(DataValidationException.class, () -> postController.createPost(postDto));

            verifyNoInteractions(postService);
        }

        @DisplayName("should throw DataValidationException when projectId and authorId are present in dto during creation")
        @Test
        void createPostWithBothAuthorsTest() {
            postDto.setProjectId(1L);

            assertThrows(DataValidationException.class, () -> postController.createPost(postDto));

            verifyNoInteractions(postService);
        }

        @DisplayName("should throw DataValidationException when projectId and authorId aren't present in dto during update")
        @Test
        void updatePostWithoutAuthorsTest() {
            postDto.setAuthorId(null);

            assertThrows(DataValidationException.class, () -> postController.updatePost(postDto));

            verifyNoInteractions(postService);
        }

        @DisplayName("should throw DataValidationException when projectId and authorId are present in dto during update")
        @Test
        void updatePostWithBothAuthorsTest() {
            postDto.setProjectId(1L);

            assertThrows(DataValidationException.class, () -> postController.updatePost(postDto));

            verifyNoInteractions(postService);
        }

        @DisplayName("should throw DataValidationException when dto doesn't have postId")
        @Test
        void updatePostWithoutPostIdTest() {
            postDto.setId(null);

            assertThrows(DataValidationException.class, () -> postController.updatePost(postDto));

            verifyNoInteractions(postService);
        }
    }
}