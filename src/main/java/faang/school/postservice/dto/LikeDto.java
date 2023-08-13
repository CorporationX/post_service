package faang.school.postservice.dto;


import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LikeDto {
    private long id;
    private Long userId;
    private Long commentId;
    private Long postId;
}
