package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class CreatePostDto {
    @NotBlank
    private String content;
    private Long authorId;
    private Long projectId;
    private List<String> hashtags;
    private List<MultipartFile> images;
}
