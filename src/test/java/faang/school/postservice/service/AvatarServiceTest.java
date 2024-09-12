package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserProfilePicDto;
import faang.school.postservice.service.s3.MinioS3Client;
import faang.school.postservice.validator.AvatarValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AvatarServiceTest {

    @InjectMocks
    private AvatarService avatarService;

    @Mock
    private MinioS3Client minioS3Client;

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private AvatarValidator avatarValidator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("test that saveAvatar() throws RuntimeException when IOException occurs")
    void testSaveAvatarThrowsRuntimeException() {
        doNothing().when(avatarValidator).validateFileSize(any());
        MultipartFile mockFile = mock(MultipartFile.class);
        when(mockFile.getSize()).thenReturn(1024L);
        try {
            when(mockFile.getInputStream()).thenThrow(new IOException("mock IO exception"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> avatarService.saveAvatar(1L, mockFile)
        );
    }

    @Test
    @DisplayName("test that getAvatar() throws RuntimeException when IOException occurs")
    void testGetAvatarThrowsRuntimeException() {
        InputStream mockInputStream = mock(InputStream.class);
        when(minioS3Client.downloadFile(anyString())).thenReturn(mockInputStream);

        avatarService.getAvatar("testKey");

        verify(minioS3Client).downloadFile(anyString());
    }

    @Test
    @DisplayName("test that deleteAvatar() run successful")
    void testDeleteAvatar() {
        UserProfilePicDto mockUserProfilePicDto = new UserProfilePicDto("fileId", "smallFileId");
        when(userServiceClient.getAvatarKeys(anyLong())).thenReturn(mockUserProfilePicDto);
        doNothing().when(userServiceClient).deleteAvatar(anyLong());
        doNothing().when(minioS3Client).deleteFIle(anyString(), anyString());

        avatarService.deleteAvatar(1L);

        verify(userServiceClient).getAvatarKeys(1L);
        verify(userServiceClient).deleteAvatar(1L);
        verify(minioS3Client).deleteFIle("fileId", "smallFileId");
    }
}