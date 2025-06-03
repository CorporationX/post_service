package faang.school.postservice.dto.languagetool;

import java.util.List;

public record LanguageToolResponseDTO(List<LanguageToolMatchDTO> matches) {
}
