package faang.school.postservice.service.post;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.common.PageResponse;
import faang.school.postservice.dto.post.PostCreateDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostUpdateDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exeption.DataValidationException;
import faang.school.postservice.exeption.ForbiddenException;
import faang.school.postservice.helpers.TestUtils;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {
    @Mock
    private PostRepository postRepository;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private ProjectServiceClient projectServiceClient;
    @Mock
    private UserContext userContext;
    @InjectMocks
    private PostService postService;
    @Captor
    private ArgumentCaptor<Post> postCaptor;

    private final Long userId = 1L;
    private final Long projectId = 2L;
    private final Long postId = 3L;
    private final UserDto user = new UserDto(userId, "name", "email");
    private final ProjectDto project = new ProjectDto(projectId, "title", userId);
    private final String content = "Post content";
    private final Post post = Post.builder().id(postId).build();
    private final Pageable pageableDefault = PageRequest.of(0, 5);

    @Test
    void createPostAsDraft_shouldCreatePostByAuthor() {
        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(user);
        when(postRepository.save(postCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        PostCreateDto postCreateDto = new PostCreateDto(content, null);

        PostDto createdDraft = postService.createPostAsDraft(postCreateDto);

        Post capturedPost = postCaptor.getValue();
        assertEquals(capturedPost.getAuthorId(), userId);
        assertNull(capturedPost.getProjectId());
        assertEquals(content, capturedPost.getContent());
        assertEquals(createdDraft.authorId(), userId);
        assertEquals(content, createdDraft.content());
        assertNull(createdDraft.projectId());
    }

    @Test
    void createPostAsDraft_shouldCreatePostByProject() {
        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(user);
        when(projectServiceClient.getProject(projectId)).thenReturn(project);
        when(postRepository.save(postCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        PostCreateDto postCreateDto = new PostCreateDto(content, projectId);

        PostDto createdDraft = postService.createPostAsDraft(postCreateDto);

        Post capturedPost = postCaptor.getValue();
        assertNull(capturedPost.getAuthorId());
        assertEquals(capturedPost.getProjectId(), projectId);
        assertEquals(capturedPost.getContent(), content);
        assertNull(createdDraft.authorId());
        assertEquals(createdDraft.content(), content);
        assertEquals(createdDraft.projectId(), projectId);
    }

    @Test
    void publishPost_shouldThrowException_whenPostAlreadyPublished() {
        post.setPublished(true);

        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(user);
        when(postRepository.getByIdOrThrow(postId)).thenReturn(post);

        TestUtils.assertThrowsWithMessage(
                ForbiddenException.class,
                "Post id=%s is already published".formatted(postId),
                () -> postService.publishPost(postId)
        );
    }

    @Test
    void publishPost_shouldThrowException_whenValidationUpdatingPostFailsByAuthorId() {
        post.setPublished(false);
        post.setAuthorId(userId + 1);

        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(user);
        when(postRepository.getByIdOrThrow(postId)).thenReturn(post);

        TestUtils.assertThrowsWithMessage(
                ForbiddenException.class,
                "User id=%s is not author of post".formatted(userId),
                () -> postService.publishPost(postId)
        );
    }

    @Test
    void publishPost_shouldThrowException_whenValidationUpdatingPostFailsByProjectId() {
        post.setPublished(false);
        post.setProjectId(projectId);

        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(user);
        when(postRepository.getByIdOrThrow(postId)).thenReturn(post);
        when(projectServiceClient.getProject(projectId)).thenReturn(
                new ProjectDto(projectId, "title", userId + 1)
        );

        TestUtils.assertThrowsWithMessage(
                ForbiddenException.class,
                "User id=%s is not owner of project id=%s".formatted(userId, projectId),
                () -> postService.publishPost(postId)
        );
    }

    @Test
    void publishPost_shouldPublishPost_whenAllDataValid() {
        post.setPublished(false);
        post.setContent(content);
        post.setAuthorId(userId);

        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(user);
        when(postRepository.getByIdOrThrow(postId)).thenReturn(post);
        when(postRepository.save(postCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        PostDto publishedPost = postService.publishPost(postId);
        Post capturedPost = postCaptor.getValue();

        assertTrue(capturedPost.isPublished());
        TestUtils.assertTimestamp(capturedPost.getPublishedAt());
        assertTrue(publishedPost.published());
        TestUtils.assertTimestamp(publishedPost.publishedAt());
    }

    @Test
    void updatePost_shouldThrowException_whenValidationUpdatingPostFailsByAuthorId() {
        post.setAuthorId(userId + 1);
        post.setContent(content);

        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(user);
        when(postRepository.getByIdOrThrow(postId)).thenReturn(post);

        TestUtils.assertThrowsWithMessage(
                ForbiddenException.class,
                "User id=%s is not author of post".formatted(userId),
                () -> postService.updatePost(postId, new PostUpdateDto("Updated content"))
        );
    }

    @Test
    void updatePost_shouldThrowException_whenValidationUpdatingPostFailsByProjectId() {
        post.setProjectId(projectId);
        post.setContent(content);

        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(user);
        when(postRepository.getByIdOrThrow(postId)).thenReturn(post);
        when(projectServiceClient.getProject(projectId)).thenReturn(
                new ProjectDto(projectId, "title", userId + 1)
        );

        TestUtils.assertThrowsWithMessage(
                ForbiddenException.class,
                "User id=%s is not owner of project id=%s".formatted(userId, projectId),
                () -> postService.updatePost(postId, new PostUpdateDto("Updated content"))
        );
    }

    @Test
    void updatePost_shouldUpdatePost_whenAllDataValid() {
        post.setAuthorId(userId);
        post.setContent(content);

        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(user);
        when(postRepository.getByIdOrThrow(postId)).thenReturn(post);
        when(postRepository.save(postCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        String updatedContent = "Updated content";
        PostDto updated = postService.updatePost(postId, new PostUpdateDto(updatedContent));
        Post capturedPost = postCaptor.getValue();

        assertEquals(updatedContent, capturedPost.getContent());
        assertEquals(updatedContent, updated.content());
        assertEquals(userId, updated.authorId());
    }

    @Test
    void deletePostSoftly_shouldThrowException_whenPostAlreadyDeleted() {
        post.setAuthorId(userId);
        post.setDeleted(true);

        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(user);
        when(postRepository.getByIdOrThrow(postId)).thenReturn(post);

        TestUtils.assertThrowsWithMessage(
                ForbiddenException.class,
                "Post id=%s is already deleted".formatted(postId),
                () -> postService.deletePostSoftly(postId)
        );
    }

    @Test
    void deletePostSoftly_shouldThrowException_whenValidationUpdatingPostFailsByAuthorId() {
        post.setAuthorId(userId + 1);

        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(user);
        when(postRepository.getByIdOrThrow(postId)).thenReturn(post);

        TestUtils.assertThrowsWithMessage(
                ForbiddenException.class,
                "User id=%s is not author of post".formatted(userId),
                () -> postService.deletePostSoftly(postId)
        );
    }

    @Test
    void deletePostSoftly_shouldThrowException_whenValidationUpdatingPostFailsByProjectId() {
        post.setProjectId(projectId);

        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(user);
        when(postRepository.getByIdOrThrow(postId)).thenReturn(post);
        when(projectServiceClient.getProject(projectId)).thenReturn(
                new ProjectDto(projectId, "title", userId + 1)
        );

        TestUtils.assertThrowsWithMessage(
                ForbiddenException.class,
                "User id=%s is not owner of project id=%s".formatted(userId, projectId),
                () -> postService.deletePostSoftly(postId)
        );
    }

    @Test
    void deletePostSoftly_shouldDeletePost_whenAllDataValid() {
        post.setAuthorId(userId);
        post.setDeleted(false);

        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(user);
        when(postRepository.getByIdOrThrow(postId)).thenReturn(post);
        when(postRepository.save(postCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        postService.deletePostSoftly(postId);
        Post capturedPost = postCaptor.getValue();

        assertTrue(capturedPost.isDeleted());
    }

    @Test
    void findById_shouldReturnPostDto_whenPostExists() {
        post.setAuthorId(userId);
        post.setContent(content);

        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(user);
        when(postRepository.getByIdOrThrow(postId)).thenReturn(post);

        PostDto found = postService.findById(postId);

        assertEquals(postId, found.id());
        assertEquals(content, found.content());
        assertEquals(userId, found.authorId());
    }

    @Test
    void findAllDraftsByAuthor_shouldReturnPage() {
        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(user);

        List<Post> posts = generateListPosts();
        Page<Post> page = new PageImpl(posts.subList(0, pageableDefault.getPageSize()), pageableDefault, posts.size());
        when(postRepository.findAll(any(Specification.class), eq(pageableDefault))).thenReturn(page);

        PageResponse<PostDto> pageResponse = postService.findAllDraftsByAuthor(pageableDefault);

        assertEquals(0, pageResponse.pageNumber());
        assertEquals(5, pageResponse.pageSize());
        assertEquals(2, pageResponse.totalPages());
        assertEquals(10, pageResponse.totalElements());

        List<PostDto> content = pageResponse.content();
        assertEquals(5, content.size());
        assertEquals(content.get(0), PostMapper.toDto(posts.get(0)));
        assertEquals(content.get(1), PostMapper.toDto(posts.get(1)));
        assertEquals(content.get(2), PostMapper.toDto(posts.get(2)));
        assertEquals(content.get(3), PostMapper.toDto(posts.get(3)));
        assertEquals(content.get(4), PostMapper.toDto(posts.get(4)));
    }

    @Test
    void findAllDraftsByProject_shouldReturnPage() {
        when(userContext.getUserId()).thenReturn(userId);
        when(userServiceClient.getUser(userId)).thenReturn(user);
        when(projectServiceClient.getProject(projectId)).thenReturn(project);

        List<Post> posts = generateListPosts();
        Page<Post> page = new PageImpl(posts.subList(0, pageableDefault.getPageSize()), pageableDefault, posts.size());
        when(postRepository.findAll(any(Specification.class), eq(pageableDefault))).thenReturn(page);

        PageResponse<PostDto> pageResponse = postService.findAllDraftsByProject(projectId, pageableDefault);

        assertEquals(0, pageResponse.pageNumber());
        assertEquals(5, pageResponse.pageSize());
        assertEquals(2, pageResponse.totalPages());
        assertEquals(10, pageResponse.totalElements());

        List<PostDto> content = pageResponse.content();
        assertEquals(5, content.size());
        assertEquals(content.get(0), PostMapper.toDto(posts.get(0)));
        assertEquals(content.get(1), PostMapper.toDto(posts.get(1)));
        assertEquals(content.get(2), PostMapper.toDto(posts.get(2)));
        assertEquals(content.get(3), PostMapper.toDto(posts.get(3)));
        assertEquals(content.get(4), PostMapper.toDto(posts.get(4)));
    }

    @Test
    void findAllPublishedByFilter_shouldThrowException_whenBothAuthorIdAndProjectIdProvided() {
        TestUtils.assertThrowsWithMessage(
                DataValidationException.class,
                "Use authorId or projectId, not both",
                () -> postService.findAllPublishedByFilter(userId, projectId, pageableDefault)
        );
    }

    @Test
    void findAllPublishedByFilter_shouldThrowException_whenNeitherAuthorIdNorProjectIdProvided() {
        TestUtils.assertThrowsWithMessage(
                DataValidationException.class,
                "Use authorId or projectId, not both",
                () -> postService.findAllPublishedByFilter(null, null, pageableDefault)
        );
    }

    @Test
    void findAllPublishedByFilter_shouldReturnPage_whenAuthorIdProvided() {
        List<Post> posts = generateListPosts();
        Page<Post> page = new PageImpl(posts.subList(0, pageableDefault.getPageSize()), pageableDefault, posts.size());
        when(postRepository.findAll(any(Specification.class), eq(pageableDefault))).thenReturn(page);

        PageResponse<PostDto> pageResponse = postService.findAllPublishedByFilter(userId, null, pageableDefault);

        assertEquals(0, pageResponse.pageNumber());
        assertEquals(5, pageResponse.pageSize());
        assertEquals(2, pageResponse.totalPages());
        assertEquals(10, pageResponse.totalElements());

        List<PostDto> content = pageResponse.content();
        assertEquals(5, content.size());
        assertEquals(content.get(0), PostMapper.toDto(posts.get(0)));
        assertEquals(content.get(1), PostMapper.toDto(posts.get(1)));
        assertEquals(content.get(2), PostMapper.toDto(posts.get(2)));
        assertEquals(content.get(3), PostMapper.toDto(posts.get(3)));
        assertEquals(content.get(4), PostMapper.toDto(posts.get(4)));
    }

    @Test
    void findAllPublishedByFilter_shouldReturnPage_whenProjectIdProvided() {
        List<Post> posts = generateListPosts();
        Page<Post> page = new PageImpl(posts.subList(0, pageableDefault.getPageSize()), pageableDefault, posts.size());
        when(postRepository.findAll(any(Specification.class), eq(pageableDefault))).thenReturn(page);

        PageResponse<PostDto> pageResponse = postService.findAllPublishedByFilter(null, projectId, pageableDefault);

        assertEquals(0, pageResponse.pageNumber());
        assertEquals(5, pageResponse.pageSize());
        assertEquals(2, pageResponse.totalPages());
        assertEquals(10, pageResponse.totalElements());

        List<PostDto> content = pageResponse.content();
        assertEquals(5, content.size());
        assertEquals(content.get(0), PostMapper.toDto(posts.get(0)));
        assertEquals(content.get(1), PostMapper.toDto(posts.get(1)));
        assertEquals(content.get(2), PostMapper.toDto(posts.get(2)));
        assertEquals(content.get(3), PostMapper.toDto(posts.get(3)));
        assertEquals(content.get(4), PostMapper.toDto(posts.get(4)));
    }

    private List<Post> generateListPosts() {
        return List.of(
                Post.builder().id(1L).content("content1").build(),
                Post.builder().id(2L).content("content2").build(),
                Post.builder().id(3L).content("content3").build(),
                Post.builder().id(4L).content("content4").build(),
                Post.builder().id(5L).content("content").build(),
                Post.builder().id(6L).content("content6").build(),
                Post.builder().id(7L).content("content7").build(),
                Post.builder().id(8L).content("content8").build(),
                Post.builder().id(9L).content("content9").build(),
                Post.builder().id(10L).content("content10").build()
        );
    }

}