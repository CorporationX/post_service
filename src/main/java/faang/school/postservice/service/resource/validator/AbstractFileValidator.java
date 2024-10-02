package faang.school.postservice.service.resource.validator;

import faang.school.postservice.exception.FileException;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.ResourceEntity;
import faang.school.postservice.model.ResourceType;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequiredArgsConstructor
public abstract class AbstractFileValidator implements FileValidator {

    private final long maxSize;
    private final int maxInPost;

    @Override
    public void validateSize(MultipartFile file) {
        if (file.getSize() > maxSize) {
            throw new FileException(file.getOriginalFilename() + " " + file.getContentType()
                    + ". Exceeded size of " + maxSize + " byte");
        }
    }

    @Override
    public void validateAmount(ResourceType type, Post post) {
        List<ResourceEntity> resourceEntities = post.getResourceEntities();
        long amountByType = resourceEntities.stream()
                .filter(resource -> resource.getType().equals(type))
                .count();
        if (amountByType >= maxInPost) {
            throw new FileException("The number of " + type.name() +
                    " cannot be more than " + maxInPost);
        }
    }
}
