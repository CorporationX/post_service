package faang.school.postservice.dto.album;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlbumDto {

  @Positive
  private Long id;
  @NotBlank
  private String title;
  @NotBlank
  private String description;
  @NotNull
  private Long authorId;
  private List<Long> postIds;
  private String createdAt;
  private String updatedAt;
}
