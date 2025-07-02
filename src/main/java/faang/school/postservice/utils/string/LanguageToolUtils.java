package faang.school.postservice.utils.string;

import faang.school.postservice.dto.post.LanguageToolClientResponseDto;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Comparator;
import java.util.List;

@UtilityClass
@Slf4j
public class LanguageToolUtils {

    public String applyCorrections(String originalText, List<LanguageToolClientResponseDto.Match> matches) {
        matches.sort(Comparator.comparingInt(LanguageToolClientResponseDto.Match::getOffset).reversed());

        StringBuilder sb = new StringBuilder(originalText);
        for (LanguageToolClientResponseDto.Match match : matches) {
            if (match.getReplacements().isEmpty()) {
                continue;
            }

            String replacement = match.getReplacements().get(0).getValue();
            int offset = match.getOffset();
            int length = match.getLength();

            String originalFragment =
                    originalText.substring(offset, Math.min(offset + length, originalText.length()));
            log.debug("Correction: replacing '{}' with '{}' at offset {} (length {})",
                    originalFragment, replacement, offset, length);
            sb.replace(offset, offset + length, replacement);
        }

        return sb.toString();
    }
}
