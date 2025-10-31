package faang.school.postservice.dto.text;

import java.util.List;

public record TextCheckResponseDto(List<MatchDto> matches) {
}
