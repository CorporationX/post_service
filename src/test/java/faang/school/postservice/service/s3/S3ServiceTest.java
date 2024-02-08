package faang.school.postservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import faang.school.postservice.model.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {
    @Mock
    private AmazonS3 clientAmazonS3;
    @InjectMocks
    private S3Service s3Service;

    @Test
    void uploadFile_Success() throws Exception {
        // Создаем тестовый файл
        InputStream inputStream = mock(InputStream.class);
        int availableStream = inputStream.available();
        MultipartFile file = new MockMultipartFile("file.txt", "file.txt", "text/plain", inputStream);

        // Вызываем метод и проверяем результат
        Resource result = s3Service.uploadFile(file, "test_folder");

        // Проверяем, что putObject был вызван
        verify(clientAmazonS3).putObject(any());

        // Проверяем, что возвращаемый ресурс содержит правильные данные
        assertEquals("test_folder/file.txt", result.getKey());
        assertEquals("file.txt", result.getName());
        assertEquals("text/plain", result.getType());
        assertEquals(availableStream, result.getSize());
        inputStream.close();
    }

    @Test
    void testUploadFileWithIOException() {
        assertThrows(IOException.class, () -> {
            InputStream inputStream = new FileInputStream("C:\\Users\\Kergshi\\Pictures\\kitt.jpg");
            MultipartFile file = new MockMultipartFile("file.txt", "file.txt", "text/plain", inputStream);
            s3Service.uploadFile(file, "test_folder");
            inputStream.close();
        });
    }

    @Test
    void testDeleteFileCallsDeleteObject() {
        s3Service.deleteFile("test_folder/file.txt");
        verify(clientAmazonS3, times(1)).deleteObject(null, "test_folder/file.txt");
    }
}