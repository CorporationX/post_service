package faang.school.postservice.dto.post;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SpellCheckerDto {
    private int pos;
    private int len;
    private List<String> s;
}
