package faang.school.postservice.dto.like;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LikeEvent {
    @Positive
    private Long id;
    @NotNull
    private Long authorLikeId;
    @Positive
    private Long authorPostId;
    @Positive
    private Long postId;
    @Positive
    private Long authorCommentId;
    @Positive
    private Long commentId;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;
}