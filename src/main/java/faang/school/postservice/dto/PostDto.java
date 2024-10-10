package faang.school.postservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDto {
    private Long id;
    @NotBlank
    private String content;
    private Long authorId;
    private Long projectId;
    private boolean published;
    private LocalDateTime publishedAt;
    private LocalDateTime scheduledAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean deleted;
    private long likesCount;
    private long views;
}
