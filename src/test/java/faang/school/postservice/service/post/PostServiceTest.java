package faang.school.postservice.service.post;

import faang.school.postservice.dto.post.serializable.PostCacheDto;
import faang.school.postservice.exception.post.PostNotFoundException;
import faang.school.postservice.exception.post.PostPublishedException;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.post.cache.PostCacheOperations;
import faang.school.postservice.service.post.cache.PostCacheService;
import faang.school.postservice.service.post.hash.tag.PostHashTagParser;
import faang.school.postservice.validator.PostValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static faang.school.postservice.util.post.PostCacheFabric.buildPost;
import static faang.school.postservice.util.post.PostCacheFabric.buildPostCacheDtosForMapping;
import static faang.school.postservice.util.post.PostCacheFabric.buildPostsForMapping;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    private static final long FIRST_POST_ID = 1L;
    private static final long FIRST_AUTHOR_ID = 1L;
    private static final long SECOND_AUTHOR_ID = 2L;
    private static final boolean PUBLISHED = true;
    private static final String DEFAULT_CONTENT = "Some Content";
    private static final String UPDATED_CONTENT = "Updated content";
    private static final String HASH_TAG = "java";
    private static final String HASH_TAG_JSON = "[\"java\"]";
    private static final int START_RANGE = 0;
    private static final int END_RANGE = 10;
    private static final int NUMBER_OF_TOP_IN_CASH = 100;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostValidator postValidator;

    @Mock
    private PostHashTagParser postHashTagParser;

    @Mock
    private PostCacheService postCacheService;

    @Mock
    private PostMapper postMapper;

    @Mock
    private PostCacheOperations postCacheOperations;

    @InjectMocks
    private PostService postService;

    private Post postForCreate;
    private Post postForUpdate;
    private Post findedPost;
    private Post publishedPost;
    private final List<Post> authorPosts = new ArrayList<>();
    private final List<Post> projectPosts = new ArrayList<>();
    private final List<String> defaultHashTags = List.of("java", "sql", "redis");

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(postService, "numberOfTopInCache", NUMBER_OF_TOP_IN_CASH);

        postForCreate = buildPost(FIRST_POST_ID, DEFAULT_CONTENT);
        postForUpdate = buildPost(FIRST_POST_ID, UPDATED_CONTENT, SECOND_AUTHOR_ID, defaultHashTags);
        findedPost = buildPost(FIRST_POST_ID, DEFAULT_CONTENT, FIRST_AUTHOR_ID, defaultHashTags);
        publishedPost = buildPost(FIRST_POST_ID, PUBLISHED);

        fulledAuthorAndProjectLists();
    }

    @Test
    void testCreateSuccessful() {
        when(postRepository.save(postForCreate)).thenReturn(postForCreate);
        Post result = postService.create(postForCreate);

        assertEquals(result.getContent(), postForCreate.getContent());
        assertFalse(result.isDeleted());
        assertFalse(result.isPublished());
        assertNotNull(result.getCreatedAt());

        verify(postRepository).save(postForCreate);
        verify(postHashTagParser).updateHashTags(postForCreate);
    }

    @Test
    void testUpdateSuccessful() {
        when(postRepository.findByIdAndNotDeleted(postForUpdate.getId())).thenReturn(Optional.ofNullable(findedPost));
        when(postRepository.save(findedPost)).thenReturn(findedPost);
        Post result = postService.update(postForUpdate);

        assertEquals(result.getContent(), UPDATED_CONTENT);
        assertNotNull(result.getUpdatedAt());

        verify(postRepository).save(any(Post.class));
        verify(postHashTagParser).updateHashTags(findedPost);
    }

    @Test
    void testPublishPostAlreadyPublished() {
        when(postRepository.findByIdAndNotDeleted(postForUpdate.getId())).thenReturn(Optional.ofNullable(publishedPost));

        assertThrows(PostPublishedException.class, () -> postService.publish(findedPost.getId()));
    }

    @Test
    void testPublishSuccessful() {
        when(postRepository.findByIdAndNotDeleted(FIRST_POST_ID)).thenReturn(Optional.ofNullable(findedPost));
        when(postRepository.save(findedPost)).thenReturn(findedPost);
        when(postMapper.toPostCacheDto(findedPost)).thenReturn(mock(PostCacheDto.class));
        Post result = postService.publish(findedPost.getId());

        assertTrue(result.isPublished());
        assertNotNull(result.getPublishedAt());

        verify(postHashTagParser).updateHashTags(findedPost);
        verify(postCacheService).executeNewPostProcess(any(PostCacheDto.class));
    }

    @Test
    void testDeleteSuccessful() {
        PostCacheDto postCacheDto = mock(PostCacheDto.class);
        when(postRepository.findByIdAndNotDeleted(FIRST_POST_ID)).thenReturn(Optional.ofNullable(findedPost));
        when(postMapper.toPostCacheDto(findedPost)).thenReturn(postCacheDto);
        postService.delete(findedPost.getId());

        assertTrue(findedPost.isDeleted());

        verify(postCacheService).executeDeletePostProcess(postCacheDto, findedPost.getHashTags());
        verify(postRepository).save(findedPost);
    }

    @Test
    @DisplayName("Given empty list of post from cache when check then update posts in cache")
    void testFindInRangeByHashTagCacheResultEmpty() {
        List<PostCacheDto> postCacheDtos = buildPostCacheDtosForMapping();
        List<Post> posts = buildPostsForMapping();
        when(postCacheService.findInRangeByHashTag(HASH_TAG, START_RANGE, END_RANGE)).thenReturn(new ArrayList<>());
        when(postCacheOperations.isRedisConnected()).thenReturn(true);
        when(postHashTagParser.convertTagToJson(HASH_TAG)).thenReturn(HASH_TAG_JSON);
        when(postRepository.findTopByHashTagByDate(HASH_TAG_JSON, NUMBER_OF_TOP_IN_CASH)).thenReturn(posts);
        when(postMapper.mapToPostCacheDtos(posts)).thenReturn(postCacheDtos);

        assertThat(postService.findInRangeByHashTag(HASH_TAG, START_RANGE, END_RANGE))
                .isEqualTo(postCacheDtos);

        verify(postCacheService).addListOfPostsToCache(postCacheDtos, HASH_TAG);
    }

    @Test
    @DisplayName("Given empty list of post from cache and redis disconnect when check then find posts in DB")
    void testFindInRangeByHashTagCacheResultEmptyRedisDisconnect() {
        List<PostCacheDto> postCacheDtos = buildPostCacheDtosForMapping();
        List<Post> posts = buildPostsForMapping();
        when(postCacheService.findInRangeByHashTag(HASH_TAG, START_RANGE, END_RANGE)).thenReturn(new ArrayList<>());
        when(postCacheOperations.isRedisConnected()).thenReturn(false);
        when(postHashTagParser.convertTagToJson(HASH_TAG)).thenReturn(HASH_TAG_JSON);
        when(postRepository.findInRangeByHashTagByDate(HASH_TAG_JSON, START_RANGE, END_RANGE)).thenReturn(posts);
        when(postMapper.mapToPostCacheDtos(posts)).thenReturn(postCacheDtos);

        assertThat(postService.findInRangeByHashTag(HASH_TAG, START_RANGE, END_RANGE))
                .isEqualTo(postCacheDtos);
    }

    @Test
    @DisplayName("Given list of post from cache when check then return posts")
    void testFindInRangeByHashTagSuccessful() {
        List<PostCacheDto> postCacheDtos = buildPostCacheDtosForMapping();
        when(postCacheService.findInRangeByHashTag(HASH_TAG, START_RANGE, END_RANGE)).thenReturn(postCacheDtos);

        assertThat(postService.findInRangeByHashTag(HASH_TAG, START_RANGE, END_RANGE))
                .isEqualTo(postCacheDtos);
    }

    @Test
    void testFindPostById() {
        when(postRepository.findByIdAndNotDeleted(findedPost.getId())).thenReturn(Optional.ofNullable(findedPost));
        Post result = postService.findPostById(findedPost.getId());

        verify(postRepository).findByIdAndNotDeleted(findedPost.getId());
        assertEquals(result, findedPost);
    }

    @Test
    void testFindPostByIdNotFound() {
        when(postRepository
                .findByIdAndNotDeleted(findedPost.getId()))
                .thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postService.findPostById(findedPost.getId()));

        verify(postRepository).findByIdAndNotDeleted(findedPost.getId());
    }

    @Test
    void testSearchPublishedPostsByAuthor() {
        Post filterPost = Post.builder()
                .authorId(2L)
                .published(true)
                .build();

        when(postRepository
                .findByAuthorId(filterPost.getAuthorId()))
                .thenReturn(authorPosts.stream()
                        .filter((p) -> p.getAuthorId().equals(filterPost.getAuthorId()))
                        .toList()
                );

        List<Post> result = postService.searchByAuthor(filterPost);

        assertEquals(result.size(), 1);
        assertEquals(result.get(0), authorPosts.get(1));

        verify(postRepository).findByAuthorId(filterPost.getAuthorId());
        verify(postRepository, times(0)).findByProjectId(anyLong());
    }

    @Test
    void testSearchUnPublishedPostsByAuthor() {
        Post filterPost = Post.builder()
                .authorId(1L)
                .published(false)
                .build();

        when(postRepository
                .findByAuthorId(filterPost.getAuthorId()))
                .thenReturn(authorPosts.stream()
                        .filter((p) -> p.getAuthorId().equals(filterPost.getAuthorId()))
                        .toList()
                );

        List<Post> result = postService.searchByAuthor(filterPost);

        assertEquals(result.size(), 1);
        assertEquals(result.get(0), authorPosts.get(0));
        assertFalse(result.get(0).isPublished());

        verify(postRepository).findByAuthorId(filterPost.getAuthorId());
        verify(postRepository, times(0)).findByProjectId(anyLong());
    }

    @Test
    void testSearchPublishedPostsByProject() {
        Post filterPost = Post.builder()
                .projectId(1L)
                .published(true)
                .build();

        when(postRepository
                .findByProjectId(filterPost.getProjectId()))
                .thenReturn(projectPosts.stream()
                        .filter((p) -> p.getProjectId().equals(filterPost.getProjectId()))
                        .toList()
                );

        List<Post> result = postService.searchByProject(filterPost);

        assertEquals(result.size(), 2);
        assertEquals(result.get(0), projectPosts.get(1));
        assertEquals(result.get(1), projectPosts.get(0));

        verify(postRepository).findByProjectId(filterPost.getProjectId());
        verify(postRepository, times(0)).findByAuthorId(anyLong());
    }

    @Test
    void testSearchUnPublishedPostsByProject() {
        Post filterPost = Post.builder()
                .projectId(1L)
                .published(false)
                .build();

        when(postRepository
                .findByProjectId(filterPost.getProjectId()))
                .thenReturn(projectPosts.stream()
                        .filter((p) -> p.getProjectId().equals(filterPost.getProjectId()))
                        .toList()
                );

        List<Post> result = postService.searchByProject(filterPost);

        assertEquals(result.size(), 0);

        verify(postRepository).findByProjectId(filterPost.getProjectId());
        verify(postRepository, times(0)).findByAuthorId(anyLong());
    }

    private void fulledAuthorAndProjectLists() {
        authorPosts.add(Post.builder()
                .id(1L)
                .content("Content 1")
                .deleted(false)
                .published(false)
                .authorId(1L)
                .createdAt(LocalDateTime.of(2024, 9, 17, 0, 0))
                .publishedAt(LocalDateTime.of(2024, 9, 17, 0, 0))
                .build());

        authorPosts.add(Post.builder()
                .id(2L)
                .content("Content 2")
                .deleted(false)
                .published(true)
                .authorId(2L)
                .createdAt(LocalDateTime.of(2024, 9, 16, 0, 0))
                .publishedAt(LocalDateTime.of(2024, 9, 16, 0, 0))
                .build());

        projectPosts.add(Post.builder()
                .id(3L)
                .content("Content 3")
                .deleted(false)
                .published(true)
                .projectId(1L)
                .createdAt(LocalDateTime.of(2024, 9, 13, 0, 0))
                .publishedAt(LocalDateTime.of(2024, 9, 13, 0, 0))
                .build());

        projectPosts.add(Post.builder()
                .id(4L)
                .content("Content 4")
                .deleted(false)
                .published(true)
                .projectId(1L)
                .createdAt(LocalDateTime.of(2024, 9, 14, 0, 0))
                .publishedAt(LocalDateTime.of(2024, 9, 14, 0, 0))
                .build());
    }
}
