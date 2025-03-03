package faang.school.postservice.dto.page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class PageDto<T> {
    private List<T> content;
    private PageableDto pageable;
    private boolean last;
    private int totalPages;
    private long totalElements;
    private boolean first;
    private int size;
    private int number;
    private PageSortDto sort;
    private int numberOfElements;
    private boolean empty;
}