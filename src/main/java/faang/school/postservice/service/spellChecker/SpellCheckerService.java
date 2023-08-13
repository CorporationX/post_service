package faang.school.postservice.service.spellChecker;

import faang.school.postservice.dto.spellChecker.SpellErrorDto;

import java.util.List;

public interface SpellCheckerService {
    List<SpellErrorDto> spellingTextCorrection(Long text);
}
