package faang.school.postservice.service.s3;

import faang.school.postservice.model.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@Service
public interface S3Service {

    Resource uploadFile(MultipartFile file, String folder);

    List<Resource> uploadFiles(List<MultipartFile> files, String folder);

    void deleteFile(String key);

    InputStream downloadFile(String key);
}
