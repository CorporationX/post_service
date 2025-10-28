package faang.school.postservice.service.resource;

import faang.school.postservice.dto.resource.ResourceDto;
import faang.school.postservice.model.ImageType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ResourcePostService {

    List<ResourceDto> addResources(long postId, MultipartFile[] files, ImageType type);

    List<String> getResource(Long postId);

    void deleteResource(Long postId, List<Long> resourceIds);
}