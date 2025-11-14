package faang.school.postservice.service.post;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.project.ProjectDto;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.util.client.ProjectServiceClientAdapter;
import faang.school.postservice.util.client.UserServiceClientAdapter;
import faang.school.postservice.util.post.PostRepositoryAdapter;
import faang.school.postservice.util.post.PostValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Spy
    private PostMapperImpl postMapper;
    @Mock
    private PostRepository postRepository;
    @Mock
    private PostValidator postValidator;
    @Mock
    private UserContext userContext;
    @Mock
    private PostRepositoryAdapter postRepositoryAdapter;
    @Mock
    private UserServiceClientAdapter userServiceClientAdapter;
    @Mock
    private ProjectServiceClientAdapter projectServiceClientAdapter;
    @Captor
    private ArgumentCaptor<Post> postArgumentCaptor;
    @InjectMocks
    private PostServiceImpl postService;

    @Test
    public void testCreateDraftAuthorIdSuccessful() {
        final long currentUserId = 1L;
        final PostDto postDto = new PostDto(
                null,
                "content",
                currentUserId,
                null,
                false,
                null,
                false
        );

        when(userContext.getUserId()).thenReturn(currentUserId);
        doNothing().when(userServiceClientAdapter).getUserById(currentUserId);
        doNothing().when(postValidator).validateUser(eq(currentUserId), any(PostDto.class));
        doNothing().when(postValidator).validatePostIsPublished(any(Post.class));
        doNothing().when(postValidator).validatePostIsDeleted(any(Post.class));
        when(postRepository.save(postArgumentCaptor.capture()))
                .thenAnswer(invocation -> {
                    Post post = postArgumentCaptor.getValue();
                    post.setId(3L);
                    return post;
                });

        PostDto actualPostDto = postService.createDraft(postDto);

        assertNotNull(actualPostDto);
        assertEquals(3L, actualPostDto.id());
        assertEquals("content", actualPostDto.content());
        assertEquals(1L, actualPostDto.authorId());

        verify(userContext, times(1)). getUserId();
        verify(userServiceClientAdapter, times(1)).getUserById(currentUserId);
        verify(postValidator, times(1)).validateUser(eq(currentUserId), any(PostDto.class));
        verify(projectServiceClientAdapter, never()).getProjectById(anyLong());
        verify(postValidator, never()).validateProject(anyLong(), anyLong(), anyLong());
        verify(postValidator, times(1)).validatePostIsPublished(any(Post.class));
        verify(postValidator, times(1)).validatePostIsDeleted(any(Post.class));
        verify(postRepository, times(1)).save(postArgumentCaptor.capture());
    }

    @Test
    public void testCreateDraftOwnerIdSuccessful() {
        final long currentUserId = 1L;
        final long projectId = 2L;
        final PostDto postDto = new PostDto(
                null,
                "content",
                null,
                projectId,
                false,
                null,
                false
        );
        final long ownerId = currentUserId;
        final ProjectDto projectDto = new ProjectDto(projectId, null, ownerId);

        when(userContext.getUserId()).thenReturn(currentUserId);
        doNothing().when(userServiceClientAdapter).getUserById(currentUserId);
        when(projectServiceClientAdapter.getProjectById(projectId)).thenReturn(projectDto);
        doNothing().when(postValidator).validateProject(ownerId, currentUserId, projectId);
        doNothing().when(postValidator).validatePostIsPublished(any(Post.class));
        doNothing().when(postValidator).validatePostIsDeleted(any(Post.class));
        when(postRepository.save(postArgumentCaptor.capture()))
                .thenAnswer(invocation -> {
                    Post post = postArgumentCaptor.getValue();
                    post.setId(3L);
                    return post;
                });

        PostDto actualPostDto = postService.createDraft(postDto);

        assertNotNull(actualPostDto);
        assertEquals(3L, actualPostDto.id());
        assertEquals("content", actualPostDto.content());
        assertEquals(2L, actualPostDto.projectId());

        verify(userContext, times(1)). getUserId();
        verify(userServiceClientAdapter, times(1)).getUserById(currentUserId);
        verify(postValidator, never()).validateUser(anyLong(), any(PostDto.class));
        verify(projectServiceClientAdapter, times(1)).getProjectById(projectId);
        verify(postValidator, times(1)).validateProject(ownerId, currentUserId, projectId);
        verify(postValidator, times(1)).validatePostIsPublished(any(Post.class));
        verify(postValidator, times(1)).validatePostIsDeleted(any(Post.class));
        verify(postRepository, times(1)).save(postArgumentCaptor.capture());
    }

    @Test
    public void testPublishPostAuthorIdSuccessful() {
        final long postId = 5L;
        final long currentUserId = 1L;
        final Post post = new Post();
        post.setId(postId);
        post.setContent("content");
        post.setAuthorId(currentUserId);

        when(postRepositoryAdapter.getPostById(postId)).thenReturn(post);
        when(userContext.getUserId()).thenReturn(currentUserId);
        doNothing().when(userServiceClientAdapter).getUserById(currentUserId);
        doNothing().when(postValidator).validateUser(eq(currentUserId), any(PostDto.class));
        doNothing().when(postValidator).validatePostIsPublished(post);
        doNothing().when(postValidator).validatePostIsDeleted(post);
        when(postRepository.save(postArgumentCaptor.capture()))
                .thenAnswer(invocation -> postArgumentCaptor.getValue());

        PostDto actualPostDto = postService.publishPost(postId);

        assertNotNull(actualPostDto);
        assertEquals(postId, actualPostDto.id());
        assertEquals("content", actualPostDto.content());
        assertEquals(currentUserId, actualPostDto.authorId());
        assertTrue(actualPostDto.published());
        assertNotNull(actualPostDto.publishedAt());

        verify(postRepositoryAdapter, times(1)).getPostById(postId);
        verify(userContext, times(1)).getUserId();
        verify(userServiceClientAdapter, times(1)).getUserById(currentUserId);
        verify(postValidator, times(1)).validateUser(eq(currentUserId), any(PostDto.class));
        verify(projectServiceClientAdapter, never()).getProjectById(anyLong());
        verify(postValidator, never()).validateProject(anyLong(), anyLong(), anyLong());
        verify(postValidator, times(1)).validatePostIsPublished(post);
        verify(postValidator, times(1)).validatePostIsDeleted(post);
        verify(postRepository, times(1)).save(postArgumentCaptor.capture());
    }

    @Test
    public void testPublishPostOwnerIdSuccessful() {
        final long postId = 5L;
        final long currentUserId = 1L;
        final long projectId = 2L;
        final long ownerId = currentUserId;
        final ProjectDto projectDto = new ProjectDto(projectId, null, ownerId);
        final Post post = new Post();
        post.setId(postId);
        post.setContent("content");
        post.setProjectId(projectId);

        when(postRepositoryAdapter.getPostById(postId)).thenReturn(post);
        when(userContext.getUserId()).thenReturn(currentUserId);
        doNothing().when(userServiceClientAdapter).getUserById(currentUserId);
        when(projectServiceClientAdapter.getProjectById(projectId)).thenReturn(projectDto);
        doNothing().when(postValidator).validateProject(ownerId, currentUserId, projectId);
        doNothing().when(postValidator).validatePostIsPublished(post);
        doNothing().when(postValidator).validatePostIsDeleted(post);
        when(postRepository.save(postArgumentCaptor.capture()))
                .thenAnswer(invocation -> postArgumentCaptor.getValue());

        PostDto actualPostDto = postService.publishPost(postId);

        assertNotNull(actualPostDto);
        assertEquals(postId, actualPostDto.id());
        assertEquals("content", actualPostDto.content());
        assertEquals(projectId, actualPostDto.projectId());
        assertTrue(actualPostDto.published());
        assertNotNull(actualPostDto.publishedAt());

        verify(postRepositoryAdapter, times(1)).getPostById(postId);
        verify(userContext, times(1)).getUserId();
        verify(userServiceClientAdapter, times(1)).getUserById(currentUserId);
        verify(postValidator, never()).validateUser(anyLong(), any(PostDto.class));
        verify(projectServiceClientAdapter, times(1)).getProjectById(projectId);
        verify(postValidator, times(1)).validateProject(ownerId, currentUserId, projectId);
        verify(postValidator, times(1)).validatePostIsPublished(post);
        verify(postValidator, times(1)).validatePostIsDeleted(post);
        verify(postRepository, times(1)).save(postArgumentCaptor.capture());
    }

    @Test
    public void testUpdatePostAuthorIdSuccessful() {
        final long postId = 5L;
        final long currentUserId = 1L;
        final PostDto postDto = new PostDto(
                postId,
                "new content",
                currentUserId,
                null,
                false,
                null,
                false
        );
        final Post post = new Post();
        post.setId(postId);
        post.setContent("old content");
        post.setAuthorId(currentUserId);

        when(userContext.getUserId()).thenReturn(currentUserId);
        doNothing().when(userServiceClientAdapter).getUserById(currentUserId);
        doNothing().when(postValidator).validateUser(currentUserId, postDto);
        when(postRepositoryAdapter.getPostById(postId)).thenReturn(post);
        doNothing().when(postValidator).validatePostIsDeleted(post);
        doNothing().when(postValidator).validateChangeAuthor(post, postDto);

        PostDto actualPostDto = postService.updatePost(postId, postDto);

        assertNotNull(actualPostDto);
        assertEquals(postDto.id(), actualPostDto.id());
        assertEquals(postDto.content(), actualPostDto.content());
        assertEquals(postDto.authorId(), actualPostDto.authorId());

        verify(userContext, times(1)).getUserId();
        verify(userServiceClientAdapter, times(1)).getUserById(currentUserId);
        verify(postValidator, times(1)).validateUser(currentUserId, postDto);
        verify(projectServiceClientAdapter, never()).getProjectById(anyLong());
        verify(postValidator, never()).validateProject(anyLong(), anyLong(), anyLong());
        verify(postRepositoryAdapter, times(1)).getPostById(postId);
        verify(postValidator, times(1)).validatePostIsDeleted(post);
        verify(postValidator, times(1)).validateChangeAuthor(post, postDto);
        verify(postMapper, times(1)).updatePost(post, postDto);
    }

    @Test
    public void testUpdatePostOwnerIdSuccessful() {
        final long postId = 5L;
        final long currentUserId = 1L;
        final long projectId = 2L;
        final long ownerId = currentUserId;
        final PostDto postDto = new PostDto(
                postId,
                "new content",
                null,
                projectId,
                false,
                null,
                false
        );
        final ProjectDto projectDto = new ProjectDto(projectId, null, ownerId);
        final Post post = new Post();
        post.setId(postId);
        post.setContent("old content");
        post.setProjectId(projectId);

        when(userContext.getUserId()).thenReturn(currentUserId);
        doNothing().when(userServiceClientAdapter).getUserById(currentUserId);
        when(projectServiceClientAdapter.getProjectById(projectId)).thenReturn(projectDto);
        doNothing().when(postValidator).validateProject(ownerId, currentUserId, projectId);
        when(postRepositoryAdapter.getPostById(postId)).thenReturn(post);
        doNothing().when(postValidator).validatePostIsDeleted(post);
        doNothing().when(postValidator).validateChangeAuthor(post, postDto);

        PostDto actualPostDto = postService.updatePost(postId, postDto);

        assertNotNull(actualPostDto);
        assertEquals(postDto.id(), actualPostDto.id());
        assertEquals(postDto.content(), actualPostDto.content());
        assertEquals(postDto.projectId(), actualPostDto.projectId());

        verify(userContext, times(1)).getUserId();
        verify(userServiceClientAdapter, times(1)).getUserById(currentUserId);
        verify(postValidator, never()).validateUser(anyLong(), any(PostDto.class));
        verify(projectServiceClientAdapter, times(1)).getProjectById(projectId);
        verify(postValidator, times(1)).validateProject(ownerId, currentUserId, projectId);
        verify(postRepositoryAdapter, times(1)).getPostById(postId);
        verify(postValidator, times(1)).validatePostIsDeleted(post);
        verify(postValidator, times(1)).validateChangeAuthor(post, postDto);
        verify(postMapper, times(1)).updatePost(post, postDto);
    }

    @Test
    public void testDeletePostAuthorIdSuccessful() {
        final long postId = 5L;
        final long currentUserId = 1L;
        final Post post = new Post();
        post.setId(postId);
        post.setContent("content");
        post.setAuthorId(currentUserId);

        when(postRepositoryAdapter.getPostById(postId)).thenReturn(post);
        when(userContext.getUserId()).thenReturn(currentUserId);
        doNothing().when(userServiceClientAdapter).getUserById(currentUserId);
        doNothing().when(postValidator).validateUser(eq(currentUserId), any(PostDto.class));
        when(postRepository.save(postArgumentCaptor.capture()))
                .thenAnswer(invocation -> postArgumentCaptor.getValue());

        PostDto actualPostDto = postService.deletePost(postId);

        assertNotNull(actualPostDto);
        assertEquals(postId, actualPostDto.id());
        assertEquals("content", actualPostDto.content());
        assertEquals(currentUserId, actualPostDto.authorId());
        assertTrue(actualPostDto.deleted());

        verify(postRepositoryAdapter, times(1)).getPostById(postId);
        verify(userContext, times(1)).getUserId();
        verify(userServiceClientAdapter, times(1)).getUserById(currentUserId);
        verify(postValidator, times(1)).validateUser(eq(currentUserId), any(PostDto.class));
        verify(projectServiceClientAdapter, never()).getProjectById(anyLong());
        verify(postValidator, never()).validateProject(anyLong(), anyLong(), anyLong());
        verify(postRepository, times(1)).save(postArgumentCaptor.capture());
    }

    @Test
    public void testDeletePostOwnerIdSuccessful() {
        final long postId = 5L;
        final long currentUserId = 1L;
        final long projectId = 2L;
        final long ownerId = currentUserId;
        final ProjectDto projectDto = new ProjectDto(projectId, null, ownerId);
        final Post post = new Post();
        post.setId(postId);
        post.setContent("content");
        post.setProjectId(projectId);

        when(postRepositoryAdapter.getPostById(postId)).thenReturn(post);
        when(userContext.getUserId()).thenReturn(currentUserId);
        doNothing().when(userServiceClientAdapter).getUserById(currentUserId);
        when(projectServiceClientAdapter.getProjectById(projectId)).thenReturn(projectDto);
        doNothing().when(postValidator).validateProject(ownerId, currentUserId, projectId);
        when(postRepository.save(postArgumentCaptor.capture()))
                .thenAnswer(invocation -> postArgumentCaptor.getValue());

        PostDto actualPostDto = postService.deletePost(postId);

        assertNotNull(actualPostDto);
        assertEquals(postId, actualPostDto.id());
        assertEquals("content", actualPostDto.content());
        assertEquals(projectId, actualPostDto.projectId());
        assertTrue(actualPostDto.deleted());

        verify(postRepositoryAdapter, times(1)).getPostById(postId);
        verify(userContext, times(1)).getUserId();
        verify(userServiceClientAdapter, times(1)).getUserById(currentUserId);
        verify(postValidator, never()).validateUser(anyLong(), any(PostDto.class));
        verify(projectServiceClientAdapter, times(1)).getProjectById(projectId);
        verify(postValidator, times(1)).validateProject(ownerId, currentUserId, projectId);
        verify(postRepository, times(1)).save(postArgumentCaptor.capture());
    }

    @Test
    public void testFindPostIsSuccessful() {
        final long postId = 5L;
        final Post post = new Post();
        post.setId(postId);
        post.setContent("content");
        post.setAuthorId(1L);

        when(postRepositoryAdapter.getPostById(postId)).thenReturn(post);
        doNothing().when(postValidator).validatePostIsUnpublished(post);
        doNothing().when(postValidator).validatePostIsDeleted(post);

        PostDto postDto = postService.findPostById(postId);

        assertNotNull(postDto);
        assertEquals(postId, postDto.id());
        assertEquals("content", postDto.content());
        assertEquals(1L, postDto.authorId());

        verify(postRepositoryAdapter, times(1)).getPostById(postId);
        verify(postValidator, times(1)).validatePostIsUnpublished(post);
        verify(postValidator, times(1)).validatePostIsDeleted(post);
    }

    @Test
    public void testFindDraftsByAuthorId() {
        final long authorId = 1L;
        final long expectedSize = 2L;

        when(postRepository.findByPublishedFalseAndAuthorIdAndDeletedFalseOrderByCreatedAtDesc(authorId))
                .thenReturn(List.of(new Post(), new Post()));

        List<PostDto> postDtos = postService.findDraftsByAuthorId(authorId);

        assertNotNull(postDtos);
        assertEquals(expectedSize, postDtos.size());

        verify(postRepository, times(1))
                .findByPublishedFalseAndAuthorIdAndDeletedFalseOrderByCreatedAtDesc(authorId);
    }

    @Test
    public void testFindDraftsByProjectId() {
        final long projectId = 2L;
        final long expectedSize = 2L;

        when(postRepository.findByPublishedFalseAndProjectIdAndDeletedFalseOrderByCreatedAtDesc(projectId))
                .thenReturn(List.of(new Post(), new Post()));

        List<PostDto> postDtos = postService.findDraftsByProjectId(projectId);

        assertNotNull(postDtos);
        assertEquals(expectedSize, postDtos.size());

        verify(postRepository, times(1))
                .findByPublishedFalseAndProjectIdAndDeletedFalseOrderByCreatedAtDesc(projectId);
    }

    @Test
    public void testFindPostsByAuthorId() {
        final long authorId = 1L;
        final long expectedSize = 2L;

        when(postRepository.findByPublishedTrueAndAuthorIdAndDeletedFalseOrderByPublishedAtDesc(authorId))
                .thenReturn(List.of(new Post(), new Post()));

        List<PostDto> postDtos = postService.findPostsByAuthorId(authorId);

        assertNotNull(postDtos);
        assertEquals(expectedSize, postDtos.size());

        verify(postRepository, times(1))
                .findByPublishedTrueAndAuthorIdAndDeletedFalseOrderByPublishedAtDesc(authorId);
    }

    @Test
    public void testFindPostsByProjectId() {
        final long projectId = 2L;
        final long expectedSize = 2L;

        when(postRepository.findByPublishedTrueAndProjectIdAndDeletedFalseOrderByPublishedAtDesc(projectId))
                .thenReturn(List.of(new Post(), new Post()));

        List<PostDto> postDtos = postService.findPostsByProjectId(projectId);

        assertNotNull(postDtos);
        assertEquals(expectedSize, postDtos.size());

        verify(postRepository, times(1))
                .findByPublishedTrueAndProjectIdAndDeletedFalseOrderByPublishedAtDesc(projectId);
    }
}
