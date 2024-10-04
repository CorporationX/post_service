package faang.school.postservice.service.resource;

import faang.school.postservice.dto.resource.ResourceDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface ResourceService {
    ResourceDto addResource(Long postId, MultipartFile file);

    void deleteResource(Long resourceId);

    ResponseEntity<byte[]> downloadResource(Long resourceId);
}
