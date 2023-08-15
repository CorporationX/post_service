package faang.school.postservice.dto.album;

import faang.school.postservice.model.Visibility;
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
    private Long id;

    @NotBlank(message = "title should not be blank")
    private String title;

    @NotBlank(message = "description should not be blank")
    @Size(max = 255, message = "description length should not exceed 255 characters")
    private String description;
    @NotNull
    private Long authorId;

    @NotNull(message = "postIds should not be null")
    @Size(min = 1, message = "postIds should have at least 1 item")
    private List<Long> postIds;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Visibility visibility;
    private List<Long> allowedUsersIds;
}





