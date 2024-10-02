package faang.school.postservice.service.resource;

import faang.school.postservice.exception.FileException;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.model.ResourceType;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.resource.minio.MinioAudioManager;
import faang.school.postservice.service.resource.minio.MinioImageManager;
import faang.school.postservice.service.resource.minio.MinioVideoManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ResourceServiceTest {
    ResourceRepository resourceRepository = mock(ResourceRepository.class);
    PostRepository postRepository = mock(PostRepository.class);
    MinioImageManager minioImageManager = mock(MinioImageManager.class);
    MinioAudioManager minioAudioManager = mock(MinioAudioManager.class);
    MinioVideoManager minioVideoManager = mock(MinioVideoManager.class);

    ResourceService resourceService = new ResourceService(
            resourceRepository,
            postRepository,
            minioImageManager,
            minioAudioManager,
            minioVideoManager
    );

    List<String> allowedImageTypes = new ArrayList<>() {{
        add("image/jpeg");
    }};

    List<String> allowedAudioTypes = new ArrayList<>() {{
        add("audio/mpeg");
    }};

    List<String> allowedVideoTypes = new ArrayList<>() {{
        add("video/mpeg");
    }};


    MultipartFile file = mock(MultipartFile.class);
    Long postId;
    Post post;

    Long resourceOneId;
    Long resourceTwoId;
    String keyOne;
    Resource resourceOne;
    Resource resourceTwo;
    Resource resourceNew;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(resourceService, "allowedImageTypes", allowedImageTypes);
        ReflectionTestUtils.setField(resourceService, "allowedAudioTypes", allowedAudioTypes);
        ReflectionTestUtils.setField(resourceService, "allowedVideoTypes", allowedVideoTypes);
        ReflectionTestUtils.setField(resourceService, "maxImageInPost", 2);
        ReflectionTestUtils.setField(resourceService, "maxImageSize", 5242880);
        resourceOneId = 10L;
        keyOne = "res1";
        resourceOne = Resource.builder()
                .key(keyOne)
                .id(resourceOneId)
                .type(ResourceType.IMAGE)
                .build();

        resourceTwoId = 11L;
        resourceTwo = Resource.builder()
                .id(resourceTwoId)
                .type(ResourceType.AUDIO)
                .build();

        resourceNew = Resource.builder()
                .type(ResourceType.IMAGE)
                .build();


        postId = 1L;
        post = Post.builder()
                .id(postId)
                .resources(new ArrayList<>() {{
                    add(resourceOne);
                    add(resourceTwo);
                }})
                .build();
    }

    @Test
    @DisplayName("testAddFileToPost_Success")
    public void testAddFileToPost_Success() {
        when(file.getContentType()).thenReturn("image/jpeg");
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(minioImageManager.addFileToStorage(file, post)).thenReturn(resourceNew);

        resourceService.addFileToPost(file, postId);

        verify(resourceRepository).save(resourceNew);
    }

    @Test
    @DisplayName("testMimeToResType_Invalid")
    public void testMimeToResType_Invalid() {
        when(file.getContentType()).thenReturn("image/bmp");

        assertThrows(FileException.class,
                () -> resourceService.addFileToPost(file, postId));
    }

    @Test
    @DisplayName("testValidateSize_Invalid")
    public void testValidateSize_Invalid() {
        when(file.getContentType()).thenReturn("image/jpeg");
        when(file.getSize()).thenReturn(5242881L);

        assertThrows(FileException.class,
                () -> resourceService.addFileToPost(file, postId));
    }

    @Test
    @DisplayName("testFindPostById_Invalid")
    public void testFindPostById_Invalid() {
        when(file.getContentType()).thenReturn("image/jpeg");

        assertThrows(NoSuchElementException.class,
                () -> resourceService.addFileToPost(file, postId));
    }

    @Test
    @DisplayName("testValidateFileAmount_Invalid")
    public void testValidateFileAmount_Invalid() {
        post.getResources().add(Resource.builder()
                .type(ResourceType.IMAGE)
                .build());
        when(file.getContentType()).thenReturn("image/jpeg");
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        assertThrows(IllegalArgumentException.class,
                () -> resourceService.addFileToPost(file, postId));
    }

    @Test
    @DisplayName("testUpdateFileInPost_Success")
    public void testUpdateFileInPost_Success() {
        when(file.getContentType()).thenReturn("image/jpeg");
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(minioImageManager.updateFileInStorage(keyOne, file, post)).thenReturn(resourceNew);

        resourceService.updateFileInPost(file, resourceOneId, postId);

        verify(resourceRepository, times(1)).save(resourceNew);
    }

    @Test
    @DisplayName("testRemoveFileInPost_Success")
    public void testRemoveFileInPost_Success() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        resourceService.removeFileInPost(resourceOneId, postId);

        verify(minioImageManager, times(1)).removeFileInStorage(keyOne);
        verify(resourceRepository, times(1)).deleteById(resourceOneId);
    }
}
