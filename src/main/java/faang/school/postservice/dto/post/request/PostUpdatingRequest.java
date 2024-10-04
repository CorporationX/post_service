package faang.school.postservice.dto.post.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Builder
public record PostUpdatingRequest(
        @NotBlank(message = "Post content can't be null or empty")
        String content,

        List<Long> filesToDeleteIds,

        List<MultipartFile> filesToAdd
) {
}