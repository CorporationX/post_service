package faang.school.postservice.dto.post;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostCorrectionDto implements Serializable {

    @JsonProperty("pos")
    int erroneousWordStartIndex;

    @JsonProperty("len")
    int erroneousWordLength;

    @JsonProperty("s")
    List<String> hints;
}