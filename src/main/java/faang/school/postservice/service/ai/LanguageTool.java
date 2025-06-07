package faang.school.postservice.service.ai;

import faang.school.postservice.dto.languagetool.LanguageToolMatchDto;
import faang.school.postservice.dto.languagetool.LanguageToolResponseDto;

import java.util.Comparator;
import java.util.List;

public class LanguageTool {

    static public String applyCorrectText(String original, LanguageToolResponseDto response) {
        StringBuilder result = new StringBuilder(original);
        List<LanguageToolMatchDto> matches = response.matches();
        matches.sort(Comparator.comparingInt(LanguageToolMatchDto::offset).reversed());

        for (LanguageToolMatchDto match : matches) {
            if (!match.replacements().isEmpty()) {
                String replacement = match.replacements().get(0).value();
                result.replace(match.offset(), match.offset() + match.length(), replacement);
            }
        }
        return result.toString();
    }
}
