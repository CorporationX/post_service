package faang.school.postservice.dto.ai;

import java.util.List;

public record CorrectionDto(
        int pos,
        int len,
        List<String> suggestions
) {}
