package faang.school.postservice.util.service;

import faang.school.postservice.service.s3.PresignService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.net.MalformedURLException;
import java.net.URI;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PresignServiceTest {

    @Mock
    private S3Presigner s3Presigner;

    @InjectMocks
    private PresignService presignService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        try {
            java.lang.reflect.Field field = PresignService.class.getDeclaredField("bucketName");
            field.setAccessible(true);
            field.set(presignService, "my-test-bucket");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGeneratePresignedUrl_WithCustomDuration() throws MalformedURLException {
        String key = "path/to/object.png";
        Duration duration = Duration.ofMinutes(15);
        String expectedUrl = "https://example.com/presigned-url";

        PresignedGetObjectRequest mockedPresigned = mock(PresignedGetObjectRequest.class);
        when(mockedPresigned.url()).thenReturn(URI.create(expectedUrl).toURL());

        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                .thenReturn(mockedPresigned);

        String actualUrl = presignService.generatePresignedUrl(key, duration);

        assertEquals(expectedUrl, actualUrl);

        ArgumentCaptor<GetObjectPresignRequest> captor = ArgumentCaptor.forClass(GetObjectPresignRequest.class);
        verify(s3Presigner, times(1)).presignGetObject(captor.capture());

        GetObjectPresignRequest passedPresignRequest = captor.getValue();
        GetObjectRequest gor = passedPresignRequest.getObjectRequest();
        assertEquals("my-test-bucket", gor.bucket());
        assertEquals(key, gor.key());
        assertEquals(duration, passedPresignRequest.signatureDuration());
    }

    @Test
    void testGeneratePresignedUrl_DefaultDuration() throws MalformedURLException {
        String key = "another/object.txt";
        Duration defaultDuration = Duration.ofHours(1);
        String expectedUrl = "https://example.com/default-url";

        PresignedGetObjectRequest mockedPresigned = mock(PresignedGetObjectRequest.class);
        when(mockedPresigned.url()).thenReturn(URI.create(expectedUrl).toURL());

        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                .thenReturn(mockedPresigned);

        String actualUrl = presignService.generatePresignedUrl(key);

        assertEquals(expectedUrl, actualUrl);

        ArgumentCaptor<GetObjectPresignRequest> captor = ArgumentCaptor.forClass(GetObjectPresignRequest.class);
        verify(s3Presigner).presignGetObject(captor.capture());
        assertEquals(defaultDuration, captor.getValue().signatureDuration());
        GetObjectRequest gor = captor.getValue().getObjectRequest();
        assertEquals("my-test-bucket", gor.bucket());
        assertEquals(key, gor.key());
    }

    @Test
    void testGeneratePresignedUrl_NullKey_ThrowsException() {
        assertThrows(Exception.class, () -> presignService.generatePresignedUrl(null));
    }
}
