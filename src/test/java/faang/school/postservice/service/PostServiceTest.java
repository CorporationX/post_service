package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostDTO;
import faang.school.postservice.exception.DataAlreadyDeletedException;
import faang.school.postservice.exception.DataAlreadyExistException;
import faang.school.postservice.exception.UnpublishedPostException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.adapter.PostRepositoryAdapter;
import faang.school.postservice.validator.PostValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostValidator postValidator;

    @Mock
    private PostMapper postMapper;

    @Mock
    private PostRepositoryAdapter postRepositoryAdapter;

    private final Post post = new Post();
    private PostDTO postDTO = new PostDTO();


    @BeforeEach
    void setUp() {
        postDTO = PostDTO.builder()
                .id(1L)
                .content("some content")
                .authorId(2L)
                .build();
    }

    @Test
    @DisplayName("Test successful create draft")
    void testCreatingDraft() {
        Mockito.when(postMapper.toEntity(postDTO)).thenReturn(post);
        Mockito.when(postRepositoryAdapter.save(post)).thenReturn(post);
        Mockito.when(postMapper.toDto(post)).thenReturn(postDTO);

        PostDTO result = postService.createDraft(postDTO);

        Mockito.verify(postValidator, Mockito.times(1)).validatedOwnerPost(postDTO);

        Mockito.verify(postMapper, Mockito.times(1)).toEntity(postDTO);
        Mockito.verify(postRepositoryAdapter, Mockito.times(1)).save(post);
        Mockito.verify(postMapper, Mockito.times(1)).toDto(post);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result, postDTO);
    }

    @Test
    @DisplayName("Test must return exception, when the post is already published")
    void testCreateAlreadyPublishedPost() {
        post.setPublished(true);

        Mockito.when(postValidator.findPostWithId(1L)).thenReturn(post);

        Assertions.assertThrows(DataAlreadyExistException.class, () -> postService.publishPost(1L));

        Mockito.verify(postValidator, Mockito.times(1)).findPostWithId(1L);
    }

    @Test
    @DisplayName("Test must return exception, when the post is scheduled for publication")
    void testCreateScheduledForPublicationPost() {
        post.setScheduledAt(LocalDateTime.now().plusSeconds(1));

        Mockito.when(postValidator.findPostWithId(2L)).thenReturn(post);

        Assertions.assertThrows(DataAlreadyExistException.class, () -> postService.publishPost(2L));

        Mockito.verify(postValidator, Mockito.times(1)).findPostWithId(2L);
    }

    @Test
    @DisplayName("Test must return exception, when post is already deleted")
    void testCreateAlreadyDeletedPost() {
        post.setDeleted(true);

        Mockito.when(postValidator.findPostWithId(3L)).thenReturn(post);

        Assertions.assertThrows(DataAlreadyDeletedException.class, () -> postService.publishPost(3L));

        Mockito.verify(postValidator, Mockito.times(1)).findPostWithId(3L);
    }

    @Test
    @DisplayName("Test successful publication of a post")
    void testPublishingPost() {
        Mockito.when(postValidator.findPostWithId(1L)).thenReturn(post);
        Mockito.when(postMapper.toDto(post)).thenReturn(postDTO);

        PostDTO result = postService.publishPost(1L);

        Assertions.assertEquals(postDTO, result);

        Mockito.verify(postValidator, Mockito.times(1)).findPostWithId(1L);
        Mockito.verify(postMapper, Mockito.times(1)).toDto(post);
    }

    @Test
    @DisplayName("Test successful update post")
    void testUpdatingPost() {
        Mockito.when(postValidator.findPostWithId(1L)).thenReturn(post);
        Mockito.when(postMapper.toDto(post)).thenReturn(postDTO);

        PostDTO result = postService.updatePost(postDTO);

        Mockito.verify(postValidator, Mockito.times(1))
                .validateAuthorForUpdate(post, postDTO);

        Assertions.assertEquals(postDTO, result);

        Mockito.verify(postValidator, Mockito.times(1)).findPostWithId(1L);
        Mockito.verify(postMapper, Mockito.times(1)).toDto(post);
    }

    @Test
    @DisplayName("Test must return exception, when post is already deleted")
    void deletingAnAlreadyDeletedPost() {
        post.setDeleted(true);

        Mockito.when(postValidator.findPostWithId(1L)).thenReturn(post);

        Assertions.assertThrows(DataAlreadyDeletedException.class, () -> postService.deletePost(1L));

        Mockito.verify(postValidator, Mockito.times(1)).findPostWithId(1L);
    }

    @Test
    @DisplayName("Test successful delete post")
    void deletingPost() {
        postDTO = PostDTO.builder()
                .deleted(true)
                .build();

        Mockito.when(postValidator.findPostWithId(1L)).thenReturn(post);
        Mockito.when(postMapper.toDto(post)).thenReturn(postDTO);

        PostDTO result = postService.deletePost(1L);

        Assertions.assertEquals(postDTO, result);

        Mockito.verify(postValidator, Mockito.times(1)).findPostWithId(1L);
        Mockito.verify(postMapper, Mockito.times(1)).toDto(post);
    }

    @Test
    @DisplayName("Test return exception when post was already deleted")
    void getAlreadyDeletedPost() {
        post.setDeleted(true);

        Mockito.when(postValidator.findPostWithId(1L)).thenReturn(post);

        Assertions.assertThrows(DataAlreadyDeletedException.class, () -> postService.getPostById(1L));

        Mockito.verify(postValidator, Mockito.times(1)).findPostWithId(1L);
    }

    @Test
    @DisplayName("Test return exception when post unpublished yet")
    void testUnpublishedPost() {
        post.setScheduledAt(LocalDateTime.now().plusMinutes(10));

        Mockito.when(postValidator.findPostWithId(1L)).thenReturn(post);

        Assertions.assertThrows(UnpublishedPostException.class, () -> postService.getPostById(1L));

        Mockito.verify(postValidator, Mockito.times(1)).findPostWithId(1L);
    }

    @Test
    @DisplayName("Test successful getting post")
    void testGettingPostWithId() {
        Mockito.when(postValidator.findPostWithId(1L)).thenReturn(post);
        Mockito.when(postMapper.toDto(post)).thenReturn(postDTO);

        PostDTO result = postService.getPostById(1L);

        Assertions.assertEquals(postDTO, result);

        Mockito.verify(postValidator, Mockito.times(1)).findPostWithId(1L);
        Mockito.verify(postMapper, Mockito.times(1)).toDto(post);
    }

    @Test
    @DisplayName("Test successful getting all drafts")
    void testGettingAllDrafts() {
        Post draft1 = new Post();
        draft1.setCreatedAt(LocalDateTime.of(2024, 10, 10, 10, 30));

        Post draft2 = new Post();
        draft2.setCreatedAt(LocalDateTime.of(2024, 10, 20, 10, 30));

        Post publishedPost = new Post();
        publishedPost.setPublished(true);

        Post deletedPost = new Post();
        deletedPost.setDeleted(true);


        List<Post> allPosts = List.of(draft1, draft2, publishedPost, deletedPost);

        Mockito.when(postRepositoryAdapter.findByAuthorId(1L)).thenReturn(allPosts);
        Mockito.when(postMapper.toDto(Mockito.any(Post.class))).thenAnswer(invocation -> {
            Post currentPost = invocation.getArgument(0);
            return PostDTO.builder()
                    .createdAt(currentPost.getCreatedAt())
                    .build();
        });

        List<PostDTO> result = postService.getAllDraftsByAuthorId(1L);

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(LocalDateTime.of(2024, 10, 20, 10, 30),
                result.get(0).createdAt());
        Assertions.assertEquals(LocalDateTime.of(2024, 10, 10, 10, 30),
                result.get(1).createdAt());

        Mockito.verify(postValidator, Mockito.times(1)).userOwnerOfThePost(1L);
        Mockito.verify(postMapper, Mockito.times(2)).toDto(Mockito.any(Post.class));
    }

    @Test
    @DisplayName("Test successful getting all posts")
    void testGettingAllPosts() {
        Post unpublishedPost = new Post();

        Post deletedPost = new Post();
        deletedPost.setDeleted(true);

        Post publishedPost1 = new Post();
        publishedPost1.setPublished(true);
        publishedPost1.setCreatedAt(LocalDateTime.of(2024, 10, 10, 10, 30));

        Post publishedPost2 = new Post();
        publishedPost2.setPublished(true);
        publishedPost2.setCreatedAt(LocalDateTime.of(2024, 10, 20, 10, 30));

        List<Post> allPosts = List.of(unpublishedPost, deletedPost, publishedPost1, publishedPost2);

        Mockito.when(postRepositoryAdapter.findByAuthorId(1L)).thenReturn(allPosts);
        Mockito.when(postMapper.toDto(Mockito.any(Post.class))).thenAnswer(invocation -> {
            Post currentPost = invocation.getArgument(0);
            return PostDTO.builder()
                    .createdAt(currentPost.getCreatedAt())
                    .build();
        });

        List<PostDTO> result = postService.getAllPostsByAuthorId(1L);

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(LocalDateTime.of(2024, 10, 20, 10, 30),
                result.get(0).createdAt());
        Assertions.assertEquals(LocalDateTime.of(2024, 10, 10, 10, 30),
                result.get(1).createdAt());

        Mockito.verify(postValidator, Mockito.times(1)).userOwnerOfThePost(1L);
        Mockito.verify(postMapper, Mockito.times(2)).toDto(Mockito.any(Post.class));
    }
}