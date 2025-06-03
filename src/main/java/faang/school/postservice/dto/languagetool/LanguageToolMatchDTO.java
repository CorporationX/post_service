package faang.school.postservice.dto.languagetool;

import java.util.List;

public record LanguageToolMatchDTO(
        int offset,
        int length,
        List<LanguageToolReplacementDTO> replacements
) {
}
