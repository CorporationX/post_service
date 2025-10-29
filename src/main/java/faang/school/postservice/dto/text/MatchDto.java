package faang.school.postservice.dto.text;

import java.util.List;
// for PR
public record MatchDto(int offset, int length, List<ReplacementDto> replacements) {
}
