package faang.school.postservice.dto.languageTool;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LanguageToolResponseDto {
    private List<GrammarMatch> matches;
}
