package faang.school.postservice.service.resource;

import faang.school.postservice.model.resource.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ResourceService {
    List<Resource> uploadResourcesForPost(List<MultipartFile> files, Long postId);

    void updatePostResources(Long postId, List<MultipartFile> newFiles, List<Long> filesToDelete);

    List<Resource> getResourcesByPostId(Long postId);

    void deletePostResources(Long postId);
}
