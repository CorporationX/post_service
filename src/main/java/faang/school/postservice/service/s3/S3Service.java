package faang.school.postservice.service.s3;

import faang.school.postservice.model.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface S3Service {
    Resource uploadFile(MultipartFile file, String folder);

    void deleteFile(String key);

    InputStream downloadFile(String key);
}
