package faang.school.postservice.dto.post.grammar;

import java.util.List;
import java.util.Map;

public record TextGearsErrorDto(
        String id,
        int offset,
        int length,
        String badVersion,
        List<String> betterVersion,
        String type,
        Map<String, String> description
) {
}
