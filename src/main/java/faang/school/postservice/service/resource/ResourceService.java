package faang.school.postservice.service.resource;

import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ResourceService {
    List<Resource> addResourcesToPost(List<MultipartFile> files, Post post);

    void deleteResourcesFromPost(List<Long> resourcesIds, Post post);
}
