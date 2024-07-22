package faang.school.postservice.dto;

import faang.school.postservice.model.AlbumVisibility;
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

    private List<Long> postIds;

    @NotNull
    private AlbumVisibility visibility;
    @NotNull
    private List<Long> allowedUserIds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}