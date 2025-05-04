package faang.school.postservice.dto.languageTool;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GrammarMatch {
    private List<ReplacementValueDto> replacements;
    private int offset;
    private int length;
}
