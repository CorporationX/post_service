package faang.school.postservice.dto;


import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class LikeDto {
    private Long id;
    @Positive
    private Long postId;
    @Positive
    private Long commentId;
}
