package faang.school.postservice.dto.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResourceDto {
    private String name;
    private String key;
    private Long size;
    private String type;
    private Long postId;
    private LocalDateTime createdAt;
}
