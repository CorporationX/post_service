package faang.school.postservice.dto.image;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageDownloadDto {
    @NotNull
    private Resource resource;
    @NotBlank
    private String originalFileName;
    @NotBlank
    private MediaType contentType;
}
