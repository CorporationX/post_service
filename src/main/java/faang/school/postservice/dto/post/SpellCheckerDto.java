package faang.school.postservice.dto.post;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SpellCheckerDto {
    private int pos;
    private int len;
    @JsonProperty("s")
    private List<String> spellErrors;
}
