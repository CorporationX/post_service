package faang.school.postservice.service;

import faang.school.postservice.config.AwsS3ApiConfig;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.service.aws.S3Service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FileServiceTest {
    @Mock
    private AwsS3ApiConfig awsS3ApiConfig;

    @Mock
    private S3Service s3Service;

    @Mock
    private PostService postService;

    @InjectMocks
    private FileService fileService;

    @Test
    void testUploadFiles() throws IOException {
        Long postId = 1L;
        MultipartFile file = mock(MultipartFile.class);
        when(file.getSize()).thenReturn(1024L);
        when(file.getContentType()).thenReturn("image/png");
        when(file.getOriginalFilename()).thenReturn("test.png");
        when(file.getBytes()).thenReturn(new byte[]{1, 2, 3});
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[]{1, 2, 3}));

        PutObjectResponse putObjectResponse = PutObjectResponse.builder()
                .eTag(UUID.randomUUID().toString())
                .build();
        when(s3Service.uploadFileAsync(
                anyString(),
                anyString(),
                anyMap(),
                any(byte[].class)
        )).thenReturn(CompletableFuture.completedFuture(putObjectResponse));

        Post post = mock(Post.class);
        when(post.getResources()).thenReturn(Collections.emptyList());
        when(postService.get(postId)).thenReturn(post);

        //cannot be verified because of random uuid generation
        List<String> result = fileService.uploadFiles(postId, Collections.singletonList(file));

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(s3Service, times(1)).uploadFileAsync(
                anyString(),
                anyString(),
                anyMap(),
                any(byte[].class)
        );
        verify(postService, times(1)).update(any(Post.class));
    }

    @Test
    void testDeleteFiles() {
        List<String> fileIds = Arrays.asList("file1", "file2");
        when(awsS3ApiConfig.getBucket()).thenReturn("test-bucket");
        when(s3Service.deleteFileAsync(anyString(), anyString()))
                .thenReturn(CompletableFuture.completedFuture(null));

        Post post = mock(Post.class);
        Resource resource = mock(Resource.class);
        when(resource.getKey()).thenReturn("file1");
        when(post.getResources()).thenReturn(Collections.singletonList(resource));
        when(postService.findPostsByResourceKeys(fileIds)).thenReturn(Collections.singletonList(post));

        fileService.deleteFiles(fileIds);

        verify(s3Service, times(2)).deleteFileAsync(anyString(), anyString());
        verify(postService, times(1)).update(any(Post.class));
    }

    @Test
    void testGetPresignedUrl() {
        String fileId = "file1";
        String presignedUrl = "http://example.com/presigned-url";
        when(awsS3ApiConfig.getBucket()).thenReturn("test-bucket");
        when(s3Service.createPresignedGetUrl(anyString(), anyString())).thenReturn(presignedUrl);

        String result = fileService.getPresignedUrl(fileId);

        assertEquals(presignedUrl, result);
        verify(s3Service, times(1)).createPresignedGetUrl(anyString(), anyString());
    }

    @Test
    void testGetObjectBytes() {
        String fileId = "file1";
        byte[] fileBytes = new byte[]{1, 2, 3};
        when(awsS3ApiConfig.getBucket()).thenReturn("test-bucket");
        when(s3Service.getObjectBytes(anyString(), anyString())).thenReturn(fileBytes);

        byte[] result = fileService.getObjectBytes(fileId);

        assertArrayEquals(fileBytes, result);
        verify(s3Service, times(1)).getObjectBytes(anyString(), anyString());
    }
}
