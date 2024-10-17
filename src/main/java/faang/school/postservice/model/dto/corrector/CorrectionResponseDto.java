package faang.school.postservice.model.dto.corrector;

import lombok.Builder;

@Builder
public record CorrectionResponseDto(
        boolean status,
        CorrectionResponse response
) {
}
