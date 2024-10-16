package faang.school.postservice.service;

import faang.school.postservice.model.entity.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface S3Service {
    List<Resource> uploadFiles(List<MultipartFile> files, String folder);

    void deleteFile(String key);
}
