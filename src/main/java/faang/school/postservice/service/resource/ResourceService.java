package faang.school.postservice.service.resource;

import faang.school.postservice.dto.resource.ResourceDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ResourceService {
    List<ResourceDto> uploadResourcesForPost(List<MultipartFile> files, Long postId);

    void updatePostResources(Long postId, List<MultipartFile> newFiles, List<Long> filesToDelete);

    List<ResourceDto> getResourcesByPostId(Long postId);

    void deletePostResources(Long postId);
}
