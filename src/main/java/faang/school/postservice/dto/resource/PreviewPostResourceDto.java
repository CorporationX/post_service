package faang.school.postservice.dto.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class PreviewPostResourceDto {
    private final Long id;
    private final String name;
}
