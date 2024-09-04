package faang.school.postservice.dto.spell;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SpellCheckerResponseDto {

    @JsonProperty("status")
    private boolean status;

    @JsonProperty("response")
    private CorrectedResponse response;
}
