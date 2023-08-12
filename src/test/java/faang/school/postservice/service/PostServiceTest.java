package faang.school.postservice.service;

import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.validator.PostValidator;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
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
    private final Long USER_ID = 1L;
    private final Long PROJECT_ID = 1L;

    @Mock
    private PostRepository postRepository;
    @Spy
    private PostMapperImpl postMapperImpl;
    @Mock
    private UserServiceClient userServiceClient;
    @Mock
    private ProjectServiceClient projectServiceClient;
    @Spy
    private PostValidator postValidator;
    @InjectMocks
    private PostService postService;

    private Long postId;
    private UserDto userDto;
    private ProjectDto projectDto;
    private PostDto postWithAuthorIdDto;
    private PostDto postWithProjectIdDto;
    private PostDto postWithAuthorIdAndProjectIdDto;

    @BeforeEach
    void setUp() {
        postId = 1L;
        userDto = UserDto.builder().id(USER_ID).build();
        projectDto = ProjectDto.builder().id(PROJECT_ID).build();
        postWithAuthorIdDto = PostDto.builder().authorId(userDto.getId()).projectId(null).content("content").build();
        postWithProjectIdDto = PostDto.builder().authorId(null).projectId(projectDto.getId()).content("content2").build();
        postWithAuthorIdAndProjectIdDto = PostDto.builder().authorId(userDto.getId()).projectId(projectDto.getId()).content("content3").build();

    }

    @Test
    void testCreatePostWithAuthorIdSuccess() {
        when(userServiceClient.getUser(USER_ID)).thenReturn(userDto);
        Post post = postMapperImpl.toPost(postWithAuthorIdDto);
        when(postRepository.save(post)).thenReturn(post);
        when(userServiceClient.getUser(USER_ID)).thenReturn(userDto);
        PostDto postDto = postService.createPost(postWithAuthorIdDto);
        verify(postValidator, Mockito.times(1)).validatePostContent(postWithAuthorIdDto);
        assertEquals(postDto, postWithAuthorIdDto);
    }

    @Test
    void testCreatePostWithAuthorIdFail() {
        when(userServiceClient.getUser(USER_ID)).thenThrow(NullPointerException.class);
        assertThrows(NullPointerException.class, () -> postService.createPost(postWithAuthorIdDto));
    }

    @Test
    void testCreatePostWithProjectIdSuccess() {
        when(projectServiceClient.getProject(PROJECT_ID)).thenReturn(projectDto);
        Post post = postMapperImpl.toPost(postWithProjectIdDto);
        when(postRepository.save(post)).thenReturn(post);
        when(projectServiceClient.getProject(PROJECT_ID)).thenReturn(projectDto);
        PostDto postDto = postService.createPost(postWithProjectIdDto);
        verify(postValidator, Mockito.times(1)).validatePostContent(postWithProjectIdDto);
        assertEquals(postDto, postWithProjectIdDto);
    }

    @Test
    void testCreatePostWithProjectIdFail() {
        when(projectServiceClient.getProject(PROJECT_ID)).thenThrow(NullPointerException.class);
        assertThrows(NullPointerException.class, () -> postService.createPost(postWithProjectIdDto));
    }

    @Test
    void testCreatePostWithAuthorIdAndProjectIdFail() {
        try {
            postService.createPost(postWithAuthorIdAndProjectIdDto);
            when(projectServiceClient.getProject(PROJECT_ID)).thenReturn(projectDto);
            when(userServiceClient.getUser(USER_ID)).thenReturn(userDto);
        } catch (DataValidationException e) {
            assertEquals("Author and project cannot be specified at the same time", e.getMessage());
        }
    }

    @Test
    void testPublishPostSuccess() {
        PostDto postDto = PostDto.builder().id(postId).createdAt(null).isPublished(false).build();
        Post post = postMapperImpl.toPost(postDto);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(post);

        postService.publishPost(post.getId());

        assertTrue(post.isPublished());
        assertNotNull(post.getPublishedAt());
    }

    @Test
    void testPublishPostShouldThrowEntityNotFoundException() {
        PostDto postDto = PostDto.builder().id(postId).createdAt(null).isPublished(false).build();
        Post post = postMapperImpl.toPost(postDto);

        assertThrows(EntityNotFoundException.class, () ->
                postService.publishPost(post.getId()), "Post not found");
    }

    @Test
    void testPublishPostShouldThrowDataValidationException() {
        PostDto postDto = PostDto.builder().id(postId).createdAt(LocalDateTime.now()).isPublished(true).build();
        Post post = postMapperImpl.toPost(postDto);
        try {
            when(postRepository.findById(postId)).thenReturn(Optional.of(post));
            postService.publishPost(post.getId());
        } catch (DataValidationException e) {
            assertEquals("Post is already published", e.getMessage());
        }
    }

    @Test
    void testUpdatePostWithAuthorIdSuccess() {
        String content = "updated content";
        PostDto updatedPostDto = PostDto.builder().content(content).authorId(userDto.getId()).build();
        Post updatedPost = postMapperImpl.toPost(updatedPostDto);

        when(postRepository.findById(updatedPost.getId())).thenReturn(Optional.of(updatedPost));
        postService.updatePost(updatedPostDto);

        verify(postValidator, Mockito.times(1)).validationOfPostUpdate(updatedPostDto, updatedPost);
        verify(postRepository, Mockito.times(1)).save(updatedPost);

        assertEquals(content, updatedPost.getContent());
    }

    @Test
    void testUpdatePostWithAuthorIdFailIfPostNotFound() {
        Assertions.assertEquals("Post not found", Assertions.assertThrows(EntityNotFoundException.class, () -> postService.updatePost(postWithAuthorIdDto)).getMessage());
    }

    @Test
    void testUpdatePostWithProjectIdSuccess() {
        String content = "updated content";
        PostDto updatedPostDto = PostDto.builder().content(content).projectId(projectDto.getId()).build();
        Post updatedPost = postMapperImpl.toPost(updatedPostDto);

        when(postRepository.findById(updatedPost.getId())).thenReturn(Optional.of(updatedPost));
        postService.updatePost(updatedPostDto);

        verify(postValidator, Mockito.times(1)).validationOfPostUpdate(updatedPostDto, updatedPost);
        verify(postRepository, Mockito.times(1)).save(updatedPost);

        assertEquals(content, updatedPost.getContent());
    }

    @Test
    void testUpdatePostWithProjectIdFailIfPostNotFound() {
        Assertions.assertEquals("Post not found", Assertions.assertThrows(EntityNotFoundException.class, () -> postService.updatePost(postWithProjectIdDto)).getMessage());
    }

    @Test
    void testUpdatePostWithAuthorIdFailIfAuthorIdHasBeenChanged() {
        Long newAuthorId = 2L;
        PostDto updatedPostDto = PostDto.builder().content("updated content").authorId(newAuthorId).build();
        Post updatedPost = postMapperImpl.toPost(updatedPostDto);
        try {
            when(postRepository.findById(updatedPost.getId())).thenReturn(Optional.of(updatedPost));
            postService.updatePost(updatedPostDto);
            verify(postValidator, Mockito.times(1)).validationOfPostUpdate(updatedPostDto, updatedPost);
        } catch (DataValidationException e) {
            assertEquals("You cannot change the author of the post", e.getMessage());
        }
    }

    @Test
    void testUpdatePostWithProjectIdFailIfProjectIdHasBeenChanged() {
        Long newProjectId = 2L;
        PostDto updatedPostDto = PostDto.builder().content("updated content").projectId(newProjectId).build();
        Post updatedPost = postMapperImpl.toPost(updatedPostDto);
        try {
            when(postRepository.findById(updatedPost.getId())).thenReturn(Optional.of(updatedPost));
            postService.updatePost(updatedPostDto);
            verify(postValidator, Mockito.times(1)).validationOfPostUpdate(updatedPostDto, updatedPost);
        } catch (DataValidationException e) {
            assertEquals("You cannot change the project of the post", e.getMessage());
        }
    }

    @Test
    void testGetPostSuccess() {
        Post post = Post.builder().id(1L).build();
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));
        PostDto postDto = postService.getPost(post.getId());
        assertEquals(postDto, postMapperImpl.toDto(post));
    }

    @Test
    void testGetPostFail() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () -> postService.getPost(1L));
    }

    @Test
    void testGetNotDeletedDraftsByAuthorIdSuccess() {
        when(userServiceClient.getUser(USER_ID)).thenReturn(userDto);
        when(postRepository.findDraftsByAuthorId(userDto.getId()))
                .thenReturn(List.of(Post.builder().authorId(userDto.getId()).build()));
        List<PostDto> posts = postService.getNotDeletedDraftsByAuthorId(userDto.getId());

        assertEquals(1, posts.size());
    }

    @Test
    void testGetNotDeletedDraftsByAuthorIdFailIfUserNotFound() {
        when(userServiceClient.getUser(USER_ID)).thenThrow(NullPointerException.class);
        assertThrows((NullPointerException.class), () -> postService.getNotDeletedDraftsByAuthorId(USER_ID));
    }

    @Test
    void testGetNotDeletedDraftsByAuthorIdFailIfNoDraftsFound() {
        when(userServiceClient.getUser(USER_ID)).thenReturn(userDto);
        when(postRepository.findDraftsByAuthorId(USER_ID)).thenReturn(List.of());
        assertEquals(0, postService.getNotDeletedDraftsByAuthorId(USER_ID).size());
    }

    @Test
    void testGetNotDeletedDraftsByProjectIdSuccess() {
        when(projectServiceClient.getProject(PROJECT_ID)).thenReturn(projectDto);
        when(postRepository.findDraftsByProjectId(projectDto.getId()))
                .thenReturn(List.of(Post.builder().projectId(projectDto.getId()).build()));
        List<PostDto> posts = postService.getNotDeletedDraftsByProjectId(projectDto.getId());

        assertEquals(1, posts.size());
    }

    @Test
    void testGetNotDeletedDraftsByProjectIdFailIfProjectNotFound() {
        when(projectServiceClient.getProject(PROJECT_ID)).thenThrow(NullPointerException.class);
        assertThrows((NullPointerException.class), () -> postService.getNotDeletedDraftsByProjectId(PROJECT_ID));
    }

    @Test
    void testGetNotDeletedDraftsByProjectIdFailIfNoDraftsFound() {
        when(projectServiceClient.getProject(PROJECT_ID)).thenReturn(projectDto);
        when(postRepository.findDraftsByProjectId(PROJECT_ID)).thenReturn(List.of());
        assertEquals(0, postService.getNotDeletedDraftsByProjectId(PROJECT_ID).size());
    }

    @Test
    void testGetPublishedPostByAuthorIdSuccess() {
        when(userServiceClient.getUser(USER_ID)).thenReturn(userDto);
        when(postRepository.findPublishedPostsByAuthorId(userDto.getId()))
                .thenReturn(List.of(Post.builder().authorId(userDto.getId()).published(true).deleted(false).build()));
        List<PostDto> posts = postService.getNotDeletedPublishedPostsByAuthorId(userDto.getId());
        assertEquals(1, posts.size());
        posts.forEach(post -> assertTrue(post.isPublished()));
    }

    @Test
    void testGetPublishedPostByAuthorIdFailIfUserNotFound() {
        when(userServiceClient.getUser(USER_ID)).thenThrow(NullPointerException.class);
        assertThrows((NullPointerException.class), () -> postService.getNotDeletedPublishedPostsByAuthorId(USER_ID));
    }

    @Test
    void testGetPublishedPostByAuthorIdFailIfNoPostsFound() {
        when(userServiceClient.getUser(USER_ID)).thenReturn(userDto);
        when(postRepository.findPublishedPostsByAuthorId(USER_ID)).thenReturn(List.of());
        assertEquals(0, postService.getNotDeletedPublishedPostsByAuthorId(USER_ID).size());
    }

    @Test
    void testGetPublishedPostByProjectIdSuccess() {
        when(projectServiceClient.getProject(PROJECT_ID)).thenReturn(projectDto);
        when(postRepository.findPublishedPostsByProjectId(projectDto.getId())).thenReturn(List.of(Post.builder().projectId(projectDto.getId()).published(true).build()));
        List<PostDto> posts = postService.getNotDeletedPublishedPostsByProjectId(projectDto.getId());
        assertEquals(1, posts.size());
        posts.forEach(post -> assertTrue(post.isPublished()));
    }

    @Test
    void testGetPublishedPostByProjectIdFailIfProjectNotFound() {
        when(projectServiceClient.getProject(PROJECT_ID)).thenThrow(NullPointerException.class);
        assertThrows((NullPointerException.class), () -> postService.getNotDeletedPublishedPostsByProjectId(PROJECT_ID));
    }

    @Test
    void testGetPublishedPostByProjectIdFailIfNoPostsFound() {
        when(projectServiceClient.getProject(PROJECT_ID)).thenReturn(projectDto);
        when(postRepository.findPublishedPostsByProjectId(PROJECT_ID)).thenReturn(List.of());
        assertEquals(0, postService.getNotDeletedPublishedPostsByProjectId(PROJECT_ID).size());
    }

    @Test
    void testSoftDeletePostSuccess() {
        Post post = postMapperImpl.toPost(postWithAuthorIdDto);
        when(postRepository.findById(postWithAuthorIdDto.getId())).thenReturn(Optional.of(post));
        boolean result = postService.softDeletePost(postWithAuthorIdDto.getId());

        assertTrue(post.isDeleted());
        assertTrue(result);
    }

    @Test
    void testSoftDeletePostFailIfPostNotFound() {
        try {
            Long postId = postWithAuthorIdDto.getId();
            postService.softDeletePost(postId);
            when(postRepository.findById(postId)).thenReturn(Optional.empty());
        } catch (EntityNotFoundException e) {
            assertEquals("Post not found", e.getMessage());
        }
    }

    @Test
    void testSoftDeletePostFailIfPostIsAlreadyDeleted() {
        try {
            Post post = postMapperImpl.toPost(postWithAuthorIdDto);
            post.setDeleted(true);
            Long postId = postWithAuthorIdDto.getId();
            when(postRepository.findById(postId)).thenReturn(Optional.of(post));
            postService.softDeletePost(postId);
        } catch (DataValidationException e) {
            assertEquals("Post already deleted", e.getMessage());
        }
    }
}