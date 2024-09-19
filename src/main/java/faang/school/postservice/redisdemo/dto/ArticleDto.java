package faang.school.postservice.redisdemo.dto;

public record ArticleDto(
        Long id,
        String title,
        String text,
        Double rating
) {
}
