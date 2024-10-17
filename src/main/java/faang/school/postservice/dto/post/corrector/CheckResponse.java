package faang.school.postservice.dto.post.corrector;

import java.util.List;

public record CheckResponse(
        List<Error> errors) implements Response {
}
