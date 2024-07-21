package faang.school.postservice.dto.post;

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

    @Size(min = 0, message = "Page can't be less than 0")
    private int page;

    @Size(min = 0, max = 100, message = "Size of elements is between 0 and 100")
    private int size;
}
