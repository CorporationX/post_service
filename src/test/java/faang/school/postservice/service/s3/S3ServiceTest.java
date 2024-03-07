package faang.school.postservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.postservice.model.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {
    @Mock
    private AmazonS3 clientAmazonS3;

    @Mock
    private PutObjectRequest putObjectRequest;
    @InjectMocks
    private S3Service s3Service;

    @Test
    void testUploadFileWithIOException() {
        assertThrows(IOException.class, () -> {
            InputStream inputStream = new FileInputStream("C:\\Users\\Kergshi\\Pictures\\kitt.jpg");
            MultipartFile file = new MockMultipartFile("file.txt", "file.txt", "text/plain", inputStream);
            s3Service.uploadFile(file);
            inputStream.close();
        });
    }

    @Test
    void testDeleteFileCallsDeleteObject() {
        s3Service.deleteFile("test_folder/file.txt");
        verify(clientAmazonS3, times(1)).deleteObject(null, "test_folder/file.txt");
    }
}