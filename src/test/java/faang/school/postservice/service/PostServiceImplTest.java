package faang.school.postservice.service;

import faang.school.postservice.config.image.ImageDimensions;
import faang.school.postservice.config.image.ImageProcessingProperties;
import faang.school.postservice.config.image.ImageResizeProperties;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.dto.ResourceDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.exception.PostNotFoundException;
import faang.school.postservice.mapper.PostMapperImpl;
import faang.school.postservice.mapper.ResourceMapperImpl;
import faang.school.postservice.model.Like;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private PostRepository postRepository;

    @Spy
    private PostMapperImpl postMapper;

    @Spy
    private ResourceMapperImpl resourceMapper;

    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private MinioClient minioClient;

    @Mock
    private ImageProcessingProperties properties;

    @Mock
    private ThreadPoolTaskScheduler taskScheduler;

    @Mock
    private PostModerationDictionaryImpl moderationDictionary;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ChannelTopic channelTopic;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private PostServiceImpl postService;

    private final int countOfUnverifiedPostsToBan = 2;

    private PostDto postDto;
    private Post post;

    @BeforeEach
    public void setUp() {
        postDto = PostDto.builder()
                .id(1L)
                .content("Test content")
                .authorId(1L)
                .published(false)
                .deleted(false)
                .build();

        post = Post.builder()
                .id(1L)
                .content("Test content")
                .authorId(1L)
                .published(false)
                .deleted(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    public void testCreateDraft() {
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(userServiceClient.getUser(postDto.getAuthorId()))
                .thenReturn(new UserDto(1L, "Rick", "test"));

        PostDto result = postService.createDraft(postDto);

        assertNotNull(result);
        assertEquals(postDto.getContent(), result.getContent());
        assertEquals(postDto.getAuthorId(), result.getAuthorId());
        verify(postMapper).toEntity(postDto);
        verify(postRepository).save(any(Post.class));
        verify(postMapper).toDto(post);
    }

    @Test
    public void testPublishPost() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(post);

        PostDto result = postService.publishPost(1L);

        assertNotNull(result);
        assertTrue(post.isPublished());
        assertNotNull(post.getPublishedAt());
        verify(postRepository).findById(1L);
        verify(postRepository).save(post);
        verify(postMapper).toDto(post);
    }

    @Test
    public void testPublishPostAlreadyPublished() {
        post.setPublished(true);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        assertThrows(IllegalStateException.class, () -> postService.publishPost(1L));
        verify(postRepository).findById(1L);
        verify(postRepository, never()).save(post);
    }

    @Test
    public void testUpdatePost() {
        PostDto updatedPostDto = PostDto.builder()
                .id(1L)
                .content("Updated content")
                .authorId(1L)
                .build();

        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(userServiceClient.getUser(postDto.getAuthorId()))
                .thenReturn(new UserDto(1L, "Rick", "test"));
        when(postRepository.save(post)).thenReturn(post);

        PostDto result = postService.updatePost(1L, updatedPostDto);

        assertNotNull(result);
        assertEquals("Updated content", result.getContent());
        verify(postRepository).findById(1L);
        verify(postRepository).save(post);
        verify(postMapper).toDto(post);
    }

    @Test
    public void testUpdatePostChangeAuthor() {
        PostDto updatedPostDto = PostDto.builder()
                .id(1L)
                .content("Updated content")
                .authorId(2L)
                .build();
        UserDto userDto = new UserDto(2L, "Rick", "test");
        when(userServiceClient.getUser(2L)).thenReturn(userDto);
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        assertThrows(IllegalArgumentException.class, () -> postService.updatePost(1L, updatedPostDto));
        verify(postRepository).findById(1L);
        verify(postRepository, never()).save(post);
    }

    @Test
    public void testSoftDelete() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postRepository.save(post)).thenReturn(post);

        PostDto result = postService.softDelete(1L);

        assertNotNull(result);
        assertTrue(post.isDeleted());
        verify(postRepository).findById(1L);
        verify(postRepository).save(post);
        verify(postMapper).toDto(post);
    }

    @Test
    public void testGetPostById() {
        post.setLikes(List.of(new Like(), new Like()));

        when(postRepository.findByIdWithLikes(1L)).thenReturn(Optional.of(post));

        PostDto result = postService.getPostById(1L);

        assertNotNull(result);
        assertEquals(postDto.getContent(), result.getContent());
        assertEquals(postDto.getAuthorId(), result.getAuthorId());
        assertEquals(2, result.getLikeCount());
        verify(postRepository).findByIdWithLikes(1L);
        verify(postMapper).toDto(post);
    }

    @Test
    public void testGetPostByIdNotFound() {
        when(postRepository.findByIdWithLikes(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> postService.getPostById(1L));
        verify(postRepository).findByIdWithLikes(1L);
    }

    @Test
    public void testGetAllDraftsByAuthorId() {
        when(postRepository.findByAuthorId(1L)).thenReturn(List.of(post));

        List<PostDto> result = postService.getAllDraftsByAuthorId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(postDto.getContent(), result.get(0).getContent());
        verify(postRepository).findByAuthorId(1L);
        verify(postMapper).toDto(post);
    }

    @Test
    public void testGetAllDraftsByProjectId() {
        when(postRepository.findByProjectId(1L)).thenReturn(List.of(post));

        List<PostDto> result = postService.getAllDraftsByProjectId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(postDto.getContent(), result.get(0).getContent());
        verify(postRepository).findByProjectId(1L);
        verify(postMapper).toDto(post);
    }

    @Test
    public void testGetAllPublishedPostsByAuthorId() {
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        post.setLikes(List.of(new Like(), new Like()));

        when(postRepository.findByAuthorIdWithLikes(1L)).thenReturn(List.of(post));

        List<PostDto> result = postService.getAllPublishedPostsByAuthorId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(postDto.getContent(), result.get(0).getContent());
        assertEquals(2, result.get(0).getLikeCount());
        verify(postRepository).findByAuthorIdWithLikes(1L);
        verify(postMapper).toDto(post);
    }

    @Test
    public void testGetAllPublishedPostsByProjectId() {
        post.setPublished(true);
        post.setPublishedAt(LocalDateTime.now());
        post.setLikes(List.of(new Like(), new Like()));

        when(postRepository.findByProjectIdWithLikes(1L)).thenReturn(List.of(post));

        List<PostDto> result = postService.getAllPublishedPostsByProjectId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(postDto.getContent(), result.get(0).getContent());
        assertEquals(2, result.get(0).getLikeCount());
        verify(postRepository).findByProjectIdWithLikes(1L);
        verify(postMapper).toDto(post);
    }

    @Test
    void testGetPostEntryByIdPostNotFound() {
        long postId = 2L;

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> postService.getPostEntryById(postId)
        );
        assertEquals("Post not found", exception.getMessage());
    }

    @Test
    public void testModeratePostsWithNoPosts() {
        ReflectionTestUtils.setField(postService, "limitToModerate", 2);
        when(postRepository.findUnverifiedPosts(2)).thenReturn(Collections.emptyList());

        postService.moderatePosts();

        verify(postRepository, times(1)).findUnverifiedPosts(2);
        verifyNoMoreInteractions(postRepository);
    }

    @Test
    public void testModeratePostsWithOnePackOfPosts() {
        ReflectionTestUtils.setField(postService, "limitToModerate", 2);
        ScheduledExecutorService realExecutor = Executors.newScheduledThreadPool(2);
        when(taskScheduler.getScheduledExecutor()).thenReturn(realExecutor);
        when(postRepository.findUnverifiedPosts(2))
                .thenReturn(List.of(post, post))
                .thenReturn(Collections.emptyList());

        postService.moderatePosts();

        verify(postRepository, times(2)).findUnverifiedPosts(2);
        verify(postRepository, times(2)).save(any());
    }

    @Test
    public void testModeratePostsWithSomePacksOfPosts() {
        ReflectionTestUtils.setField(postService, "limitToModerate", 2);
        ScheduledExecutorService realExecutor = Executors.newScheduledThreadPool(2);
        when(taskScheduler.getScheduledExecutor()).thenReturn(realExecutor);
        when(postRepository.findUnverifiedPosts(2))
                .thenReturn(List.of(post, post))
                .thenReturn(List.of(post))
                .thenReturn(Collections.emptyList());

        postService.moderatePosts();

        verify(postRepository, times(3)).findUnverifiedPosts(2);
        verify(postRepository, times(3)).save(any());
    }

    @Test
    public void testUploadImageToPostWithWrongPostId() throws Exception {
        Long postId = 1L;
        MultipartFile file1 = mock(MultipartFile.class);
        MultipartFile file2 = mock(MultipartFile.class);
        List<MultipartFile> files = List.of(file1, file2);
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> postService.uploadImageToPost(1L, files));
        verify(resourceRepository, never()).save(any());
        verify(minioClient, never()).putObject(any());
    }

    @Test
    public void testUploadImageToPostHandleProceedFileException() throws Exception {
        Long postId = 1L;
        MultipartFile invalidFile = mock(MultipartFile.class);
        List<MultipartFile> files = List.of(invalidFile);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(properties.getAllowedContentTypes()).thenReturn(List.of("image/jpeg"));
        when(invalidFile.getContentType()).thenReturn("image/jpeg");
        when(invalidFile.getInputStream()).thenThrow(new IOException("Test error"));

        assertThrows(RuntimeException.class, () -> postService.uploadImageToPost(postId, files));

        verify(resourceRepository, never()).save(any());
        verify(minioClient, never()).putObject(any());
    }

    @Test
    public void testUploadImageToPostRollbackWhenMinioUploadFails() throws Exception {
        Long postId = 1L;
        ReflectionTestUtils.setField(postService, "bucketName", "test-bucket");

        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        File imageFile = File.createTempFile("test", ".jpg");
        ImageIO.write(image, "jpg", imageFile);
        imageFile.deleteOnExit();

        MultipartFile file = new MockMultipartFile(
                "test.jpg", "test.jpg", "image/jpeg", new FileInputStream(imageFile)
        );
        List<MultipartFile> files = List.of(file);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(properties.getAllowedContentTypes()).thenReturn(List.of("image/jpeg"));

        ImageResizeProperties resizeProps = createProperties();
        when(properties.getResize()).thenReturn(resizeProps);

        Resource savedResource = createResource();

        doThrow(new RuntimeException("MinIO error"))
                .when(minioClient).putObject(any(PutObjectArgs.class));

        assertThrows(RuntimeException.class, () -> postService.uploadImageToPost(postId, files));
    }

    @Test
    public void testUploadImageToPostSuccessfullyUploadMultipleFiles() throws Exception {
        Long postId = 1L;
        ReflectionTestUtils.setField(postService, "bucketName", "test-bucket");

        BufferedImage image1 = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        BufferedImage image2 = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);

        File tempFile1 = File.createTempFile("test1", ".jpg");
        File tempFile2 = File.createTempFile("test2", ".jpg");
        ImageIO.write(image1, "jpg", tempFile1);
        ImageIO.write(image2, "jpg", tempFile2);
        tempFile1.deleteOnExit();
        tempFile2.deleteOnExit();

        MultipartFile file1 = new MockMultipartFile("image1.jpg",
                "image1.jpg", "image/jpeg", new FileInputStream(tempFile1));
        MultipartFile file2 = new MockMultipartFile("image2.jpg",
                "image2.jpg", "image/jpeg", new FileInputStream(tempFile2));
        List<MultipartFile> files = List.of(file1, file2);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(properties.getAllowedContentTypes()).thenReturn(List.of("image/jpeg"));

        ImageResizeProperties resizeProps = createProperties();
        when(properties.getResize()).thenReturn(resizeProps);

        Resource resource1 = createResource();
        Resource resource2 = createResource();
        resource1.setKey("posts/uuid1.jpg");
        resource2.setKey("posts/uuid2.jpg");

        when(resourceRepository.save(any(Resource.class)))
                .thenReturn(resource1)
                .thenReturn(resource2);

        ResourceDto dto1 = resourceMapper.toDto(resource1);
        ResourceDto dto2 = resourceMapper.toDto(resource2);
        when(resourceMapper.toDto(resource1)).thenReturn(dto1);
        when(resourceMapper.toDto(resource2)).thenReturn(dto2);

        List<ResourceDto> result = postService.uploadImageToPost(postId, files);

        assertEquals(2, result.size());
        assertEquals("posts/uuid1.jpg", result.get(0).getKey());
        assertEquals("posts/uuid2.jpg", result.get(1).getKey());

        verify(postRepository).findById(postId);
        verify(resourceRepository, times(2)).save(any(Resource.class));
        verify(minioClient, times(2)).putObject(any(PutObjectArgs.class));

        ArgumentCaptor<Resource> resourceCaptor = ArgumentCaptor.forClass(Resource.class);
        verify(resourceRepository, times(2)).save(resourceCaptor.capture());

        List<Resource> savedResources = resourceCaptor.getAllValues();
        assertEquals(2, savedResources.size());
    }

    private ImageResizeProperties createProperties() {
        ImageResizeProperties resizeProps = new ImageResizeProperties();
        ImageDimensions square = new ImageDimensions();
        ImageDimensions horizontal = new ImageDimensions();
        square.setHeight(800);
        square.setWidth(800);
        horizontal.setWidth(1200);
        horizontal.setHeight(900);
        resizeProps.setSquare(square);
        resizeProps.setHorizontal(horizontal);
        return resizeProps;
    }

    private Resource createResource() throws IOException {
        Resource resource = new Resource();
        resource.setId(1L);
        resource.setKey("posts/test.jpg");
        resource.setPost(post);
        return resource;
    }

    @Test
    void testGetPostEntryByIdSuccessfulFetch() {
        long postId = 1L;
        Post post = new Post();
        post.setId(postId);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        Post result = postService.getPostEntryById(postId);

        assertNotNull(result);
        assertEquals(postId, result.getId());
        verify(postRepository, times(1)).findById(postId);
    }

    @Test
    public void testFindPostByIdThrowPostNotFoundException() {
        when(postRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postService.findPostById(1L));
    }

    @Test
    public void testFindPostById() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        Post actualResult = postService.findPostById(1L);

        assertNotNull(actualResult);
        assertEquals(postDto.getContent(), actualResult.getContent());
        assertEquals(postDto.getAuthorId(), actualResult.getAuthorId());
        verify(postRepository, times(1)).findById(1L);
    }

    @Test
    public void testRemoveTagsFromPost() {
        postService.removeTagsFromPost(1L, List.of(1L, 2L));

        verify(postRepository, times(1)).deleteTagsFromPost(1L, List.of(1L, 2L));
    }

    @Test
    public void testBanUserWithTooManyOffendedPostsWithNoRejectedPosts() {
        when(postRepository.findByVerifiedFalse()).thenReturn(Collections.emptyList());

        postService.banUsersWithTooManyOffendedPosts();

        verify(redisTemplate, never()).convertAndSend(anyString(), any());
    }

    @Test
    public void testBanUserWithTooManyOffendedPostsSuccessfully() {
        post.setAuthorId(1L);
        when(postRepository.findByVerifiedFalse()).thenReturn(List.of(post, post, post));
        when(channelTopic.getTopic()).thenReturn("ban-users");

        postService.banUsersWithTooManyOffendedPosts();

        verify(redisTemplate, times(1)).convertAndSend(anyString(), any());
    }
}
