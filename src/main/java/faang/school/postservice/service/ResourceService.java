package faang.school.postservice.service;

import faang.school.postservice.model.dto.ResourceDto;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ResourceService {
    List<ResourceDto> attachImages(Long postId, List<MultipartFile> imageFiles);

    ResourceDto deleteResource(Long resourceId);

    ResourceDto restoreResource(Long resourceId);

    void deleteOldDeletedResources();
}
