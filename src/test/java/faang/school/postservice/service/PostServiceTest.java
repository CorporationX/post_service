package faang.school.postservice.service;

import faang.school.postservice.dto.posts.PostCreatingRequest;
import faang.school.postservice.dto.posts.PostResultResponse;
import faang.school.postservice.dto.posts.PostUpdatingDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.cache.PostAuthorCacheService;
import faang.school.postservice.utils.PostUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    private static PostCreatingRequest postCreatingRequest;
    private static PostResultResponse postResultResponse;
    private static Post post;
    @InjectMocks
    private PostService postService;
    @Mock
    private PostRepository postRepository;
    @Mock
    private PostUtil postUtil;
    @Mock
    private RewriterService rewriterService;
    @Mock
    private ExecutorService scheduledPublishPostThreadPool;
    @Mock
    private PostAuthorCacheService postAuthorCacheService;
    @Spy
    private PostMapper postMapper = Mappers.getMapper(PostMapper.class);

    @BeforeEach
    public void setUp() {
        postCreatingRequest = PostCreatingRequest.builder()
                .content("This is a test content")
                .authorId(1L)
                .projectId(null)
                .build();

        postResultResponse = PostResultResponse.builder()
                .id(postCreatingRequest.authorId())
                .build();

        post = Post.builder()
                .id(1L)
                .content("HeLlO_W0o0oo0orlxD!")
                .authorId(postCreatingRequest.authorId())
                .published(false)
                .deleted(false)
                .build();
    }

    @Test
    public void createPost_PostWasCreatedSuccessfully() {
        Mockito.when(postUtil.validateCreator(postCreatingRequest.authorId(), postCreatingRequest.projectId()))
                .thenReturn(0);
        Mockito.when(postRepository.save(any(Post.class)))
                .thenReturn(post);

        PostResultResponse result = postService.createPost(postCreatingRequest);
        Assertions.assertNotNull(result);
        assertEquals(result, postResultResponse);
        verify(postUtil, times(1)).validateCreator(postCreatingRequest.authorId(), postCreatingRequest.projectId());
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    public void publishPost_PostSuccessfullyPublished() {
        when(postRepository.findById(post.getId())).thenReturn(Optional.of(post));

        PostResultResponse result = postService.publishPost(post.getId());

        Assertions.assertTrue(post.isPublished(), "Post should be published");
        Assertions.assertNotNull(post.getPublishedAt(), "PublishedAt should be set");

        assertEquals(postResultResponse, result);
        verify(postAuthorCacheService, timeout(1000).times(1)).cachePostAuthor(post.getAuthorId());
    }

    @Test
    public void publishPost_PostAlreadyPublished() {
        Post setPost = Post.builder()
                .id(1L)
                .content("HeLlO_W0o0oo0orlxD!")
                .authorId(postCreatingRequest.authorId())
                .published(true)
                .deleted(false)
                .build();

        setPost.setPublished(true);

        when(postRepository.findById(setPost.getId())).thenReturn(Optional.of(setPost));

        Assertions.assertThrows(IllegalArgumentException.class, () -> postService.publishPost(setPost.getId()));
    }

    @Test
    public void updatePost_PostWasUpdatedSuccessfully() {
        Post setPost = Post.builder()
                .id(1L)
                .content("HeLlO_W0o0oo0orlxD!")
                .authorId(postCreatingRequest.authorId())
                .published(true)
                .deleted(false)
                .build();

        PostUpdatingDto postUpdatingDto = PostUpdatingDto.builder()
                .postId(1L)
                .updatingContent("New content")
                .build();

        Long postId = setPost.getId();
        String postContent = setPost.getContent() + "New content";

        when(postRepository.findById(postId)).thenReturn(Optional.ofNullable(setPost));


        PostResultResponse result = postService.updatePost(postUpdatingDto);

        assertEquals(result, postResultResponse);
    }

    @Test
    public void updatePost_PostWasDeleted() {
        Post setPost = Post.builder()
                .id(1L)
                .content("HeLlO_W0o0oo0orlxD!")
                .authorId(postCreatingRequest.authorId())
                .published(true)
                .deleted(true)
                .build();

        updatePostWithError(setPost);
    }

    @Test
    public void updatePost_PostWasNotPublishedYet() {
        Post setPost = Post.builder()
                .id(1L)
                .content("HeLlO_W0o0oo0orlxD!")
                .authorId(postCreatingRequest.authorId())
                .published(false)
                .deleted(false)
                .build();

        updatePostWithError(setPost);
    }

    @Test
    public void softDeletePost_PostWasSoftDeletedSuccessfully() {
        Long postId = post.getId();

        Mockito.when(postRepository.findById(postId))
                .thenReturn(Optional.of(post));

        PostResultResponse result = postService.softDelete(postId);

        assertEquals(result, postResultResponse);
    }

    @Test
    public void softDeletePost_ThrowsExceptionWhenAlreadyDeleted() {
        Long postId = post.getId();

        post.setDeleted(true);

        Mockito.when(postRepository.findById(postId))
                .thenReturn(Optional.of(post));

        Assertions.assertThrows(IllegalArgumentException.class, () -> postService.softDelete(postId));
    }

    private void updatePostWithError(Post setPost) {
        PostUpdatingDto postUpdatingDto = PostUpdatingDto.builder()
                .postId(1L)
                .updatingContent("New content")
                .build();

        Long postId = setPost.getId();
        String postContent = setPost.getContent() + "New content";

        when(postRepository.findById(postId)).thenReturn(Optional.of(setPost));

        Assertions.assertThrows(IllegalArgumentException.class, () -> postService.updatePost(postUpdatingDto));
    }

    @Test
    public void publishScheduledPost_success6ThreadsRun() {
        List<Post> posts = new ArrayList<>();

        for (int i = 1; i < 5050; i++) {
            posts.add(Post.builder()
                    .id((long) i)
                    .published(false)
                    .build());
        }

        when(postRepository.findReadyToPublish()).thenReturn(posts);

        postService.publishScheduledPost();

        verify(scheduledPublishPostThreadPool, times(6)).submit(any(Runnable.class));
    }

    @Test
    public void publishScheduledPost_success1ThreadsRun() {
        List<Post> posts = new ArrayList<>();

        for (int i = 1; i < 500; i++) {
            posts.add(Post.builder()
                    .id((long) i)
                    .published(false)
                    .build());
        }

        when(postRepository.findReadyToPublish()).thenReturn(posts);

        postService.publishScheduledPost();

        verify(scheduledPublishPostThreadPool, times(1)).submit(any(Runnable.class));
    }

    @Test
    public void publishScheduledPost_zeroPostsToPublish() {
        when(postRepository.findReadyToPublish()).thenReturn(Collections.emptyList());

        postService.publishScheduledPost();

        verify(scheduledPublishPostThreadPool, times(0)).submit(any(Runnable.class));
    }

    @Test
    public void processRewritePost_Success() {
        String postContent = "This is a test content";
        String rewrittenContent = "rewritten content";

        Post post = new Post();
        post.setId(1L);
        post.setContent(postContent);

        when(rewriterService.rewriteText(postContent)).thenReturn(rewrittenContent);

        postService.processRewritePost(post);

        assertEquals(rewrittenContent, post.getContent());
        verify(postRepository).save(post);
    }

    @Test
    void testPostCorrections() {
        Post post1 = new Post();
        post1.setId(1L);
        post1.setContent("original content 1");

        Post post2 = new Post();
        post2.setId(2L);
        post2.setContent("original content 2");

        List<Post> posts = Arrays.asList(post1, post2);

        when(postRepository.findReadyToPublish()).thenReturn(posts);

        when(rewriterService.rewriteText("original content 1"))
                .thenReturn("rewritten content 1");
        when(rewriterService.rewriteText("original content 2"))
                .thenReturn("rewritten content 2");

        postService.postCorrections();

        verify(postRepository,
                org.mockito.Mockito.timeout(2000).times(1)).save(post1);
        verify(postRepository,
                org.mockito.Mockito.timeout(2000).times(1)).save(post2);

        assertEquals("rewritten content 1", post1.getContent());
        assertEquals("rewritten content 2", post2.getContent());
    }
}
