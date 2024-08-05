package faang.school.postservice.dto.post;

import faang.school.postservice.exception.exceptionmessages.ValidationExceptionMessage;
import faang.school.postservice.exception.validation.DataValidationException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @NotBlank(
            message = "The content of the post cannot consist of white separators.",
            groups = DataValidationException.class
    )
    @NotEmpty(
            message = "The content of the post cannot be empty.",
            groups = DataValidationException.class
    )
    private String content;
    @Size(
            max = 10,
            message = "The maximum number of files in a draft has been exceeded.",
            groups = DataValidationException.class
    )
    private List<MultipartFile> media;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime scheduledAt;
}
