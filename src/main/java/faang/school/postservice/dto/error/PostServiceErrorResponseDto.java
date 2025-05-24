package faang.school.postservice.dto.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostServiceErrorResponseDto {
    private String errorMsg;
    private LocalDateTime timestamp;
    private int codeResponse;
}
