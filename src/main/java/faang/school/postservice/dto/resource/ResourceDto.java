package faang.school.postservice.dto.resource;

import lombok.*;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class ResourceDto {
    Long id;
    String key;
    String name;
    long size;
    String type;
}
