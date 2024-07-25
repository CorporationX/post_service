package faang.school.postservice.dto;

import faang.school.postservice.model.Post;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Builder
public class ResourceDto {
    private long id;
    private long size;
    private String key;
    private LocalDateTime createdAt;
    private String name;
    private String type;
    private long postId;
}
