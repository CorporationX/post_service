package faang.school.postservice.dto.corrector;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Content {
    private String text;
    private String lang = "en";

    @Override
    public String toString() {
        return String.format("""
                {
                  "text": "%s",
                  "lang": "%s"
                }
                """, text, lang);
    }
}
