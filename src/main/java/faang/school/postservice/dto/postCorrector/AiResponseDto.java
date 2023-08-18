package faang.school.postservice.dto.postCorrector;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiResponseDto {
    private boolean status;
    private ResponseFieldDto response;

    @JsonCreator
    public AiResponseDto(@JsonProperty("status") boolean status, @JsonProperty("response") ResponseFieldDto response) {
        this.status = status;
        this.response = response;
    }
}
