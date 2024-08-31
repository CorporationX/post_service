package faang.school.postservice.dto.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class PreviewPostResourceDto {
    private Long id;
    private String name;
}
