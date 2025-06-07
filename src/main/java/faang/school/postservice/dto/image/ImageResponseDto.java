package faang.school.postservice.dto.image;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageResponseDto {
    @NotBlank
    private String fileKey;
    @NotBlank
    private String previewKey;
    @NotBlank
    private String contentType;
    private long size;
}
