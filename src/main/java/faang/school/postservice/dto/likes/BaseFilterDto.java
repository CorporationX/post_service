package faang.school.postservice.dto.likes;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BaseFilterDto {
    private int page;
    private int count;
}
