package faang.school.postservice.service;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataAlreadyDeletedException;
import faang.school.postservice.exception.DataAlreadyExistException;
import faang.school.postservice.exception.UnpublishedPostException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
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

    @Mock
    private PostRepository postRepository;

    private final Post post = new Post();
    private PostDto postDTO = new PostDto();

    @BeforeEach
    void setUp() {
        postDTO = PostDto.builder()
                .id(1L)
                .content("some content")
                .authorId(2L)
                .build();
    }

    @Test
    @DisplayName("Test successful create draft")
    void testCreatingDraft() {
        Mockito.when(postMapper.toEntity(postDTO)).thenReturn(post);
        Mockito.when(postRepository.save(post)).thenReturn(post);
        Mockito.when(postMapper.toDto(post)).thenReturn(postDTO);

        PostDto result = postService.createDraft(postDTO);

        Mockito.verify(postValidator, Mockito.times(1)).validatedOwnerPost(postDTO);

        Mockito.verify(postMapper, Mockito.times(1)).toEntity(postDTO);
        Mockito.verify(postRepository, Mockito.times(1)).save(post);
        Mockito.verify(postMapper, Mockito.times(1)).toDto(post);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result, postDTO);
    }

    @Test
    @DisplayName("Test must return exception, when the post is already published")
    void testCreateAlreadyPublishedPost() {
        post.setPublished(true);

        Mockito.when(postRepositoryAdapter.getByIdWithLikes(1L)).thenReturn(post);

        Assertions.assertThrows(DataAlreadyExistException.class, () -> postService.publishPost(1L));

        Mockito.verify(postRepositoryAdapter, Mockito.times(1)).getByIdWithLikes(1L);
    }

    @Test
    @DisplayName("Test must return exception, when the post is scheduled for publication")
    void testCreateScheduledForPublicationPost() {
        post.setScheduledAt(LocalDateTime.now().plusSeconds(1));

        Mockito.when(postRepositoryAdapter.getByIdWithLikes(2L)).thenReturn(post);

        Assertions.assertThrows(DataAlreadyExistException.class, () -> postService.publishPost(2L));

        Mockito.verify(postRepositoryAdapter, Mockito.times(1)).getByIdWithLikes(2L);
    }

    @Test
    @DisplayName("Test must return exception, when post is already deleted")
    void testCreateAlreadyDeletedPost() {
        post.setDeleted(true);

        Mockito.when(postRepositoryAdapter.getByIdWithLikes(3L)).thenReturn(post);

        Assertions.assertThrows(DataAlreadyDeletedException.class, () -> postService.publishPost(3L));

        Mockito.verify(postRepositoryAdapter, Mockito.times(1)).getByIdWithLikes(3L);
    }

    @Test
    @DisplayName("Test successful publication of a post")
    void testPublishingPost() {
        Mockito.when(postRepositoryAdapter.getByIdWithLikes(1L)).thenReturn(post);
        Mockito.when(postMapper.toDto(post)).thenReturn(postDTO);

        PostDto result = postService.publishPost(1L);

        Assertions.assertEquals(postDTO, result);

        Mockito.verify(postRepositoryAdapter, Mockito.times(1)).getByIdWithLikes(1L);
        Mockito.verify(postMapper, Mockito.times(1)).toDto(post);
    }

    @Test
    @DisplayName("Test successful update post")
    void testUpdatingPost() {
        Mockito.when(postRepositoryAdapter.getByIdWithLikes(1L)).thenReturn(post);
        Mockito.when(postMapper.toDto(post)).thenReturn(postDTO);

        PostDto result = postService.updatePost(postDTO);

        Mockito.verify(postValidator, Mockito.times(1))
                .validateAuthorForUpdate(post, postDTO);

        Assertions.assertEquals(postDTO, result);

        Mockito.verify(postRepositoryAdapter, Mockito.times(1)).getByIdWithLikes(1L);
        Mockito.verify(postMapper, Mockito.times(1)).toDto(post);
    }

    @Test
    @DisplayName("Test must return exception, when post is already deleted")
    void deletingAnAlreadyDeletedPost() {
        post.setDeleted(true);

        Mockito.when(postRepositoryAdapter.getByIdWithLikes(1L)).thenReturn(post);

        Assertions.assertThrows(DataAlreadyDeletedException.class, () -> postService.deletePost(1L));

        Mockito.verify(postRepositoryAdapter, Mockito.times(1)).getByIdWithLikes(1L);
    }

    @Test
    @DisplayName("Test successful delete post")
    void deletingPost() {
        postDTO = PostDto.builder()
                .deleted(true)
                .build();

        Mockito.when(postRepositoryAdapter.getByIdWithLikes(1L)).thenReturn(post);
        Mockito.when(postMapper.toDto(post)).thenReturn(postDTO);

        PostDto result = postService.deletePost(1L);

        Assertions.assertEquals(postDTO, result);

        Mockito.verify(postRepositoryAdapter, Mockito.times(1)).getByIdWithLikes(1L);
        Mockito.verify(postMapper, Mockito.times(1)).toDto(post);
    }

    @Test
    @DisplayName("Test return exception when post was already deleted")
    void getAlreadyDeletedPost() {
        post.setDeleted(true);

        Mockito.when(postRepositoryAdapter.getByIdWithLikes(1L)).thenReturn(post);

        Assertions.assertThrows(DataAlreadyDeletedException.class, () -> postService.getPostById(1L));

        Mockito.verify(postRepositoryAdapter, Mockito.times(1)).getByIdWithLikes(1L);
    }

    @Test
    @DisplayName("Test return exception when post unpublished yet")
    void testUnpublishedPost() {
        post.setScheduledAt(LocalDateTime.now().plusMinutes(10));

        Mockito.when(postRepositoryAdapter.getByIdWithLikes(1L)).thenReturn(post);

        Assertions.assertThrows(UnpublishedPostException.class, () -> postService.getPostById(1L));

        Mockito.verify(postRepositoryAdapter, Mockito.times(1)).getByIdWithLikes(1L);
    }

    @Test
    @DisplayName("Test successful getting post")
    void testGettingPostWithId() {
        Mockito.when(postRepositoryAdapter.getByIdWithLikes(1L)).thenReturn(post);
        Mockito.when(postMapper.toDto(post)).thenReturn(postDTO);

        PostDto result = postService.getPostById(1L);

        Assertions.assertEquals(postDTO, result);

        Mockito.verify(postRepositoryAdapter, Mockito.times(1)).getByIdWithLikes(1L);
        Mockito.verify(postMapper, Mockito.times(1)).toDto(post);
    }

    @Test
    @DisplayName("Test successful getting all drafts")
    void testGettingAllDrafts() {
        LocalDateTime localDateTime1 = LocalDateTime.of(2024, 10, 10, 10, 30);
        LocalDateTime localDateTime2 = LocalDateTime.of(2024, 10, 20, 10, 30);

        Post draft1 = new Post();
        draft1.setCreatedAt(localDateTime1);

        Post draft2 = new Post();
        draft2.setCreatedAt(localDateTime2);

        List<Post> allPosts = List.of(draft1, draft2);

        Mockito.when(postRepository.findDraftsByAuthorIdWithLikesOrderByCreationDateDesc(1L)).thenReturn(allPosts);
        Mockito.when(postMapper.toDtoList(allPosts)).thenAnswer(invocation -> {
            List<Post> posts = invocation.getArgument(0);
            return posts.stream().map(currentPost -> PostDto.builder()
                    .createdAt(currentPost.getCreatedAt())
                    .build()).toList();
        });

        List<PostDto> result = postService.getAllDraftsByAuthorId(1L);

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(localDateTime1, result.get(0).createdAt());
        Assertions.assertEquals(localDateTime2, result.get(1).createdAt());

        Mockito.verify(postRepository, Mockito.times(1))
                .findDraftsByAuthorIdWithLikesOrderByCreationDateDesc(1L);
        Mockito.verify(postMapper, Mockito.times(1)).toDtoList(allPosts);
    }

    @Test
    @DisplayName("Test successful getting all posts")
    void testGettingAllPosts() {
        LocalDateTime localDateTime1 = LocalDateTime.of(2024, 10, 10, 10, 30);
        LocalDateTime localDateTime2 = LocalDateTime.of(2024, 10, 20, 10, 30);

        Post publishedPost1 = new Post();
        publishedPost1.setPublished(true);
        publishedPost1.setCreatedAt(localDateTime1);

        Post publishedPost2 = new Post();
        publishedPost2.setPublished(true);
        publishedPost2.setCreatedAt(localDateTime2);

        List<Post> allPosts = List.of(publishedPost1, publishedPost2);

        Mockito.when(postRepository.findByAuthorIdWithLikesOrderByPublishDateDesc(1L)).thenReturn(allPosts);
        Mockito.when(postMapper.toDtoList(allPosts)).thenAnswer(invocation -> {
            List<Post> posts = invocation.getArgument(0);
            return posts.stream().map(currentPost -> PostDto.builder()
                    .createdAt(currentPost.getCreatedAt())
                    .build()).toList();
        });

        List<PostDto> result = postService.getAllPostsByAuthorId(1L);

        Assertions.assertEquals(2, result.size());
        Assertions.assertEquals(localDateTime1, result.get(0).createdAt());
        Assertions.assertEquals(localDateTime2, result.get(1).createdAt());

        Mockito.verify(postRepository, Mockito.times(1)).findByAuthorIdWithLikesOrderByPublishDateDesc(1L);
        Mockito.verify(postMapper, Mockito.times(1)).toDtoList(allPosts);
    }
}