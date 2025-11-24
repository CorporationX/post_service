package faang.school.postservice.dto.post.grammar;

import java.util.List;

public record TextGearsResponseBodyDto(
        boolean result,
        List<TextGearsErrorDto> errors
) {
}
