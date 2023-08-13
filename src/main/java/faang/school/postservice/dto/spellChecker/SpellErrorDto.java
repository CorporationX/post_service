package faang.school.postservice.dto.spellChecker;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpellErrorDto {
    private String word;
    private List<String> s;
}
