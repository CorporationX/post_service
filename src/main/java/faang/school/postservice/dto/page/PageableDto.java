package faang.school.postservice.dto.page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PageableDto {
    private PageSortDto sort;
    private long offset;
    private int pageNumber;
    private int pageSize;
    private boolean paged;
    private boolean unpaged;
}

