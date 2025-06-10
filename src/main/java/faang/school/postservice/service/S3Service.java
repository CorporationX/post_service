package faang.school.postservice.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface S3Service {
    String uploadFile(MultipartFile file, String entityName);

    InputStream downloadFile(String fileKey);

    void deleteFile(String fileKey);
}
