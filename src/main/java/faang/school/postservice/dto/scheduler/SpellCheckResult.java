package faang.school.postservice.dto.scheduler;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SpellCheckResult {

    private int code;
    private int pos;
    private int row;
    private int col;
    private int len;
    private String word;
    private List<String> s;

}
