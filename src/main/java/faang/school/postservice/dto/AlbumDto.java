package faang.school.postservice.dto;

import faang.school.postservice.model.AlbumVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AlbumDto {

    private Long id;

    @NotNull(message = "Title must not be null")
    @NotBlank(message = "Title must not be blank")
    @Size(max = 128, message = "Title must not exceed 128 characters")
    private String title;

    @NotNull(message = "Description must not be null")
    @NotBlank(message = "Description must not be blank")
    @Size(max = 4096, message = "Description must not exceed 4096 characters")
    private String description;

    private Long authorId;

    private AlbumVisibility visibility;

    private List<Long> postIds;

    private List<Long> favouriteUserIds;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}