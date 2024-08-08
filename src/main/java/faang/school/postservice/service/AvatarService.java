package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.service.resource.CustomMultipartFile;
import faang.school.postservice.service.s3.MinioS3Client;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
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

//        ByteArrayOutputStream largeOutputStream = new ByteArrayOutputStream();
//        if (originalWidth > 1080 || originalHeight > 1080) {
//            try {
//                Thumbnails.of(originalImage)
//                        .size(1080, 1080)
//                        .keepAspectRatio(true)
//                        .toOutputStream(largeOutputStream);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        } else {
//            try {
//                Thumbnails.of(originalImage)
//                        .scale(1)
//                        .toOutputStream(largeOutputStream);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
//
//        ByteArrayOutputStream smallOutputStream = new ByteArrayOutputStream();
//        if (originalWidth > 170 || originalHeight > 170) {
//            try {
//                Thumbnails.of(originalImage)
//                        .size(170, 170)
//                        .keepAspectRatio(true)
//                        .toOutputStream(smallOutputStream);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        } else {
//            try {
//                Thumbnails.of(originalImage)
//                        .scale(1)
//                        .toOutputStream(smallOutputStream);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }

//        byte[] largeOutputStreamByteArray = largeOutputStream.toByteArray();
//        byte[] smallOutputStreamByteArray = smallOutputStream.toByteArray();
//
//        MultipartFile largeFile = new CustomMultipartFile(largeOutputStreamByteArray, "large_" + file.getName(), "large_" + file.getOriginalFilename(), file.getContentType());
//        MultipartFile smallFile = new CustomMultipartFile(smallOutputStreamByteArray, "small_" + file.getName(), "small_" + file.getOriginalFilename(), file.getContentType());

        String largeFileId;
        String smallFileId;

        try {
            largeFileId = minioS3Client.uploadFile(file, String.valueOf(userId)).getKey();
            smallFileId = minioS3Client.uploadFile(file, String.valueOf(userId)).getKey();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        userServiceClient.uploadAvatar(userId, largeFileId, smallFileId);
    }

    public InputStream getAvatar(String key) {
        return minioS3Client.downloadFile(key);
    }

    public void deleteAvatar(long userId, String largeFileKey, String smallFileKey) {
        userServiceClient.deleteAvatar(userId);
        minioS3Client.deleteFIle(largeFileKey, smallFileKey);
    }
}
