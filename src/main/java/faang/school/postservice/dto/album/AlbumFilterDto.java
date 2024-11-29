package faang.school.postservice.dto.album;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlbumFilterDto {

  private String titlePattern;
  private String descriptionPattern;
  private Long authorId;
  private String createdAtFrom;
  private String createdAtTo;
  private int page;
  private int pageSize;
}
