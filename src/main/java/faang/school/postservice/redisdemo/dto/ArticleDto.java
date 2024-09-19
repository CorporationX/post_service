package faang.school.postservice.redisdemo.dto;

import java.util.List;

public record ArticleDto(
        Long id,
        String title,
        String text,
        Double rating,
        List<String> hashTags
) {
}
