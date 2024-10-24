package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.request.PostCreationRequest;
import faang.school.postservice.dto.post.request.PostUpdatingRequest;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.resource.ResourceObjectResponse;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.post.PostAlreadyPublishedException;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.model.post.PostCreator;
import faang.school.postservice.publisher.PostViewEventPublisher;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.post.impl.PostServiceImpl;
import faang.school.postservice.service.resource.ResourceService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    private static final int MAX_SIZE_COUNT = 10;

    @Mock
    private PostRepository postRepository;

    @Spy
    private PostMapper postMapper = Mappers.getMapper(PostMapper.class);

    @Mock
    private UserServiceClient userClient;

    @Mock
    private ProjectServiceClient projectClient;

    @Mock
    private PostContentVerifier postContentVerifier;

    @Mock
    private ResourceService resourceService;

    @Mock
    private PostViewEventPublisher postViewEventPublisher;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private PostServiceImpl postService;

    private Post post;
    private PostDto postDto;
    private PostCreationRequest creationRequest;
    private PostUpdatingRequest updatingRequest;
    private final List<Post> posts = new ArrayList<>();

    @BeforeEach
    public void setUp() {
        post = Post.builder()
                .id(0L)
                .authorId(1L)
                .content("Test")
                .projectId(null)
                .published(false)
                .deleted(false)
                .build();
        postDto = postMapper.toPostDto(post);
        creationRequest = PostCreationRequest.builder()
                .authorId(1L)
                .projectId(null)
                .content("Test")
                .build();
        updatingRequest = PostUpdatingRequest.builder().content("Test2").build();
        postService.setMaxFilesCount(MAX_SIZE_COUNT);
        ReflectionTestUtils.setField(postService,"postBatchSize", 3);
    }

    @Test
    @DisplayName("Create Post with not found author")
    public void testCreatePostWithNotExistAuthor() {
        when(userClient.getUser(1L)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> postService.create(creationRequest));
    }

    @Test
    @DisplayName("Create post with not found project")
    public void testCreatePostWithNotExistProject() {
        PostCreationRequest requestWithProject = PostCreationRequest.builder()
                .authorId(null)
                .projectId(1L)
                .content("some test")
                .build();

        when(projectClient.getProject(1L)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> postService.create(requestWithProject));
    }

    @Test
    @DisplayName("Create post with author success")
    public void testCreatePostWithAuthorSuccess() {
        when(userClient.getUser(1L)).thenReturn(new UserDto(1L, "John Doe", "john@doe.com"));
        when(postRepository.save(post)).thenReturn(post);

        postService.create(creationRequest);

        verify(postRepository).save(post);

    }

    @Test
    @DisplayName("Create post with project success")
    public void testCreatePostWithProjectSuccess() {
        post = Post.builder()
                .id(0L)
                .authorId(null)
                .content("Test")
                .projectId(1L)
                .build();
        creationRequest = PostCreationRequest.builder()
                .authorId(null)
                .projectId(1L)
                .content("Test")
                .build();
        when(projectClient.getProject(1L)).thenReturn(new ProjectDto());
        when(postRepository.save(post)).thenReturn(post);

        postService.create(creationRequest);

        verify(postRepository).save(post);
        assertEquals(postDto.id(), post.getId());
    }

    @Test
    @DisplayName("Create post with files success")
    public void testCreatePostWithFilesSuccess() {
        List<MultipartFile> filesToAdd = List.of(initFile("test.jpeg",
                "image/jpeg", new byte[1024]));
        List<Resource> resources = List.of(initResource(1L, "testKey", "test.jpeg",
                1024, "image/jpeg"));
        post = Post.builder()
                .id(0L)
                .authorId(null)
                .content("Test")
                .projectId(1L)
                .build();
        creationRequest = PostCreationRequest.builder()
                .authorId(null)
                .projectId(1L)
                .content("Test")
                .filesToAdd(filesToAdd)
                .build();
        when(projectClient.getProject(1L)).thenReturn(new ProjectDto());
        when(resourceService.addResourcesToPost(filesToAdd, post)).thenReturn(resources);

        postService.create(creationRequest);

        verify(postRepository).save(any());
        verify(resourceService).addResourcesToPost(any(), any());
        assertEquals(postDto.id(), post.getId());
    }

    @Test
    @DisplayName("Create post with too many files")
    public void testCreatePostWithTooManyFiles() {
        List<MultipartFile> filesToAdd = initFiles(MAX_SIZE_COUNT + 1);
        creationRequest = PostCreationRequest.builder()
                .authorId(null)
                .projectId(1L)
                .content("Test")
                .filesToAdd(filesToAdd)
                .build();

        assertThrows(IllegalArgumentException.class, () -> postService.create(creationRequest));
    }

    @Test
    @DisplayName("Publish a post that was not found")
    public void testPublishPostWithNotExistId() {
        when(postRepository.findByIdAndDeletedFalse(0L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> postService.publish(0L));
    }

    @Test
    @DisplayName("Publish a published post")
    public void testPublishPublishedPost() {
        post = Post.builder()
                .id(0L)
                .authorId(1L)
                .content("Test")
                .projectId(null)
                .published(true)
                .build();
        when(postRepository.findByIdAndDeletedFalse(0L)).thenReturn(Optional.of(post));

        assertThrows(PostAlreadyPublishedException.class, () -> postService.publish(0L));
    }

    @Test
    @DisplayName("Publish a post success")
    public void testPublishPostSuccess() {
        when(postRepository.findByIdAndDeletedFalse(0L)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(post);

        postDto = postService.publish(0L);

        verify(postRepository).save(post);
        assertTrue(postDto.published());
        assertNotNull(postDto.publishedAt());
    }

    @Test
    @DisplayName("Update a post with not exists post")
    public void testUpdatePostWithNotExistId() {
        when(postRepository.findByIdAndDeletedFalse(0L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> postService.update(0L, updatingRequest));
    }

    @Test
    @DisplayName("Update a post success")
    public void testUpdatePostSuccess() {
        when(postRepository.findByIdAndDeletedFalse(0L)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(post);
        post.setResources(new ArrayList<>());

        postDto = postService.update(0L, updatingRequest);

        verify(postRepository).save(post);
        assertEquals(postDto.content(), updatingRequest.content());
    }

    @Test
    @DisplayName("Update a post with files to delete success")
    public void testUpdatePostWithFilesToDeleteSuccess() {
        List<Resource> resources = List.of(
                initResource(1L, "testKey", "test.jpeg", 1024, "image/jpeg"),
                initResource(2L, "testKey", "test.jpeg", 1024, "image/jpeg"));
        List<Long> filesToDeleteIds = List.of(1L);
        post.setResources(resources);
        PostUpdatingRequest request = PostUpdatingRequest.builder()
                .content("new content")
                .filesToDeleteIds(filesToDeleteIds)
                .build();
        when(postRepository.findByIdAndDeletedFalse(post.getId())).thenReturn(Optional.of(post));

        postService.update(post.getId(), request);

        verify(resourceService).deleteResourcesFromPost(filesToDeleteIds, post.getId());
        verify(postRepository).save(post);
    }

    @Test
    @DisplayName("Update post with files to add success")
    public void testUpdatePostWithFilesToAddSuccess() {
        List<MultipartFile> filesToAdd = List.of(
                initFile("test.jpeg", "image/jpeg", new byte[1024]));
        PostUpdatingRequest request = PostUpdatingRequest.builder()
                .content("new content")
                .filesToAdd(filesToAdd)
                .build();
        post.setResources(new ArrayList<>());
        when(postRepository.findByIdAndDeletedFalse(post.getId())).thenReturn(Optional.of(post));

        postService.update(post.getId(), request);

        verify(resourceService).addResourcesToPost(filesToAdd, post);
        verify(postRepository).save(post);
    }

    @Test
    @DisplayName("Update post with files to add and delete success")
    public void testUpdatePostWithFilesToAddAndDeleteSuccess() {
        List<Resource> resources = List.of(
                initResource(1L, "testKey", "test.jpeg", 1024, "image/jpeg"),
                initResource(2L, "testKey", "test.jpeg", 1024, "image/jpeg"));
        List<Long> filesToDeleteIds = List.of(1L);
        List<MultipartFile> filesToAdd = List.of(
                initFile("test.jpeg", "image/jpeg", new byte[1024]));
        PostUpdatingRequest request = PostUpdatingRequest.builder()
                .content("new content")
                .filesToDeleteIds(filesToDeleteIds)
                .filesToAdd(filesToAdd)
                .build();
        post.setResources(resources);
        when(postRepository.findByIdAndDeletedFalse(post.getId())).thenReturn(Optional.of(post));

        postService.update(post.getId(), request);

        verify(resourceService).deleteResourcesFromPost(filesToDeleteIds, post.getId());
        verify(resourceService).addResourcesToPost(filesToAdd, post);
        verify(postRepository).save(post);
    }

    @Test
    @DisplayName("Update a post with too many files to delete")
    public void testUpdatePostWithTooManyFilesToDelete() {
        List<Long> filesToDeleteIds = List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L);
        PostUpdatingRequest request = PostUpdatingRequest.builder()
                .content("new content")
                .filesToDeleteIds(filesToDeleteIds)
                .build();
        post.setResources(new ArrayList<>());
        when(postRepository.findByIdAndDeletedFalse(post.getId())).thenReturn(Optional.of(post));

        assertThrows(IllegalArgumentException.class, () -> postService.update(post.getId(), request));
    }

    @Test
    @DisplayName("Update a post with delete more files than post has")
    public void testUpdatePostWithDeleteMoreFilesThanPostHas() {
        List<Resource> resources = List.of(
                initResource(1L, "testKey", "test.jpeg", 1024, "image/jpeg"),
                initResource(2L, "testKey", "test.jpeg", 1024, "image/jpeg"));
        List<Long> filesToDeleteIds = List.of(1L, 2L, 3L);
        post.setResources(resources);
        PostUpdatingRequest request = PostUpdatingRequest.builder()
                .content("new content")
                .filesToDeleteIds(filesToDeleteIds)
                .build();
        when(postRepository.findByIdAndDeletedFalse(post.getId())).thenReturn(Optional.of(post));

        assertThrows(IllegalArgumentException.class, () -> postService.update(post.getId(), request));
    }

    @Test
    @DisplayName("Update a post with add and delete files but exceeded max files count")
    public void testUpdatePostWithAddAndDeleteFilesButExceededMaxFilesCount() {
        List<Resource> resources = List.of(
                initResource(1L, "testKey", "test.jpeg", 1024, "image/jpeg"),
                initResource(2L, "testKey", "test.jpeg", 1024, "image/jpeg"),
                initResource(3L, "testKey", "test.jpeg", 1024, "image/jpeg"),
                initResource(4L, "testKey", "test.jpeg", 1024, "image/jpeg"));
        List<MultipartFile> filesToAdd = initFiles(MAX_SIZE_COUNT);
        List<Long> filesToDeleteIds = List.of(1L);
        post.setResources(resources);
        PostUpdatingRequest request = PostUpdatingRequest.builder()
                .content("new content")
                .filesToDeleteIds(filesToDeleteIds)
                .filesToAdd(filesToAdd)
                .build();
        post.setResources(resources);
        when(postRepository.findByIdAndDeletedFalse(post.getId())).thenReturn(Optional.of(post));

        assertThrows(IllegalArgumentException.class, () -> postService.update(post.getId(), request));
    }

    @Test
    @DisplayName("Update a post with too many files to add")
    public void testUpdatePostWithTooManyFilesToAdd() {
        List<MultipartFile> filesToAdd = initFiles(MAX_SIZE_COUNT + 1);
        PostUpdatingRequest request = PostUpdatingRequest.builder()
                .content("new content")
                .filesToAdd(filesToAdd)
                .build();
        post.setResources(new ArrayList<>());
        when(postRepository.findByIdAndDeletedFalse(post.getId())).thenReturn(Optional.of(post));

        assertThrows(IllegalArgumentException.class, () -> postService.update(post.getId(), request));
    }

    @Test
    @DisplayName("Remove a post with not exists post")
    public void testRemovePostWithNotExistId() {
        when(postRepository.findByIdAndDeletedFalse(0L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> postService.remove(0L));
    }

    @Test
    @DisplayName("Remove a post success")
    public void testRemovePostSuccess() {
        when(postRepository.findByIdAndDeletedFalse(0L)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(post);
        post.setResources(new ArrayList<>());

        postDto = postService.remove(0L);

        verify(postRepository).save(post);
        assertTrue(postDto.deleted());
    }

    @Test
    @DisplayName("Get a post with not exists post")
    public void testGetPostWithNotExistId() {
        when(postRepository.findByIdAndDeletedFalse(0L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> postService.getPostById(0L));
    }

    @Test
    @DisplayName("Get a post success")
    public void testGetPostSuccess() {
        when(postRepository.findByIdAndDeletedFalse(0L)).thenReturn(Optional.of(post));
        when(userContext.getUserId()).thenReturn(2L);

        PostDto postDto = postService.getPostById(0L);

        verify(postRepository).findByIdAndDeletedFalse(0L);
        verify(userContext).getUserId();
        verify(postViewEventPublisher).publish(any());
        assertNotNull(postDto);
        assertEquals(0L, postDto.id());
    }

    @Test
    @DisplayName("Getting empty list of post by author if no unpublished posts found")
    public void testGettingUnpublishedPostsByAuthorWherePostsAreEmpty() {
        List<Post> posts = new ArrayList<>();
        when(postRepository.findByAuthorId(1L)).thenReturn(posts);

        List<PostDto> postDtos = postService.getPostsByCreatorAndPublishedStatus(1L, PostCreator.AUTHOR, false);

        verify(postRepository).findByAuthorId(1L);
        assertEquals(0, postDtos.size());
    }

    @Test
    @DisplayName("Getting unpublished posts by author")
    public void testGettingUnpublishedPostsByAuthor() {
        post = Post.builder()
                .id(0L)
                .published(false)
                .createdAt(LocalDateTime.of(2024, 9, 21, 17, 30))
                .build();
        Post post2 = Post.builder()
                .id(1L)
                .published(false)
                .createdAt(LocalDateTime.of(2024, 9, 21, 17, 32))
                .build();
        Post post3 = Post.builder()
                .id(2L)
                .published(false)
                .createdAt(LocalDateTime.of(2024, 9, 21, 17, 28))
                .build();
        Post post4 = Post.builder()
                .id(3L)
                .published(true)
                .publishedAt(LocalDateTime.of(2024, 9, 21, 17, 22))
                .createdAt(LocalDateTime.of(2024, 9, 21, 17, 22))
                .build();
        List<Post> posts = List.of(post, post2, post3, post4);
        when(postRepository.findByAuthorId(1L)).thenReturn(posts);

        List<PostDto> postDtos = postService.getPostsByCreatorAndPublishedStatus(1L, PostCreator.AUTHOR, false);

        verify(postRepository).findByAuthorId(1L);
        assertEquals(3, postDtos.size());
        assertEquals(1L, postDtos.get(0).id());
        assertEquals(2L, postDtos.get(2).id());
    }

    @Test
    @DisplayName("Getting empty list of post by project if no unpublished posts found")
    public void testGettingUnpublishedPostsByProjectWherePostsAreEmpty() {
        List<Post> posts = new ArrayList<>();
        when(postRepository.findByProjectId(1L)).thenReturn(posts);

        List<PostDto> postDtos = postService.getPostsByCreatorAndPublishedStatus(1L, PostCreator.PROJECT, false);

        verify(postRepository).findByProjectId(1L);
        assertEquals(0, postDtos.size());
    }

    @Test
    @DisplayName("Getting unpublished posts by project")
    public void testGettingUnpublishedPostsByProject() {
        post = Post.builder()
                .id(0L)
                .published(false)
                .createdAt(LocalDateTime.of(2024, 9, 21, 17, 30))
                .build();
        Post post2 = Post.builder()
                .id(1L)
                .published(false)
                .createdAt(LocalDateTime.of(2024, 9, 21, 17, 32))
                .build();
        Post post3 = Post.builder()
                .id(2L)
                .published(false)
                .createdAt(LocalDateTime.of(2024, 9, 21, 17, 28))
                .build();
        Post post4 = Post.builder()
                .id(3L)
                .published(true)
                .publishedAt(LocalDateTime.of(2024, 9, 21, 17, 22))
                .createdAt(LocalDateTime.of(2024, 9, 21, 17, 22))
                .build();
        List<Post> posts = List.of(post, post2, post3, post4);
        when(postRepository.findByProjectId(1L)).thenReturn(posts);

        List<PostDto> postDtos = postService.getPostsByCreatorAndPublishedStatus(1L, PostCreator.PROJECT, false);

        verify(postRepository).findByProjectId(1L);
        assertEquals(3, postDtos.size());
        assertEquals(1L, postDtos.get(0).id());
        assertEquals(2L, postDtos.get(2).id());
    }

    @Test
    @DisplayName("Getting published posts by author")
    public void testGettingPublishedPostsByAuthor() {
        post = Post.builder()
                .id(0L)
                .published(true)
                .publishedAt(LocalDateTime.of(2024, 9, 21, 17, 30))
                .build();
        Post post2 = Post.builder()
                .id(1L)
                .published(true)
                .publishedAt(LocalDateTime.of(2024, 9, 21, 17, 32))
                .build();
        Post post3 = Post.builder()
                .id(2L)
                .published(true)
                .publishedAt(LocalDateTime.of(2024, 9, 21, 17, 28))
                .build();
        Post post4 = Post.builder()
                .id(3L)
                .published(false)
                .publishedAt(LocalDateTime.of(2024, 9, 21, 17, 22))
                .build();

        List<Post> posts = List.of(post, post2, post3, post4);
        when(postRepository.findByAuthorId(1L)).thenReturn(posts);

        List<PostDto> postDtos = postService.getPostsByCreatorAndPublishedStatus(1L, PostCreator.AUTHOR, true);

        verify(postRepository).findByAuthorId(1L);
        assertEquals(3, postDtos.size());
        assertEquals(1L, postDtos.get(0).id());
        assertEquals(2L, postDtos.get(2).id());
    }

    @Test
    @DisplayName("Getting published posts by project")
    public void testGettingPublishedPostsByProject() {
        post = Post.builder()
                .id(0L)
                .published(true)
                .publishedAt(LocalDateTime.of(2024, 9, 21, 17, 30))
                .build();
        Post post2 = Post.builder()
                .id(1L)
                .published(true)
                .publishedAt(LocalDateTime.of(2024, 9, 21, 17, 32))
                .build();
        Post post3 = Post.builder()
                .id(2L)
                .published(true)
                .publishedAt(LocalDateTime.of(2024, 9, 21, 17, 28))
                .build();
        Post post4 = Post.builder()
                .id(3L)
                .published(false)
                .createdAt(LocalDateTime.of(2024, 9, 21, 17, 22))
                .build();
        List<Post> posts = List.of(post, post2, post3, post4);
        when(postRepository.findByProjectId(1L)).thenReturn(posts);

        List<PostDto> postDtos = postService.getPostsByCreatorAndPublishedStatus(1L, PostCreator.PROJECT, true);

        verify(postRepository).findByProjectId(1L);
        assertEquals(3, postDtos.size());
        assertEquals(1L, postDtos.get(0).id());
        assertEquals(2L, postDtos.get(2).id());
    }

    @Test
    public void testModeratePostsWhenNoPostsFound() {
        when(postRepository.findNotVerified()).thenReturn(Collections.emptyList());
        postService.moderatePosts();
        verify(postContentVerifier, never()).verifyPosts(any());
    }

    @Test
    public void testModeratePostsWhenPostsFound() {
        when(postRepository.findNotVerified()).thenReturn(posts);
        postService.moderatePosts();

        verify(postRepository).findNotVerified();
    }

    @Test
    @DisplayName("Getting resources by post id")
    public void testGettingResourcesByPostId() {
        Resource resource = initResource(1L, "file1", "file1.txt", 15, "text/jpeg");
        post.setResources(List.of(resource));
        ResourceObjectResponse responseObject = ResourceObjectResponse.builder()
                .contentType("text/jpeg")
                .contentLength(15)
                .content(new ByteArrayInputStream(new byte[15]))
                .build();
        when(postRepository.findByIdAndDeletedFalse(post.getId())).thenReturn(Optional.of(post));
        when(resourceService.getDownloadedResourceById(1L)).thenReturn(responseObject);

        List<ResourceObjectResponse> result = postService.getResourcesByPostId(post.getId());

        assertEquals(List.of(responseObject), result);
    }

    @Test
    @DisplayName("Getting empty resources by post id")
    public void testGettingEmptyResourcesByPostId() {
        post.setResources(List.of());
        when(postRepository.findByIdAndDeletedFalse(post.getId())).thenReturn(Optional.of(post));

        List<ResourceObjectResponse> result = postService.getResourcesByPostId(post.getId());

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Getting resources from non-existing post")
    public void testGettingResourcesFromNonExistingPost() {
        assertThrows(EntityNotFoundException.class, () -> postService.getResourcesByPostId(1L));
    }

    private MultipartFile initFile(String fileName, String contentType, byte[] content) {
        return new MockMultipartFile("test", fileName, contentType, content);
    }

    private Resource initResource(Long id, String fileKey, String fileName, long size, String contentType) {
        return Resource.builder()
                .id(id)
                .key(fileKey)
                .size(size)
                .name(fileName)
                .type(contentType)
                .build();
    }

    private List<MultipartFile> initFiles(int size) {
        return IntStream.range(0, size)
                .boxed()
                .map(i -> initFile("file" + i, "text/plain", new byte[15]))
                .toList();
    }
}
