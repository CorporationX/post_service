package faang.school.postservice.dto.client;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProjectDto {

    private Long id;
    private String name;
    private String description;
    private Long ownerId;
    private Long parentId;
    private List<Long> childrenId;
}
