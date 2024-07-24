package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataOperationException;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.util.TestData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @InjectMocks
    private PostService postService;
    @Mock
    private PostRepository postRepository;
    @Mock
    private PostVerifier postVerifier;
    @Spy
    private PostMapper postMapper = Mappers.getMapper(PostMapper.class);
    @Spy
    private TestData testData;

    private PostDto postDto;
    private Post post;
    private List<Post> draftsOfUser;
    private List<Post> draftsOfProject;
    private List<Post> postsOfUser;
    private List<Post> postsOfProject;

    @BeforeEach
    void setUp() {
        postDto = testData.returnPostDto();
        post = testData.returnPostCreatedByUser(TestData.AUTHOR_ID, "content A", TestData.CREATED_AT, false);
        draftsOfUser = testData.getDraftsOfUser();
        draftsOfProject = testData.getDraftsOfProject();
        postsOfUser = testData.getPostsOfUser();
        postsOfProject = testData.getPostsOfProject();
    }

    @Nested
    class PositiveTests {
        @Test
        void createPostByUserTest() {
            when(postMapper.toEntity(any())).thenReturn(post);

            assertDoesNotThrow(() -> postService.createPost(postDto));

            verify(postRepository).save(post);
            assertFalse(post.isPublished());
            assertFalse(post.isDeleted());
        }

        @Test
        void publishPostTest() {
            when(postRepository.findById(anyLong())).thenReturn(Optional.ofNullable(post));

            assertDoesNotThrow(() -> postService.publishPost(anyLong()));

            assertTrue(post.isPublished());
            assertEquals(0, ChronoUnit.MINUTES.between(LocalDateTime.now(), post.getPublishedAt()));
            verify(postRepository).save(post);
        }

        @Test
        void updatePostTest() {
            postDto.setContent("New content");
            when(postRepository.findById(anyLong())).thenReturn(Optional.ofNullable(post));

            assertDoesNotThrow(() -> postService.updatePost(postDto));

            verify(postRepository).save(any(Post.class));
            assertEquals(postDto.getContent(), post.getContent());
        }

        @Test
        void deletePostTest() {
            when(postRepository.findById(anyLong())).thenReturn(Optional.ofNullable(post));

            assertDoesNotThrow(() -> postService.deletePost(anyLong()));

            verify(postRepository).save(any(Post.class));
            assertTrue(post.isDeleted());
        }

        @Test
        void getPostByIdTest() {
            when(postRepository.findById(anyLong())).thenReturn(Optional.ofNullable(post));

            assertDoesNotThrow(() -> postService.getPostById(anyLong()));

            verify(postMapper).toDto(any(Post.class));
        }

        @Test
        void getDraftsOfUserTest() {
            List<PostDto> expectedDrafts = draftsOfUser.stream()
                    .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                    .map(postMapper::toDto)
                    .toList();

            when(postRepository.findByAuthorId(anyLong())).thenReturn(draftsOfUser);

            List<PostDto> actualDrafts = assertDoesNotThrow(() -> postService.getDraftsOfUser(anyLong()));

            assertEquals(expectedDrafts, actualDrafts);
        }

        @Test
        void getDraftsOfProjectTest() {
            List<PostDto> expectedDrafts = draftsOfProject.stream()
                    .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                    .map(postMapper::toDto)
                    .toList();

            when(postRepository.findByProjectId(anyLong())).thenReturn(draftsOfProject);

            List<PostDto> actualDrafts = assertDoesNotThrow(() -> postService.getDraftsOfProject(anyLong()));

            assertEquals(expectedDrafts, actualDrafts);
        }

        @Test
        void getPostsOfUserTest() {
            List<PostDto> expectedPosts = postsOfUser.stream()
                    .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                    .map(postMapper::toDto)
                    .toList();

            when(postRepository.findByAuthorId(anyLong())).thenReturn(postsOfUser);

            List<PostDto> actualPosts = assertDoesNotThrow(() -> postService.getPostsOfUser(anyLong()));

            assertEquals(expectedPosts, actualPosts);
        }

        @Test
        void getPostsOfProjectTest() {
            List<PostDto> expectedPosts = postsOfProject.stream()
                    .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                    .map(postMapper::toDto)
                    .toList();

            when(postRepository.findByProjectId(anyLong())).thenReturn(postsOfProject);

            List<PostDto> actualPosts = assertDoesNotThrow(() -> postService.getPostsOfProject(anyLong()));

            assertEquals(expectedPosts, actualPosts);
        }
    }

    @Nested
    class NegativeTests {
        @DisplayName("should throw DataValidationException when post author doesn't exist")
        @Test
        void createPostByUserTest() {
            doThrow(DataValidationException.class).when(postVerifier).verifyAuthorExistence(any(), any());

            assertThrows(DataValidationException.class, () -> postService.createPost(postDto));

            verifyNoInteractions(postRepository);
        }

        @DisplayName("should throw exception when post to be published is nonexistent")
        @Test
        void publishNonexistentPostTest() {
            when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThrows(DataValidationException.class, () -> postService.publishPost(anyLong()));

            assertFalse(post.isPublished());
            assertNull(post.getPublishedAt());
            verifyNoMoreInteractions(postRepository);
        }

        @DisplayName("should throw exception when post to be published is already published")
        @Test
        void rePublishPostTest() {
            post.setPublished(true);
            when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

            assertThrows(DataOperationException.class, () -> postService.publishPost(anyLong()));

            verifyNoMoreInteractions(postRepository);
        }

        @DisplayName("should throw exception when post to be updated is nonexistent")
        @Test
        void updateNonexistentPostTest() {
            postDto.setContent("New content");
            when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThrows(DataValidationException.class, () -> postService.updatePost(postDto));

            verifyNoMoreInteractions(postRepository);
            assertNotEquals(postDto.getContent(), post.getContent());
        }

        @DisplayName("should throw exception when post author is nonexistent")
        @Test
        void updatePostWithNonexistentAuthorTest() {
            postDto.setContent("New content");
            doThrow(DataValidationException.class).when(postVerifier).verifyAuthorExistence(any(), any());

            assertThrows(DataValidationException.class, () -> postService.updatePost(postDto));

            verifyNoMoreInteractions(postRepository);
            assertNotEquals(postDto.getContent(), post.getContent());
        }

        @DisplayName("should throw exception when post to be updated doesn't match with system")
        @Test
        void updatePostWithInvalidDtoTest() {
            postDto.setContent("New content");
            doThrow(DataValidationException.class).when(postVerifier).verifyPostMatchingSystem(any(), any());
            when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

            assertThrows(DataValidationException.class, () -> postService.updatePost(postDto));

            verifyNoMoreInteractions(postRepository);
            assertNotEquals(postDto.getContent(), post.getContent());
        }

        @DisplayName("should throw exception when post to be deleted doesn't exist")
        @Test
        void deleteNonexistentPostTest() {
            when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

            assertThrows(DataValidationException.class, () -> postService.deletePost(anyLong()));

            verifyNoMoreInteractions(postRepository);
            assertFalse(post.isDeleted());
        }

        @DisplayName("should throw exception when post to be deleted is already deleted")
        @Test
        void deleteDeletedPostTest() {
            post.setDeleted(true);
            when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

            assertThrows(DataValidationException.class, () -> postService.deletePost(anyLong()));

            verifyNoMoreInteractions(postRepository);
        }
    }
}