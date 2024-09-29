package faang.school.postservice.dto.like;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LikeDto {

    @Positive
    private Long id;

    @NotNull
    @Positive
    private Long userId;

    @Positive
    private Long commentId;

    @Positive
    private Long postId;

    private LocalDateTime createdAt;
}
