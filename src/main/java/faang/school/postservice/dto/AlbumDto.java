package faang.school.postservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlbumDto {

    private long id;

    @NotNull
    @NotBlank
    @Size(max = 256)
    private String title;

    @NotNull
    @NotBlank
    @Size(max = 4096)
    private String description;

    @NotNull
    private Long authorId;

    private List<Long> postIds;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}