package faang.school.postservice.dto.post.corrector;

public record LanguageResponse(
        String language,
        String dialect) implements Response {
}
