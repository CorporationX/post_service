package faang.school.postservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class S3ServiceImplTest {

    @Mock
    private AmazonS3 s3Client;

    @InjectMocks
    private S3ServiceImpl s3Service;

    @Test
    void testUploadFile() {
        MultipartFile multipartFile = new MockMultipartFile("file", "image", "image/jpeg", new byte[1024]);

        s3Service.uploadFile(multipartFile, anyString());

        verify(s3Client, times(1)).putObject(any());
    }

    @Test
    void testDeleteFile() {
        s3Service.deleteFile("image");

        verify(s3Client, times(1)).deleteObject(any(), eq("image"));
    }

    @Test
    void testDownloadFile() {
        S3Object s3Object = new S3Object();

        when(s3Client.getObject(any(), anyString())).thenReturn(s3Object);

        s3Service.downloadFile("image");

        verify(s3Client, times(1)).getObject(any(), anyString());
    }
}