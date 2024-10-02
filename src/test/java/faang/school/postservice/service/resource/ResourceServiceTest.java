package faang.school.postservice.service.resource;

import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.model.ResourceType;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.resource.minio.MinioAudioManager;
import faang.school.postservice.service.resource.minio.MinioImageManager;
import faang.school.postservice.service.resource.minio.MinioVideoManager;
import faang.school.postservice.service.resource.validator.AudioFileValidator;
import faang.school.postservice.service.resource.validator.ImageFileValidator;
import faang.school.postservice.service.resource.validator.VideoFileValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResourceServiceTest {
    private final ResourceRepository resourceRepository = mock(ResourceRepository.class);
    private final PostRepository postRepository = mock(PostRepository.class);
    private final MimeConverter mimeConverter = mock(MimeConverter.class);

    private final MinioImageManager minioImageManager = mock(MinioImageManager.class);
    private final MinioAudioManager minioAudioManager = mock(MinioAudioManager.class);
    private final MinioVideoManager minioVideoManager = mock(MinioVideoManager.class);

    private final ImageFileValidator imageValidator = mock(ImageFileValidator.class);
    private final AudioFileValidator audioValidator = mock(AudioFileValidator.class);
    private final VideoFileValidator videoValidator = mock(VideoFileValidator.class);

    private final ResourceService resourceService = new ResourceService(
            resourceRepository,
            postRepository,
            mimeConverter,
            minioImageManager,
            minioAudioManager,
            minioVideoManager,
            imageValidator,
            audioValidator,
            videoValidator
    );

    private final MultipartFile file = mock(MultipartFile.class);
    private Long postId;
    private Post post;

    private Long resourceId;
    private String key;
    private Resource resource;
    private Resource resourceNew;
    private String imageMimeType;

    @BeforeEach
    public void setUp() {
        resourceService.init();

        imageMimeType = "image/jpeg";

        postId = 1L;
        post = Post.builder()
                .id(postId)
                .build();

        resourceId = 10L;
        key = "res1";
        resource = Resource.builder()
                .key(key)
                .id(resourceId)
                .type(ResourceType.IMAGE)
                .post(post)
                .build();

        resourceNew = Resource.builder()
                .type(ResourceType.IMAGE)
                .build();
    }

    @Test
    @DisplayName("testAddFileToPost_Success")
    public void testAddFileToPost_Success() {
        when(file.getContentType()).thenReturn(imageMimeType);
        when(mimeConverter.getType(imageMimeType)).thenReturn(ResourceType.IMAGE);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(minioImageManager.addFileToStorage(file, post)).thenReturn(resourceNew);

        resourceService.addFileToPost(file, postId);

        verify(resourceRepository).save(resourceNew);
    }


    @Test
    @DisplayName("testFindPostById_Invalid")
    public void testFindPostById_Invalid() {
        when(file.getContentType()).thenReturn(imageMimeType);
        when(mimeConverter.getType(imageMimeType)).thenReturn(ResourceType.IMAGE);

        assertThrows(NoSuchElementException.class,
                () -> resourceService.addFileToPost(file, postId));
    }


    @Test
    @DisplayName("testUpdateFileInPost_Success")
    public void testUpdateFileInPost_Success() {
        when(file.getContentType()).thenReturn(imageMimeType);
        when(mimeConverter.getType(imageMimeType)).thenReturn(ResourceType.IMAGE);

        when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(resource));
        when(minioImageManager.updateFileInStorage(key, file, post)).thenReturn(resourceNew);

        resourceService.updateFileInPost(file, resourceId);

        verify(resourceRepository, times(1)).save(resourceNew);
    }

    @Test
    @DisplayName("testFindResourceById_Invalid")
    public void testFindResourceById_Invalid() {
        when(file.getContentType()).thenReturn(imageMimeType);
        when(mimeConverter.getType(imageMimeType)).thenReturn(ResourceType.IMAGE);

        assertThrows(NoSuchElementException.class,
                () -> resourceService.updateFileInPost(file, postId));
    }

    @Test
    @DisplayName("testRemoveFileInPost_Success")
    public void testRemoveFileInPost_Success() {
        when(resourceRepository.findById(resourceId)).thenReturn(Optional.of(resource));

        resourceService.removeFileInPost(resourceId);

        verify(minioImageManager, times(1)).removeFileInStorage(key);
        verify(resourceRepository, times(1)).deleteById(resourceId);
    }
}
