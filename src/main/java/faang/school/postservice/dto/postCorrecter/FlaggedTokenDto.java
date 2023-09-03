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
public class FlaggedTokenDto {
    Long offset;
    String token;
    String type;
    List<SuggestionDto> suggestions;
}
