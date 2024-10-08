package faang.school.postservice.dto.post.corrector;

import java.util.List;

public record Error(
        String id,
        int offset,
        int length,
        String bad,
        List<String> better,
        String type) {
}
