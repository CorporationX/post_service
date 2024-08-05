package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class DraftPostDto {
    private Long authorId;
    private Long projectId;
    @NotBlank
    @NotNull
    private String content;
    private List<MultipartFile> media;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime scheduledAt;
}
