package faang.school.postservice.dto.like;

import faang.school.postservice.annotations.ValidateLikeDto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ValidateLikeDto
public class LikeDto {
    @Positive
    private Long id;

    @Positive
    @NotNull
    private Long userId;

    @Positive
    private Long commentId;

    @Positive
    private Long postId;

    @PastOrPresent
    private LocalDateTime createdAt;
}
