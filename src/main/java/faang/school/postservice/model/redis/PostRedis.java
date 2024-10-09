package faang.school.postservice.model.redis;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Id;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class PostRedis implements Serializable {

    @Id
    private String id;
    private String content;
    private Long authorId;
    private String commentSetId;
    private Long likes;
    private Long views;
    private Long projectId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
