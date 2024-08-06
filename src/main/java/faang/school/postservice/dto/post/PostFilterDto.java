package faang.school.postservice.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PostFilterDto {

    private Long projectId;
    private Long authorId;
    private Boolean deleted;
    private Boolean published;
    private SortField sortField;
    private String direction;
    private Integer page;
    private Integer size;
}
