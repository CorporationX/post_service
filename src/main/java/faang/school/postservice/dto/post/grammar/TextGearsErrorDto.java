package faang.school.postservice.dto.post.grammar;

import java.util.List;
import java.util.Map;

public record TextGearsErrorDto(
        String id,
        int offset,
        int length,
        String bad,
        List<String> better,
        String type,
        Map<String, String> description
) {
}
