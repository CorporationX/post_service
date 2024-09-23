package faang.school.postservice.dto.like;

import lombok.Data;

@Data
public class LikeKafkaDto {
    private Long id;
    private Long postId;
    private Long authorLikeId;
}
