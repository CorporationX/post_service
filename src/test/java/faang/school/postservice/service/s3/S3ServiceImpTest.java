package faang.school.postservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.model.Resource;
import faang.school.postservice.repository.ResourceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3ServiceImpTest {
    @InjectMocks
    private S3ServiceImp s3ServiceImp;
    @Mock
    private AmazonS3 s3Client;
    @Mock
    private ResourceRepository resourceRepository;


    @Test
    void testUploadImages() {
        MultipartFile file =
                new MockMultipartFile("file", "test.txt", "text", new byte[0]);

        when(s3Client.putObject(any(), any(), any(), any())).thenReturn(null);

        Resource actual = s3ServiceImp.uploadFiles(file, new byte[0]);

        assertEquals(file.getOriginalFilename(), actual.getName());
        assertEquals(file.getSize(), actual.getSize());
        assertNotNull(actual.getKey());
    }

    @Test
    void testDeleteResource() {
        long deletedFileId = 1L;
        Resource resource = Resource.builder()
                .id(deletedFileId)
                .key("test-key")
                .build();

        when(resourceRepository.findById(deletedFileId)).thenReturn(Optional.of(resource));

        Resource actual = s3ServiceImp.deleteResource(deletedFileId);

        assertEquals(deletedFileId, actual.getId());
        assertEquals("test-key", actual.getKey());
    }

    @Test
    void testGetResource() {
        long deletedFileId = 1L;

        when(resourceRepository.findById(deletedFileId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> s3ServiceImp.deleteResource(deletedFileId));
    }
}
