package faang.school.postservice.dto;

import faang.school.postservice.model.Post;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class ResourceDto {
    private long id;
    private long size;
    private LocalDateTime createdAt;
    private String name;
    private String type;
    private long postId;
}
