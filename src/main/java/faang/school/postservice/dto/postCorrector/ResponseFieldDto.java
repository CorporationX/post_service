package faang.school.postservice.dto.postCorrector;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResponseFieldDto {
    private String corrected;

    @JsonCreator
    public ResponseFieldDto(@JsonProperty("corrected") String corrected) {
        this.corrected = corrected;
    }
}
