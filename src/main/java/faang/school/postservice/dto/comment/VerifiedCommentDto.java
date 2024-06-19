package faang.school.postservice.dto.comment;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class VerifiedCommentDto {
    private LocalDateTime verifiedDate;
    private boolean verified;
}
