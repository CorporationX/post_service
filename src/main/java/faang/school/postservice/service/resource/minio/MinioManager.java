package faang.school.postservice.service.resource.minio;

import faang.school.postservice.model.Post;
import faang.school.postservice.model.ResourceEntity;
import org.springframework.web.multipart.MultipartFile;

public interface MinioManager {
    ResourceEntity addFileToStorage(MultipartFile file, Post post);

    ResourceEntity updateFileInStorage(String key, MultipartFile newFile, Post post);

    void removeFileInStorage(String key);
}
