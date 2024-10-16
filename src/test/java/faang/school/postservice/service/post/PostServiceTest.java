package faang.school.postservice.service.post;

import com.amazonaws.SdkClientException;
import faang.school.postservice.dto.post.serializable.PostCacheDto;
import faang.school.postservice.exception.ValidationException;
import faang.school.postservice.exception.post.PostNotFoundException;
import faang.school.postservice.exception.post.PostPublishedException;
import faang.school.postservice.exception.post.image.DownloadImageFromPostException;
import faang.school.postservice.exception.post.image.UploadImageToPostException;
import faang.school.postservice.exception.spelling_corrector.DontRepeatableServiceException;
import faang.school.postservice.exception.spelling_corrector.RepeatableServiceException;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.aws.s3.S3Service;
import faang.school.postservice.service.post.cache.PostCacheProcessExecutor;
import faang.school.postservice.service.post.cache.PostCacheService;
import faang.school.postservice.service.post.hash.tag.PostHashTagParser;
import faang.school.postservice.utils.ImageRestrictionRule;
import faang.school.postservice.validator.PostValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static faang.school.postservice.model.VerificationPostStatus.REJECTED;
import static faang.school.postservice.util.post.PostCacheFabric.buildPostCacheDtosForMapping;
import static faang.school.postservice.util.post.PostCacheFabric.buildPostsForMapping;
import static faang.school.postservice.utils.ImageRestrictionRule.POST_IMAGES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    private static final String BUCKET_NAME_PREFIX = "posts/post_";

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
    private SpellingCorrectionService spellingCorrectionService;
    @Mock
    private ResourceRepository resourceRepository;
    @Mock
    private S3Service s3Service;
    @Mock
    private MultipartFile image1;
    @Mock
    private MultipartFile image2;
    @Mock
    private InputStream inputStream;
    @Mock
    private PostHashTagParser postHashTagParser;
    @Mock
    private PostCacheProcessExecutor postCacheProcessExecutor;
    @Mock
    private PostMapper postMapper;
    @Mock
    private PostCacheService postCacheService;
    @InjectMocks
    private PostService postService;

    private Post postForCreate;
    private Post postForUpdate;
    private Post foundPost;
    private List<Post> authorPosts = new ArrayList<>();
    private List<Post> projectPosts = new ArrayList<>();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(postService, "bucketNamePrefix", BUCKET_NAME_PREFIX);
        ReflectionTestUtils.setField(postService, "numberOfTopInCache", NUMBER_OF_TOP_IN_CASH);

        postForCreate = Post.builder()
                .content("Some Content")
                .authorId(1L)
                .build();

        postForUpdate = Post.builder()
                .id(1L)
                .content("Updated Content")
                .authorId(2L)
                .build();

        foundPost = Post.builder()
                .id(1L)
                .content("Some Content")
                .authorId(1L)
                .build();

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

    @Test
    void testSuccessCreate() {
        when(postRepository.save(postForCreate)).thenReturn(postForCreate);

        Post result = postService.create(postForCreate);

        verify(postRepository).save(postForCreate);

        assertEquals(result.getContent(), postForCreate.getContent());
        assertFalse(result.isDeleted());
        assertFalse(result.isPublished());
        assertNotNull(result.getCreatedAt());

        verify(postHashTagParser).updateHashTags(postForCreate);
    }

    @Test
    void testSuccessUpdate() {
        List<String> primalTags = List.of("java", "sql", "redis");
        foundPost.setHashTags(primalTags);
        foundPost.setDeleted(false);
        foundPost.setPublished(true);
        when(postRepository.findByIdAndNotDeleted(postForUpdate.getId()))
                .thenReturn(Optional.ofNullable(foundPost));

        Post result = postService.update(postForUpdate);

        verify(postRepository).save(any(Post.class));

        assertEquals(result.getContent(), postForUpdate.getContent());
        assertNotNull(result.getUpdatedAt());
        assertNotEquals(result.getAuthorId(), postForUpdate.getAuthorId());

        ArgumentCaptor<PostCacheDto> captor = ArgumentCaptor.forClass(PostCacheDto.class);
        verify(postHashTagParser).updateHashTags(any(Post.class));
        verify(postCacheProcessExecutor).executeUpdatePostProcess(captor.capture(), anyList());
    }

    @Test
    void testSuccessPublishPost() {
        when(postRepository.findByIdAndNotDeleted(postForUpdate.getId()))
                .thenReturn(Optional.ofNullable(foundPost));

        Post result = postService.publish(foundPost.getId());

        verify(postRepository).save(any(Post.class));

        assertTrue(result.isPublished());
        assertNotNull(result.getPublishedAt());

        ArgumentCaptor<PostCacheDto> captor = ArgumentCaptor.forClass(PostCacheDto.class);
        verify(postHashTagParser).updateHashTags(any(Post.class));
        verify(postCacheProcessExecutor).executeNewPostProcess(captor.capture());
    }

    @Test
    void testFailedPublishPost() {
        foundPost.setPublished(true);
        when(postRepository.findByIdAndNotDeleted(postForUpdate.getId()))
                .thenReturn(Optional.ofNullable(foundPost));

        assertThrows(PostPublishedException.class, () -> postService.publish(foundPost.getId()));
        verify(postRepository, never()).save(any(Post.class));
    }

    @Test
    void testSuccessDelete() {
        List<String> primalTags = List.of("java", "sql", "redis");
        foundPost.setHashTags(primalTags);
        when(postRepository.findByIdAndNotDeleted(1L)).thenReturn(Optional.ofNullable(foundPost));

        postService.delete(foundPost.getId());

        verify(postRepository).save(any(Post.class));

        ArgumentCaptor<PostCacheDto> captor = ArgumentCaptor.forClass(PostCacheDto.class);
        verify(postCacheProcessExecutor).executeDeletePostProcess(captor.capture(), anyList());
    }

    @Test
    @DisplayName("Given empty list of post from cache when check then update posts in cache")
    void testFindInRangeByHashTagCacheResultEmpty() {
        List<PostCacheDto> postCacheDtos = buildPostCacheDtosForMapping();
        List<Post> posts = buildPostsForMapping();
        when(postCacheService.findInRangeByHashTag(HASH_TAG, START_RANGE, END_RANGE)).thenReturn(new ArrayList<>());
        when(postCacheService.isRedisConnected()).thenReturn(true);
        when(postHashTagParser.convertTagToJson(HASH_TAG)).thenReturn(HASH_TAG_JSON);
        when(postRepository.findTopByHashTagByDate(HASH_TAG_JSON, NUMBER_OF_TOP_IN_CASH)).thenReturn(posts);
        when(postMapper.mapToPostCacheDtos(posts)).thenReturn(postCacheDtos);

        assertThat(postService.findInRangeByHashTag(HASH_TAG, START_RANGE, END_RANGE))
                .isEqualTo(postCacheDtos);

        verify(postCacheProcessExecutor).executeAddListOfPostsToCache(postCacheDtos, HASH_TAG);
    }

    @Test
    @DisplayName("Given empty list of post from cache and redis disconnect when check then find posts in DB")
    void testFindInRangeByHashTagCacheResultEmptyRedisDisconnect() {
        List<PostCacheDto> postCacheDtos = buildPostCacheDtosForMapping();
        List<Post> posts = buildPostsForMapping();
        when(postCacheService.findInRangeByHashTag(HASH_TAG, START_RANGE, END_RANGE)).thenReturn(new ArrayList<>());
        when(postCacheService.isRedisConnected()).thenReturn(false);
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
        when(postRepository
                .findByIdAndNotDeleted(foundPost.getId()))
                .thenReturn(Optional.ofNullable(foundPost));

        Post result = postService.findPostById(foundPost.getId());

        verify(postRepository).findByIdAndNotDeleted(foundPost.getId());
        assertEquals(result, foundPost);
    }

    @Test
    void testFindPostByIdNotFound() {
        when(postRepository
                .findByIdAndNotDeleted(foundPost.getId()))
                .thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postService.findPostById(foundPost.getId()));

        verify(postRepository).findByIdAndNotDeleted(foundPost.getId());
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
        verify(postRepository, never()).findByProjectId(anyLong());
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
        verify(postRepository, never()).findByProjectId(anyLong());
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
        verify(postRepository, never()).findByAuthorId(anyLong());
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
        verify(postRepository, never()).findByAuthorId(anyLong());
    }

    @Test
    void testCorrectPosts() {
        List<Post> posts = List.of(foundPost);
        String correctedContent = foundPost.getContent() + " Corrected";

        when(spellingCorrectionService.getCorrectedContent(foundPost.getContent()))
                .thenReturn(correctedContent);

        postService.correctPosts(posts);

        verify(postRepository).saveAll(posts);
    }

    @Test
    void testCorrectPostsRepeatableException() {
        List<Post> posts = List.of(foundPost);
        when(spellingCorrectionService.getCorrectedContent(foundPost.getContent()))
                .thenThrow(RepeatableServiceException.class);

        postService.correctPosts(posts);

        verify(postRepository).saveAll(posts);
    }

    @Test
    void testCorrectPostsDontRepeatableException() {
        List<Post> posts = List.of(foundPost);
        when(spellingCorrectionService.getCorrectedContent(foundPost.getContent()))
                .thenThrow(DontRepeatableServiceException.class);

        postService.correctPosts(posts);

        verify(postRepository).saveAll(posts);
    }

    @Test
    void testFindUserIdsForBan_emptyResult() {
        when(postRepository.findAllUsersBorBan(REJECTED)).thenReturn(List.of());
        List<Long> result = postService.findUserIdsForBan();
        assertEquals(0, result.size());
        verify(postRepository, times(1)).findAllUsersBorBan(REJECTED);
    }

    @Test
    void testFindUserIdsForBan_nonEmptyResult() {
        List<Long> expectedUserIds = List.of(1L, 2L, 3L);
        when(postRepository.findAllUsersBorBan(REJECTED)).thenReturn(expectedUserIds);
        List<Long> result = postService.findUserIdsForBan();
        assertEquals(expectedUserIds, result);
        verify(postRepository, times(1)).findAllUsersBorBan(REJECTED);
    }

    @Test
    void testFindUserIdsForBan_nullResult() {
        when(postRepository.findAllUsersBorBan(REJECTED)).thenReturn(null);
        List<Long> result = postService.findUserIdsForBan();
        Assertions.assertNull(result);
        verify(postRepository, times(1)).findAllUsersBorBan(REJECTED);
    }

    @Test
    void testUploadImages_Exception_ValidationException() {
        Long postId = 1L;
        List<MultipartFile> images = List.of(image1, image2);

        doThrow(new ValidationException(""))
                .when(postValidator)
                .validateImagesToUpload(postId, images);

        assertThrows(ValidationException.class, () -> {
            postService.uploadImages(postId, images);
        });
    }

    @Test
    void testUploadImages_Exception_PostNotFound() {
        Long postId = 1L;
        List<MultipartFile> images = List.of(image1, image2);

        when(postRepository.findByIdAndNotDeleted(postId)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> {
            postService.uploadImages(postId, images);
        });
    }

    @Test
    void testUploadImages_Exception_S3ServiceException() throws IOException {
        Long postId = 1L;
        List<MultipartFile> images = List.of(image1, image2);
        Post existedPost = new Post();

        when(postRepository.findByIdAndNotDeleted(postId)).thenReturn(Optional.of(existedPost));
        doThrow(new IOException())
                .when(s3Service)
                .uploadFile(image1, "posts/post_1", POST_IMAGES);

        assertThrows(UploadImageToPostException.class, () -> {
            postService.uploadImages(postId, images);
        });
    }

    @Test
    void testUploadImages_Success() throws IOException {
        Long postId = 1L;
        List<MultipartFile> images = List.of(image1, image2);
        List<Resource> existedImages = List.of(new Resource(), new Resource());
        Post existedPost = new Post();
        Resource savedResource = new Resource();

        when(postRepository.findByIdAndNotDeleted(postId)).thenReturn(Optional.of(existedPost));
        when(s3Service.uploadFile(any(MultipartFile.class), anyString(), any(ImageRestrictionRule.class))).thenReturn(savedResource);
        when(resourceRepository.saveAll(anyList())).thenReturn(List.of(savedResource));

        assertDoesNotThrow(() -> postService.uploadImages(postId, images));
    }

    @Test
    void testDownloadImage_Exception_S3ServiceException() {
        Resource resource = new Resource();
        resource.setKey("key");

        doThrow(new SdkClientException(""))
                .when(s3Service)
                .downloadFile(eq(resource.getKey()));

        assertThrows(DownloadImageFromPostException.class, () -> {
            postService.downloadImage(resource);
        });
    }

    @Test
    void testDownloadImage_Success() throws IOException {
        Resource resource = new Resource();
        resource.setKey("resourceKey");

        when(s3Service.downloadFile(eq(resource.getKey()))).thenReturn(inputStream);

        org.springframework.core.io.Resource result = postService.downloadImage(resource);

        assertNotNull(result);
        assertTrue(result instanceof InputStreamResource);
    }

    @Test
    void testDeleteImagesFromPost_Exception_S3ServiceException() {
        List<Long> resourceIds = List.of(1L, 2L);
        Post post = Post.builder().id(1L).build();
        List<Resource> existedImages = List.of(
                Resource.builder().id(1L).key("key1").post(post).build(),
                Resource.builder().id(2L).key("key1").post(post).build());

        when(resourceRepository.findAllByIdIn(eq(resourceIds))).thenReturn(existedImages);
        doThrow(new SdkClientException(""))
                .when(s3Service)
                .deleteFiles(anyList());

        assertThrows(SdkClientException.class, () -> {
            postService.deleteImagesFromPost(resourceIds);
        });
    }

    @Test
    void testDeleteImagesFromPost_Success() {
        List<Long> resourceIds = List.of(1L, 2L);
        Post post = Post.builder().id(1L).build();
        List<Resource> existedImages = List.of(
                Resource.builder().id(1L).key("key1").post(post).build(),
                Resource.builder().id(2L).key("key1").post(post).build());

        when(resourceRepository.findAllByIdIn(eq(resourceIds))).thenReturn(existedImages);
        doNothing().when(s3Service).deleteFiles(anyList());
        doNothing().when(resourceRepository).deleteAll(existedImages);

        postService.deleteImagesFromPost(resourceIds);
    }

    @Test
    void testGetReadyToPublish() {
        int readyToPublishPostsCount = 2;
        when(postRepository.findReadyToPublishCount()).thenReturn(readyToPublishPostsCount);

        var result = postService.getReadyToPublish();

        verify(postRepository).findReadyToPublishCount();
        assertEquals(readyToPublishPostsCount, result);
    }

    @Test
    void testProcessReadyToPublishPosts() {
        int postPublishBatchSize = 10;
        postService.processReadyToPublishPosts(postPublishBatchSize);
        verify(postRepository).findReadyToPublishSkipLocked(postPublishBatchSize);
    }
}
