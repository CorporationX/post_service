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
@Builder
public class LikeEvent {
    @Positive
    private Long id; // TODO: передавать
    @NotNull
    private Long authorLikeId; // TODO: передавать
    @Positive
    private Long authorPostId;
    @Positive
    private Long postId; // TODO: передавать
    @Positive
    private Long authorCommentId;
    @Positive
    private Long commentId;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime createdAt;
}