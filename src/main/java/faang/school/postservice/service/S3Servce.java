package faang.school.postservice.service;

import faang.school.postservice.model.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;

public interface S3Servce {

    String uploadFile(ByteArrayInputStream file, String folder);


    void deleteFile(String key);

    InputStream downloadFile(String fileKey);
}
