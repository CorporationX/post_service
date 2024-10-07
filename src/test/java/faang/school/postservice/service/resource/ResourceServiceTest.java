package faang.school.postservice.service.resource;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.post.resources.ImageProcessor;
import faang.school.postservice.service.s3.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResourceServiceTest {
    private static final long FILE_MAX_SIZE = 1024;
    @Mock
    private ResourceRepository resourceRepository;

    @Mock
    private S3Service s3Service;

    @Mock
    private ImageProcessor imageProcessor;

    @InjectMocks
    private ResourceServiceImpl resourceService;

    private Post post;

    @BeforeEach
    void setUp() {
        resourceService.setMaxFileSize(FILE_MAX_SIZE);
        post = Post.builder()
                .id(1L)
                .content("content")
                .build();
    }

    @Test
    @DisplayName("Add resources to post")
    void resourceServiceTest_addResourceToPost() {
        List<MultipartFile> files = List.of(
                initFile("file1.jpeg", "image/jpeg", new byte[16]),
                initFile("file2.jpeg", "image/png", new byte[32]));
        try {
            when(imageProcessor.processImage(files.get(0))).thenReturn(files.get(0).getBytes());
            when(imageProcessor.processImage(files.get(1))).thenReturn(files.get(1).getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<Resource> result = resourceService.addResourcesToPost(files, post);
        List<String> expectedNames = files.stream()
                .map(MultipartFile::getOriginalFilename)
                .toList();
        List<String> resultNames = result.stream()
                .map(Resource::getName)
                .toList();

        verify(imageProcessor, times(2)).processImage(any(MultipartFile.class));
        verify(resourceRepository).saveAll(any());
        verify(s3Service, times(files.size())).uploadFile(any(), any(), any());
        assertEquals(files.size(), result.size());
        assertEquals(expectedNames, resultNames);
    }

    @Test
    @DisplayName("Add empty files list to post")
    void resourceServiceTest_addEmptyFilesListToPost() {
        List<MultipartFile> files = new ArrayList<>();

        List<Resource> result = resourceService.addResourcesToPost(files, post);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Add files exceeding max size to post")
    void resourceServiceTest_addFilesExceedingMaxSizeToPost() {
        List<MultipartFile> files = List.of(
                initFile("file1.jpeg", "image/jpeg", new byte[10]),
                initFile("file2.jpeg", "image/png", new byte[(int) FILE_MAX_SIZE + 1]));

        assertThrows(DataValidationException.class, () -> resourceService.addResourcesToPost(files, post));
    }

    @Test
    @DisplayName("Add files to post with null arguments")
    void resourceServiceTest_addFilesToPostWithNullArguments() {
        assertAll(
                () -> assertThrows(NullPointerException.class,
                        () -> resourceService.addResourcesToPost(null, post)),
                () -> assertThrows(NullPointerException.class,
                        () -> resourceService.addResourcesToPost(List.of(), null)));
    }

    @Test
    @DisplayName("Delete files from post")
    void resourceServiceTest_deleteFilesFromPost() {
        List<Long> ids = List.of(1L, 2L);
        List<Resource> resources = List.of(
                initResource(1L, post),
                initResource(2L, post));
        when(resourceRepository.findAllById(ids)).thenReturn(resources);

        resourceService.deleteResourcesFromPost(ids, post.getId());

        verify(resourceRepository).deleteAll(resources);
        verify(s3Service, times(ids.size())).deleteFile(any());
    }

    @Test
    @DisplayName("Delete empty list of files from post")
    void resourceServiceTest_deleteEmptyFilesFromPost() {
        when(resourceRepository.findAllById(any())).thenReturn(new ArrayList<>());

        assertDoesNotThrow(() -> resourceService.deleteResourcesFromPost(new ArrayList<>(), post.getId()));
    }

    @Test
    @DisplayName("Delete files from another post")
    void resourceServiceTest_deleteFilesFromAnotherPost() {
        Post anotherPost = Post.builder()
                .id(2L)
                .content("content")
                .build();
        List<Long> ids = List.of(1L, 2L);
        List<Resource> resources = List.of(
                initResource(1L, anotherPost),
                initResource(2L, post));
        when(resourceRepository.findAllById(ids)).thenReturn(resources);

        assertThrows(DataValidationException.class, () -> resourceService.deleteResourcesFromPost(ids, post.getId()));
    }

    @Test
    @DisplayName("Delete some non-existing files from post")
    void resourceServiceTest_deleteSomeNonExistingFilesFromPost() {
        List<Long> ids = List.of(1L, 2L);
        List<Resource> resources = List.of(
                initResource(2L, post));
        when(resourceRepository.findAllById(ids)).thenReturn(resources);
        String expectedMessage = "Resources with ids 1 not found";

        DataValidationException ex = assertThrows(DataValidationException.class,
                () -> resourceService.deleteResourcesFromPost(ids, post.getId()));

        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    @DisplayName("Delete files from post with null arguments")
    void resourceServiceTest_deleteFilesFromPostWithNullArguments() {
        assertAll(
                () -> assertThrows(NullPointerException.class,
                        () -> resourceService.deleteResourcesFromPost(null, post.getId())),
                () -> assertThrows(NullPointerException.class,
                        () -> resourceService.deleteResourcesFromPost(new ArrayList<>(), null)));
    }

    private MultipartFile initFile(String fileName, String contentType, byte[] content) {
        return new MockMultipartFile(
                "test",
                fileName,
                contentType,
                content);
    }

    private Resource initResource(Long id, Post post) {
        return Resource.builder()
                .id(id)
                .name("test")
                .post(post)
                .build();
    }
}
