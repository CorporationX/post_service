package faang.school.postservice.dto.postCorrecter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SpellCheckDto {
    String _type;
    List<FlaggedTokenDto> flaggedTokens;
    String correctionType;
}
