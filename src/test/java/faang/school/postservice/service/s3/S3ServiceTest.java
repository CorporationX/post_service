package faang.school.postservice.service.s3;

import faang.school.postservice.config.s3.S3Properties;
import faang.school.postservice.exception.file.FileNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.http.AbortableInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

import java.io.ByteArrayInputStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {

    @Mock
    private S3Client s3Client;

    @InjectMocks
    private S3Service s3Service;

    @BeforeEach
    void setUp() {
        S3Properties props = new S3Properties();
        props.setBucket("corpbucket");
        ReflectionTestUtils.setField(s3Service, "s3Properties", props);
    }

    @Test
    void shouldDownloadFileFromS3() {
        byte[] content = "test-content".getBytes();

        GetObjectResponse response = GetObjectResponse.builder()
                .contentLength((long) content.length)
                .contentType("image/jpeg")
                .build();

        ResponseInputStream<GetObjectResponse> stream = new ResponseInputStream<>(
                response,
                AbortableInputStream.create(new ByteArrayInputStream(content), () -> {})
        );

        when(s3Client.getObject(any(GetObjectRequest.class))).thenReturn(stream);

        Resource resource = s3Service.download("some-key");

        assertThat(resource).isNotNull();
        assertThat(resource.getFilename()).isEqualTo("some-key");
        verify(s3Client).getObject(any(GetObjectRequest.class));
    }

    @Test
    void shouldThrowIfFileNotFound() {
        when(s3Client.getObject(any(GetObjectRequest.class))).thenThrow(NoSuchKeyException.builder().message("Not found").build());

        assertThrows(FileNotFoundException.class, () -> s3Service.download("missing.jpg"));
    }
}
