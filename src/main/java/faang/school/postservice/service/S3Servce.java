package faang.school.postservice.service;

import faang.school.postservice.model.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;

public interface S3Servce {

    Resource uploadFile (ByteArrayInputStream file, String folder);

//    URL getFileUrl(String fileKey);

    void deleteFile (String key);

    InputStream downloadFile(String fileKey);
}
