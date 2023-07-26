package faang.school.postservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PostDto {
    private Long id;

    @NotNull
    @NotBlank
    private String content;
    private Long authorId;
    private Long projectId;
    private Integer likes;
    private LocalDateTime scheduledAt;
    private LocalDateTime createdAt;
}