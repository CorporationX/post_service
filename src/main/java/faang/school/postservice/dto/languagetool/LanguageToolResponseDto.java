package faang.school.postservice.dto.languagetool;

import java.util.List;

public record LanguageToolResponseDto(List<LanguageToolMatchDto> matches) {
}
