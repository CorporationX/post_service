package faang.school.postservice.model.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PostEvent implements Serializable {
    private Long id;
    private String content;
    private Long authorId;
    private Long projectId;
    private List<Long> likes;
    private List<Long> comments;
    private List<Long> albums;
    private Long ad;
    private List<Long> resources;
    private boolean published;
    private boolean aiChecked;
    private LocalDateTime publishedAt;
    private LocalDateTime scheduledAt;
    private boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
