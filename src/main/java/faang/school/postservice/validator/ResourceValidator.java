package faang.school.postservice.validator;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.repository.ResourceRepository;
import faang.school.postservice.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Component
public class ResourceValidator {
    private ResourceService resourceService;

    @Value("${post.content_to_post.max_amount_audio}")
    private long max_amount_audio;

    @Value("${post.content_to_post.max_size_audio}")
    private long max_size_audio;

    @Autowired
    public ResourceValidator(@Lazy ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    public void validateFiles(PostDto postDto, List<MultipartFile> files) {
        List<Long> resourceIds = postDto.getResourceIds();
        long amountAudioInPost = 0;
        if (resourceIds != null) {
            amountAudioInPost = resourceIds.stream()
                    .filter(resource -> resourceService.getResourceById(resource).getType().contains("audio"))
//                    .filter(resource -> resourceRepository.getReferenceById(resource).getType().contains("audio"))
//                    либо использовать Репозиторий и уйти от @Lazy
                    .count();
        }
        long amountAudioInFiles = files.stream()
                .filter(file -> Objects.requireNonNull(file.getContentType()).contains("audio"))
                .count();
        if (amountAudioInFiles + amountAudioInPost > max_amount_audio) {
            throw new DataValidationException("The maximum number of audio files has been reached!");
        }
        if (files.stream()
                .filter(file -> Objects.requireNonNull(file.getContentType()).contains("audio"))
                .anyMatch(file -> file.getSize() > max_size_audio)) {
            throw new DataValidationException("The audio file size limit has been exceeded!");
        }
    }
}
