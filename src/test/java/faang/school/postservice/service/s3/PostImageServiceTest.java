package faang.school.postservice.service.s3;

import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.model.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostImageServiceTest {
    @InjectMocks
    private PostImageService postImageService;
    @Mock
    private S3ServiceImp s3ServiceImp;


    @Test
    void testUploadImages() {
        MultipartFile[] files = new MultipartFile[3];
        for (int i = 0; i < files.length; i++) {
            files[i] = new MockMultipartFile("file " + i,
                    "test.txt", "text", new byte[0]);
        }

        when(s3ServiceImp.uploadFiles(any(MultipartFile.class), any(byte[].class))).thenReturn(new Resource());

        List<Resource> actual = postImageService.uploadImages(files);

        assertEquals(3, actual.size());
    }


    @Test
    void testUploadImagesExceedMaxFileCount() {
        MultipartFile[] files = new MultipartFile[11];
        for (int i = 0; i < files.length; i++) {
            files[i] = new MockMultipartFile("file " + i,
                    "test.txt", "text", new byte[0]);
        }

        DataValidationException exception = assertThrows(DataValidationException.class, () -> postImageService.uploadImages(files));
        assertEquals("Max file count is 10", exception.getMessage());
    }

    @Test
    void testDeleteImages() {
        List<Long> deletedImageIds = List.of(1L, 2L);

        when(s3ServiceImp.deleteResource(any(Long.class))).thenReturn(new Resource());

        List<Resource> actual = postImageService.deleteImages(deletedImageIds);

        assertEquals(2, actual.size());
    }
}