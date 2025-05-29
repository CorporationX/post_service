package faang.school.postservice.service.utils;

import faang.school.postservice.dto.post.CreatePostDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.project.ProjectService;
import faang.school.postservice.service.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceUtilsTest {

    @Mock
    private UserService userServiceMock;
    @Mock
    private ProjectService projectServiceMock;
    @Mock
    private PostRepository postRepositoryMock;

    @InjectMocks
    private PostServiceUtils postServiceUtils; // The class under test

    private CreatePostDto createPostDto;

    @Nested
    class IsAuthorOrProjectAddedTests {

        @Test
        void whenAuthorIdValidAndProjectIdNull_thenChecksUser() {
            createPostDto = CreatePostDto.builder()
                    .authorId(1L)
                    .projectId(null)
                    .content("test content")
                    .build();
            doNothing().when(userServiceMock).checkUserExist(1L);

            assertDoesNotThrow(() -> postServiceUtils.isAuthorOrProjectAdded(createPostDto));

            verify(userServiceMock).checkUserExist(1L);
            verifyNoInteractions(projectServiceMock);
        }

        @Test
        void whenProjectIdValidAndAuthorIdNull_thenChecksProject() {
            createPostDto = CreatePostDto.builder()
                    .authorId(null)
                    .projectId(1L)
                    .content("test content")
                    .build();
            doNothing().when(projectServiceMock).checkProjectExist(createPostDto.getProjectId());

            assertDoesNotThrow(() -> postServiceUtils.isAuthorOrProjectAdded(createPostDto));

            verify(projectServiceMock).checkProjectExist(1L);
            verifyNoInteractions(userServiceMock);
        }

        @Test
        void whenBothAuthorIdAndProjectIdNull_thenThrowsIllegalArgumentException() {
            createPostDto = CreatePostDto.builder()
                    .authorId(null)
                    .projectId(null)
                    .content("test content")
                    .build();

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> postServiceUtils.isAuthorOrProjectAdded(createPostDto));
            assertEquals("Exactly one of AuthorId or ProjectId must be provided as a positive value.",
                    exception.getMessage());
            verifyNoInteractions(userServiceMock);
            verifyNoInteractions(projectServiceMock);
        }

    }

    @Nested
    @DisplayName("isPostExists Method Tests")
    class IsPostExistsTests {

        @Test
        @DisplayName("Post exists - should return Post object")
        void whenPostExists_thenReturnPost() {
            Long postId = 1L;
            Post expectedPost;
            expectedPost = Post.builder()
                    .id(postId)
                    .build();
            when(postRepositoryMock.findById(postId)).thenReturn(Optional.of(expectedPost));

            Post actualPost = postServiceUtils.checkPostExists(postId);

            assertNotNull(actualPost);
            assertEquals(expectedPost, actualPost);
            verify(postRepositoryMock).findById(postId);
        }

        @Test
        @DisplayName("Post does not exist - should throw IllegalArgumentException")
        void whenPostDoesNotExist_thenThrowIllegalArgumentException() {
            Long postId = 2L;
            when(postRepositoryMock.findById(postId)).thenReturn(Optional.empty());

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                    () -> postServiceUtils.checkPostExists(postId));
            assertEquals("Post not found", exception.getMessage());

            verify(postRepositoryMock).findById(postId);
        }
    }
}