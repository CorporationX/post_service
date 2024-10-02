package faang.school.postservice.service.resource.validator;

import faang.school.postservice.model.Post;
import faang.school.postservice.model.ResourceType;
import org.springframework.web.multipart.MultipartFile;

public interface FileValidator {

    void validateSize(MultipartFile file);

    void validateAmount(ResourceType type, Post post);
}
