package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class DraftPostDto {
    private Long authorId;
    private Long projectId;
    @NotBlank(
            message = "The content of the post cannot be empty or consist of white separators."
    )
    @NotEmpty(
            message = "The content of the post cannot be empty or consist of white separators."
    )
    private String content;
    @Size(
            max = 10,
            message = "The maximum number of files in a draft has been exceeded."
    )
    private List<MultipartFile> resource;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime scheduledAt;
}
