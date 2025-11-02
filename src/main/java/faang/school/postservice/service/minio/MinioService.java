package faang.school.postservice.service.minio;

import faang.school.postservice.model.Resource;

import java.util.List;

public interface MinioService {

    Resource uploadImage(byte[] imageData, String folder, String originalFileName, String contentType);

    List<String> downloadImage(String key, String folder);

    void deleteImage(String key);
}