package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserProfilePicDto;
import faang.school.postservice.service.resource.CustomMultipartFile;
import faang.school.postservice.service.s3.MinioS3Client;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.utils.IoUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class AvatarService {
    private final UserServiceClient userServiceClient;
    private final MinioS3Client minioS3Client;

    public void saveAvatar(long userId, MultipartFile file) {
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("File size exceeds 5 MB");
        }

        BufferedImage originalImage;
        try {
            originalImage = ImageIO.read(file.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        ByteArrayOutputStream largeOutputStream = new ByteArrayOutputStream();
        if (originalWidth > 1080 || originalHeight > 1080) {
            try {
                Thumbnails.of(originalImage)
                        .size(1080, 1080)
                        .keepAspectRatio(true)
                        .outputFormat("png")
                        .toOutputStream(largeOutputStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                Thumbnails.of(originalImage)
                        .scale(1)
                        .outputFormat("png")
                        .toOutputStream(largeOutputStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        ByteArrayOutputStream smallOutputStream = new ByteArrayOutputStream();
        if (originalWidth > 170 || originalHeight > 170) {
            try {
                Thumbnails.of(originalImage)
                        .size(170, 170)
                        .keepAspectRatio(true)
                        .outputFormat("png")
                        .toOutputStream(smallOutputStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                Thumbnails.of(originalImage)
                        .scale(1)
                        .outputFormat("png")
                        .toOutputStream(smallOutputStream);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        byte[] largeOutputStreamByteArray = largeOutputStream.toByteArray();
        byte[] smallOutputStreamByteArray = smallOutputStream.toByteArray();

        MultipartFile largeFile = new CustomMultipartFile(largeOutputStreamByteArray, "large_" + file.getName(), "large_" + file.getOriginalFilename(), file.getContentType());
        MultipartFile smallFile = new CustomMultipartFile(smallOutputStreamByteArray, "small_" + file.getName(), "small_" + file.getOriginalFilename(), file.getContentType());

        String fileId;
        String smallFileId;

        try {
            fileId = minioS3Client.uploadFile(largeFile, String.valueOf(userId)).getKey();
            smallFileId = minioS3Client.uploadFile(smallFile, String.valueOf(userId)).getKey();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        saveLargeAndSmallFileId(userId, fileId, smallFileId);
    }

    @Retryable(value = FeignException.FeignClientException.class, backoff = @Backoff(multiplier = 2))
    private void saveLargeAndSmallFileId(long userId, String fileId, String smallFileId) {
        userServiceClient.uploadAvatar(userId, new UserProfilePicDto(fileId, smallFileId));
    }

    public ResponseEntity<byte[]> getAvatar(String key) {
        InputStream avatarInputStream = minioS3Client.downloadFile(key);
        byte[] avatarByteArray;
        try {
            avatarByteArray = IoUtils.toByteArray(avatarInputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String contentType = "";
        Path path = Paths.get(key);
        String originalFileName = path.getFileName().toString();
        try {
            contentType = Files.probeContentType(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(contentType));
        return new ResponseEntity<>(avatarByteArray, headers, HttpStatus.OK);
    }

    public void deleteAvatar(long userId, String fileKey, String smallFileKey) {
        userServiceClient.deleteAvatar(userId);
        minioS3Client.deleteFIle(fileKey, smallFileKey);
    }
}
