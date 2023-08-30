package faang.school.postservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import faang.school.postservice.client.ProjectServiceClient;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.ResponsePostDto;
import faang.school.postservice.dto.post.UpdatePostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.dto.redis.LikeEvent;
import faang.school.postservice.dto.redis.PostViewEventDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.publisher.LikeEventPublisher;
import faang.school.postservice.repository.LikeRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.async.PostAsyncService;
import faang.school.postservice.validator.PostValidator;
import org.apache.commons.collections4.ListUtils;
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
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
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
    @Mock
    private PostViewEventService postViewEventService;
    @Mock
    private UserContext userContext;
    @Spy
    private PostValidator postValidator;
    @Mock
    private PostAsyncService postAsyncService;
    @Mock
    private LikeRepository likeRepository;
    @Mock
    private LikeEventPublisher likeEventPublisher;
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
        PostDto postDto = PostDto.builder().id(postId).createdAt(null).published(false).build();
        Post post = postMapperImpl.toPost(postDto);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(post);

        postService.publishPost(post.getId());

        assertTrue(post.isPublished());
        assertNotNull(post.getPublishedAt());
    }

    @Test
    void testPublishPostShouldThrowEntityNotFoundException() {
        PostDto postDto = PostDto.builder().id(postId).createdAt(null).published(false).build();
        Post post = postMapperImpl.toPost(postDto);

        assertThrows(EntityNotFoundException.class, () ->
                postService.publishPost(post.getId()), "Post with id " + postId + " not found");
    }

    @Test
    void testPublishPostShouldThrowDataValidationException() {
        PostDto postDto = PostDto.builder().id(postId).createdAt(LocalDateTime.now()).published(true).build();
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
        postWithAuthorIdDto.setId(1L);
        Assertions.assertEquals("Post with id " + postId + " not found",
                Assertions.assertThrows(EntityNotFoundException.class, () -> postService.updatePost(postWithAuthorIdDto)).getMessage());
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
        postWithProjectIdDto.setId(1L);
        Assertions.assertEquals("Post with id " + postId + " not found",
                Assertions.assertThrows(EntityNotFoundException.class, () -> postService.updatePost(postWithProjectIdDto)).getMessage());
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
        Post post = new Post();
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        when(userContext.getUserId()).thenReturn(1L);

        PostViewEventDto postViewEventDto = new PostViewEventDto();
        when(postViewEventService.getPostViewEventDto(anyLong(), any(Post.class))).thenReturn(postViewEventDto);

        PostDto postDto = new PostDto();
        when(postMapperImpl.toDto(any(Post.class))).thenReturn(postDto);

        postService.getPost(1L);

        verify(postRepository, times(1)).findById(1L);
        verify(userContext, times(1)).getUserId();
        verify(postViewEventService, times(1)).getPostViewEventDto(1L, post);
        verify(postViewEventService, times(1)).publishEventToChannel(postViewEventDto);
        verify(postMapperImpl, times(1)).toDto(post);
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
            postWithAuthorIdDto.setId(1L);
            Long postId = postWithAuthorIdDto.getId();
            postService.softDeletePost(postId);
            when(postRepository.findById(postId)).thenReturn(Optional.empty());
        } catch (EntityNotFoundException e) {
            assertEquals("Post with id " + postId + " not found", e.getMessage());
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

    @Test
    void testGetPostById_ExistingPostId_ReturnsPost() {
        Long postId = 1L;
        Post existingPost = Post.builder()
                .id(postId)
                .content("Test post content")
                .build();

        when(postRepository.findById(postId)).thenReturn(Optional.of(existingPost));

        Post retrievedPost = postService.getPostById(postId);

        assertNotNull(retrievedPost);
        assertEquals(existingPost.getId(), retrievedPost.getId());
        assertEquals(existingPost.getContent(), retrievedPost.getContent());

        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    void testGetPostById_NonExistingPostId_ThrowsEntityNotFoundException() {
        Long postId = 1L;

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> postService.getPostById(postId));

        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    void testPublishScheduledPosts() {
        Post post = mock(Post.class);
        List<Post> posts = List.of(post, post, post);

        when(postRepository.findReadyToPublish()).thenReturn(posts);

        postService.publishScheduledPosts(1);

        verify(postRepository, times(1)).findReadyToPublish();
        List<List<Post>> partitions = ListUtils.partition(posts, posts.size());
        partitions.forEach(partition -> verify(postAsyncService).publishPosts(partition));
    }

    @Test
    void likePostTest() throws JsonProcessingException {
        Long postId = 1L;
        Long userId = 2L;
        Post post = new Post();
        post.setId(postId);
        post.setAuthorId(userId);
        post.setLikes(List.of(Like.builder().id(12).userId(13L).build()));

        UpdatePostDto updatePostDto = new UpdatePostDto(postId, "qweqwe");

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(likeRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        PostDto postDto = postService.likePost(updatePostDto, userId);

        verify(likeRepository, times(1)).save(any());
        verify(likeEventPublisher, times(1)).publish(LikeEvent.builder().idPost(1L).idAuthor(2L).idUser(2L).dateTime(any()).build());

        assertNotNull(postDto);
    }
}