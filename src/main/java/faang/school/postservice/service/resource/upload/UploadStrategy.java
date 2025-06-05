package faang.school.postservice.service.resource.upload;

import faang.school.postservice.dto.resource.StoredFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface UploadStrategy {
    boolean supports(String contentType);

    CompletableFuture<List<StoredFile>> upload(List<MultipartFile> files);

    void delete(String key);

    String getType();
}
