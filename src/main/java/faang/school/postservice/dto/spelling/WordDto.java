package faang.school.postservice.dto.spelling;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WordDto {
    private String word;
    private List<String> s;
}