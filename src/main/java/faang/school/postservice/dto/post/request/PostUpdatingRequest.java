package faang.school.postservice.dto.post.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Builder
public record PostUpdatingRequest(
        @NotBlank(message = "Post content can't be null or empty")
        String content,

        @Size(max = 10, message = "Post can't contain more than 10 resources")
        List<Long> filesToDeleteIds,

        @Size(max = 10, message = "Can't add more than 10 resources to post")
        List<MultipartFile> filesToAdd
) {
}