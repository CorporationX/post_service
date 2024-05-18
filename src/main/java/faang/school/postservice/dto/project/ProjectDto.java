package faang.school.postservice.dto.project;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjectDto {
    private long id;
    private String title;
}
