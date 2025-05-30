package faang.school.postservice.service;

import faang.school.postservice.model.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;

public interface S3Servce {

    Resource uploadFile (MultipartFile file, String folder);

    URL getFileUrl(String fileKey);

    void deleteFile (String key);
}
