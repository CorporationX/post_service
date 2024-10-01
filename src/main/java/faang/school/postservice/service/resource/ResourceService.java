package faang.school.postservice.service.resource;

import faang.school.postservice.dto.resource.ResourceDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface ResourceService {
    ResourceDto addResource(Long post_id, MultipartFile file);

    void deleteResource(Long resource_id);

    InputStream downloadResource(Long resource_id);
}
