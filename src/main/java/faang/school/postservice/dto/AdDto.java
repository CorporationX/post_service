package faang.school.postservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdDto {
    private Long id;
    private Long postId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
