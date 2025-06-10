package faang.school.postservice.service.resource;

import faang.school.postservice.model.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface ResourceService {
    Resource addBuildForPost(long postId, MultipartFile file);

    void deleteImageByPostId(long postId, long resourceId);
}
