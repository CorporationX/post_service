package faang.school.postservice.dto.corrector;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CorrectWordDto {
    private int code;
    private int pos;
    private int row;
    private int col;
    private int len;
    private String word;
    @JsonProperty("s")
    private List<String> suggestions;
}
