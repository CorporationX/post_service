package faang.school.postservice.dto.corrector;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CorrectorResponse {
    private String correction;
    private double latency;
    private String status;
}
