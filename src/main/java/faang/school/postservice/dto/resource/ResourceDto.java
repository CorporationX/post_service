package faang.school.postservice.dto.resource;

import lombok.*;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class ResourceDto {
    private Long id;
    private String key;
    private String name;
    private long size;
    private String type;
}
