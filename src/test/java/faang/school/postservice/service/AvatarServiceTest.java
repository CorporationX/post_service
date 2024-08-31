package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserProfilePicDto;
import faang.school.postservice.service.s3.MinioS3Client;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AvatarServiceTest {

    @InjectMocks
    private AvatarService avatarService;

    @Mock
    private MinioS3Client minioS3Client;

    @Mock
    private UserServiceClient userServiceClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("test that saveAvatar() throws IllegalArgumentException when file too large")
    void testSaveAvatarThrowsException() {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getSize()).thenReturn(6 * 1024 * 1024L); // Set file size to 6 MB

        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> avatarService.saveAvatar(1L, mockFile)
        );

        assertEquals("File size exceeds 5 MB", thrown.getMessage());
    }

    @Test
    @DisplayName("test that saveAvatar() throws RuntimeException when IOException occurs")
    void testSaveAvatarThrowsRuntimeException() throws Exception {
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getSize()).thenReturn(1024L);
        when(mockFile.getInputStream()).thenThrow(new IOException("mock IO exception"));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> avatarService.saveAvatar(1L, mockFile)
        );

        assertEquals("java.io.IOException: mock IO exception", thrown.getMessage());
    }

    @Test
    @DisplayName("test that getAvatar() throws RuntimeException when IOException occurs")
    void testGetAvatarThrowsRuntimeException() throws Exception {
        InputStream mockInputStream = mock(InputStream.class);
        when(minioS3Client.downloadFile(anyString())).thenReturn(mockInputStream);
        when(mockInputStream.read(any(byte[].class))).thenThrow(new IOException("mock IO exception"));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> avatarService.getAvatar("testKey")
        );

        assertEquals("java.io.IOException: mock IO exception", thrown.getMessage());
    }

    @Test
    @DisplayName("test that deleteAvatar() run successful")
    void testDeleteAvatar() {
        UserProfilePicDto mockUserProfilePicDto = new UserProfilePicDto("fileId", "smallFileId");
        when(userServiceClient.getAvatarKeys(anyLong())).thenReturn(mockUserProfilePicDto);

        avatarService.deleteAvatar(1L);

        verify(userServiceClient, times(1)).getAvatarKeys(1L);
        verify(userServiceClient, times(1)).deleteAvatar(1L);
        verify(minioS3Client, times(1)).deleteFIle("fileId", "smallFileId");
    }
}