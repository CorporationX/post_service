package faang.school.postservice.config.moderation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class ModerationWordsHolder {
    private List<String> offensiveWords;
}
