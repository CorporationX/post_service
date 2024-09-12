package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserProfilePicDto;
import faang.school.postservice.model.Resource;
import faang.school.postservice.service.resource.CustomMultipartFile;
import faang.school.postservice.service.resource.ResizeService;
import faang.school.postservice.service.s3.MinioS3Client;
import faang.school.postservice.validator.AvatarValidator;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class AvatarService {
    private final UserServiceClient userServiceClient;
    private final MinioS3Client minioS3Client;
    private final ResizeService resizeService;
    private final AvatarValidator avatarValidator;

    @Value("${spring.resources.file.max-file-size}")
    private long maxFIleSize;

    @Value("${spring.resources.image.max-side}")
    private int maxSide;

    @Value("${spring.resources.image.min-side}")
    private int minSide;

    @Transactional
    public void saveAvatar(long userId, MultipartFile file) {
        avatarValidator.validateFileSize(file);
        BufferedImage originalImage;
        try {
            originalImage = ImageIO.read(file.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException("error when reading a file " + e);
        }

        MultipartFile largeFile = imageProcessing(file, originalImage, maxSide);
        MultipartFile smallFile = imageProcessing(file, originalImage, minSide);

        String fileId;
        String smallFileId;

        Resource fileResource = minioS3Client.uploadFile(largeFile, String.valueOf(userId));
        Resource smallfileResource = minioS3Client.uploadFile(smallFile, String.valueOf(userId));

        try {
            fileId = fileResource.getKey();
            smallFileId = smallfileResource.getKey();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        deleteAvatar(userId);

        saveLargeAndSmallFileId(userId, fileId, smallFileId);
    }

    private CustomMultipartFile imageProcessing(MultipartFile file, BufferedImage originalImage, int maxSide) {
        ByteArrayOutputStream smallOutputStream = resizeService.squeezeImageOrLeave(originalImage, maxSide);
        byte[] smallOutputStreamByteArray = smallOutputStream.toByteArray();
        return new CustomMultipartFile(smallOutputStreamByteArray, file.getName(), file.getOriginalFilename(), file.getContentType());
    }

    @Retryable(value = FeignException.FeignClientException.class, backoff = @Backoff(multiplier = 2))
    private void saveLargeAndSmallFileId(long userId, String fileId, String smallFileId) {
        userServiceClient.uploadAvatar(userId, new UserProfilePicDto(fileId, smallFileId));
    }

    public InputStreamResource getAvatar(String key) {
        InputStream avatarInputStream = minioS3Client.downloadFile(key);
        InputStreamResource avatarResource = new InputStreamResource(avatarInputStream);

        return avatarResource;
    }

    public void deleteAvatar(long userId) {
        UserProfilePicDto userProfilePicDto = userServiceClient.getAvatarKeys(userId);

        if (!userProfilePicDto.getFileId().isEmpty()) {
            userServiceClient.deleteAvatar(userId);
            minioS3Client.deleteFIle(userProfilePicDto.getFileId(), userProfilePicDto.getSmallFileId());
        }
    }
}
