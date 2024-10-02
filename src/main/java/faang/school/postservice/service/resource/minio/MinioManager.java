package faang.school.postservice.service.resource.minio;

import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface MinioManager {
    Resource addFileToStorage(MultipartFile file, Post post);

    Resource updateFileInStorage(String key, MultipartFile newFile, Post post);

    void removeFileInStorage(String key);
}
