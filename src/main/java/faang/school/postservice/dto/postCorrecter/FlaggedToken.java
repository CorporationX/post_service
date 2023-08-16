package faang.school.postservice.dto.postCorrecter;

import lombok.Data;

import java.util.List;

@Data
public class FlaggedToken {
    public Integer offset;
    public String token;
    public String type;
    public List<Suggestion> suggestions;
}
