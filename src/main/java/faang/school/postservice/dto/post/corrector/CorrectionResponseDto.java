package faang.school.postservice.dto.post.corrector;

import lombok.Builder;

@Builder
public record CorrectionResponseDto(
        boolean status,
        CorrectionResponse response
) {
}
