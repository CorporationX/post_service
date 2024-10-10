package faang.school.postservice.service.post;

import com.amazonaws.SdkClientException;
import faang.school.postservice.exception.ValidationException;
import faang.school.postservice.exception.post.PostNotFoundException;
import faang.school.postservice.exception.post.PostPublishedException;
import faang.school.postservice.exception.post.image.DownloadImageFromPostException;
import faang.school.postservice.exception.post.image.UploadImageToPostException;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.aws.s3.S3Service;
import faang.school.postservice.utils.ImageRestrictionRule;
import faang.school.postservice.validator.PostValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

import static faang.school.postservice.utils.ImageRestrictionRule.POST_IMAGES;
import static faang.school.postservice.model.VerificationPostStatus.REJECTED;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    private static final String BUCKET_NAME_PREFIX = "posts/post_";

    @Mock
    private PostRepository postRepository;
    @Mock
    private PostValidator postValidator;
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
    @InjectMocks
    private PostService postService;

    private Post postForCreate;
    private Post postForUpdate;
    private Post findedPost;
    private List<Post> authorPosts = new ArrayList<>();
    private List<Post> projectPosts = new ArrayList<>();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(postService, "bucketNamePrefix", BUCKET_NAME_PREFIX);


        postForCreate = Post.builder()
                .content("Some Content")
                .authorId(1L)
                .build();

        postForUpdate = Post.builder()
                .id(1L)
                .content("Updated Content")
                .authorId(2L)
                .build();

        findedPost = Post.builder()
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
    }

    @Test
    void testSuccessUpdate() {
        when(postRepository
                .findByIdAndNotDeleted(postForUpdate.getId()))
                .thenReturn(Optional.ofNullable(findedPost));

        Post result = postService.update(postForUpdate);

        verify(postRepository).save(any(Post.class));

        assertEquals(result.getContent(), postForUpdate.getContent());
        assertNotNull(result.getUpdatedAt());
        assertNotEquals(result.getAuthorId(), postForUpdate.getAuthorId());
    }

    @Test
    void testSuccessPublishPost() {
        when(postRepository
                .findByIdAndNotDeleted(postForUpdate.getId()))
                .thenReturn(Optional.ofNullable(findedPost));

        Post result = postService.publish(findedPost.getId());

        verify(postRepository).save(any(Post.class));

        assertTrue(result.isPublished());
        assertNotNull(result.getPublishedAt());
    }

    @Test
    void testFailedPublishPost() {
        findedPost.setPublished(true);
        when(postRepository
                .findByIdAndNotDeleted(postForUpdate.getId()))
                .thenReturn(Optional.ofNullable(findedPost));

        assertThrows(PostPublishedException.class, () -> postService.publish(findedPost.getId()));
        verify(postRepository, times(0)).save(any(Post.class));
    }

    @Test
    void testSuccessDelete() {
        when(postRepository
                .findByIdAndNotDeleted(1L))
                .thenReturn(Optional.ofNullable(findedPost));

        postService.delete(findedPost.getId());

        verify(postRepository).save(any(Post.class));
    }

    @Test
    void testFindPostById() {
        when(postRepository
                .findByIdAndNotDeleted(findedPost.getId()))
                .thenReturn(Optional.ofNullable(findedPost));

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
    void testFindUserIdsForBan_nullResult(){
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
}
