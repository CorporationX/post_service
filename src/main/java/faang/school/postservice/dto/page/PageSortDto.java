package faang.school.postservice.dto.page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PageSortDto {
    private boolean empty;
    private boolean unsorted;
    private boolean sorted;
}