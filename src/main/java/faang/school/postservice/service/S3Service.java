package faang.school.postservice.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public interface S3Service {

    String uploadFile(ByteArrayInputStream file, String folder);

    void deleteFile(String key);

    InputStream downloadFile(String fileKey);
}
