package faang.school.postservice.dto.languagetool;

import java.util.List;

public record LanguageToolMatchDto(
        int offset,
        int length,
        List<LanguageToolReplacementDto> replacements
) {
}
