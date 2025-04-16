package faang.school.postservice.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ResourceDto {

    private Long id;
    private String key;
    private Long size;
    private Instant createdAt;
    private String name;
    private String type;
    private Long postId;
}
