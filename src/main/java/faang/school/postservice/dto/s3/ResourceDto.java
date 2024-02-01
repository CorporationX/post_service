package faang.school.postservice.dto.s3;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceDto {
    @NotBlank(message = "Field cannot be blank")
    private String name;
    private String type;
    private long size;
    private MultipartFile file;
}
