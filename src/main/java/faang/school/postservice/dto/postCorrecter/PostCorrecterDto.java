package faang.school.postservice.dto.postCorrecter;

import lombok.Data;

import java.util.List;

@Data
public class PostCorrecterDto {
    public String _type;
    public List<FlaggedToken> flaggedTokens;
    public String correctionType;
}
