package faang.school.postservice.service.image;

import faang.school.postservice.dto.image.ImageResponseDto;
import faang.school.postservice.service.s3.S3KeyGenerator;
import faang.school.postservice.service.s3.S3Service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @Mock
    private S3Service s3Service;

    @Mock
    private S3KeyGenerator s3KeyGenerator;

    @InjectMocks
    private ImageService imageService;

    @Test
    void uploadToS3_shouldProcessAndUploadImage() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("test.jpg");
        when(file.getContentType()).thenReturn("image/jpeg");
        when(file.getSize()).thenReturn(12345L);

        String key = "user_1/originals/...jpg";
        String previewKey = "user_1/previews/...jpg";

        when(s3KeyGenerator.generateImageKey(anyString())).thenReturn(key);
        when(s3KeyGenerator.generatePreviewKey(key)).thenReturn(previewKey);

        BufferedImage testImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
        ImageIO.write(testImage, "jpg", imageStream);

        when(file.getInputStream()).thenReturn(
                new ByteArrayInputStream(imageStream.toByteArray())
        );

        ImageResponseDto response = imageService.uploadToS3(file);

        assertEquals(key, response.getFileKey());
        assertEquals(previewKey, response.getPreviewKey());
        assertEquals("image/jpeg", response.getContentType());
        assertEquals(12345L, response.getSize());

        verify(s3Service, times(2)).upload(any(), any(), eq("image/jpeg"));
    }

    @Test
    void delete_shouldDelegateToS3Service() {
        imageService.delete("key.jpg");

        verify(s3Service).delete("key.jpg");
    }
}
