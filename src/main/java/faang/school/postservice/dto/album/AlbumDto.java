package faang.school.postservice.dto.album;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class AlbumDto extends AlbumDtoResponse {
    private Long id;
    @NotNull
    @NotBlank
    @Size(min = 1, max = 256)
    private String title;
    @NotNull
    @NotBlank
    @Size(min = 1, max = 4096)
    private String description;
    private Long authorId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

