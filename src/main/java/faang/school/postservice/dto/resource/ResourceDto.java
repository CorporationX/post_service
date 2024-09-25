package faang.school.postservice.dto.resource;

import faang.school.postservice.model.ResourceType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResourceDto {
    private Long resourceId;
    private Long postId;
    private String name;
    private ResourceType type;
}
