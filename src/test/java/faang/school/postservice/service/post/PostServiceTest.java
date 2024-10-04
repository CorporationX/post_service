package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.request.PostCreationRequest;
import faang.school.postservice.dto.post.request.PostUpdatingRequest;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.post.PostAlreadyPublishedException;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.post.PostCreator;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Spy
    private PostMapper postMapper = Mappers.getMapper(PostMapper.class);

    @Mock
    private UserServiceClient userClient;

    @Mock
    private ProjectServiceClient projectClient;

    @Mock
    private ResourceService resourceService;

    @InjectMocks
    private PostServiceImpl postService;

    private Post post;
    private PostDto postDto;
    private PostCreationRequest creationRequest;
    private PostUpdatingRequest updatingRequest;

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

        PostDto postDto = postService.getPostById(0L);

        verify(postRepository).findByIdAndDeletedFalse(0L);
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
}
