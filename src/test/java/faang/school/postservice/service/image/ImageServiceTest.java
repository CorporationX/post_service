package faang.school.postservice.service.image;

import faang.school.postservice.dto.image.ImageResponseDto;
import faang.school.postservice.service.s3.S3KeyGenerator;
import faang.school.postservice.service.s3.S3Service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @InjectMocks
    private ImageService imageService;

    @Mock
    private S3Service s3Service;

    @Mock
    private S3KeyGenerator s3KeyGenerator;

    @Test
    void uploadToS3_shouldReturnImageResponseDto() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("file.jpg");
        when(file.getContentType()).thenReturn("image/jpeg");
        when(file.getSize()).thenReturn(1234L);
        when(file.getInputStream()).thenReturn(getTestImageStream());

        when(s3KeyGenerator.generateImageKey("file.jpg")).thenReturn("imgKey");
        when(s3KeyGenerator.generatePreviewKey("imgKey")).thenReturn("previewKey");

        ImageResponseDto result = imageService.uploadToS3(file);

        assertEquals("imgKey", result.getFileKey());
        assertEquals("previewKey", result.getPreviewKey());
        assertEquals("image/jpeg", result.getContentType());
        assertEquals(1234L, result.getSize());
        verify(s3Service, times(2)).uploadImageBytesInS3(any(), any(), eq("image/jpeg"));
    }

    @Test
    void download_shouldReturnInputStream() throws Exception {
        InputStream expectedStream = new ByteArrayInputStream(new byte[]{1, 2, 3});
        when(s3Service.download("someKey")).thenReturn(expectedStream);

        InputStream actualStream = imageService.download("someKey");

        assertSame(expectedStream, actualStream);
    }

    @Test
    void delete_shouldCallS3() {
        imageService.delete("toDelete");

        verify(s3Service).delete("toDelete");
    }

    @Test
    void detectContentType_shouldReturnCorrectType() {
        when(s3Service.getContentType("key")).thenReturn("image/png");

        MediaType mediaType = imageService.detectContentType("key");

        assertEquals(MediaType.IMAGE_PNG, mediaType);
    }

    @Test
    void detectContentType_shouldReturnOctetStreamOnFailure() {
        when(s3Service.getContentType("key")).thenThrow(new RuntimeException());

        MediaType mediaType = imageService.detectContentType("key");

        assertEquals(MediaType.APPLICATION_OCTET_STREAM, mediaType);
    }


    private InputStream getTestImageStream() throws Exception {
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", out);
        return new ByteArrayInputStream(out.toByteArray());
    }
}
