package faang.school.postservice.dto;

public record CreatePostDto(
        Long id,
        String content,
        Long authorId,
        Long projectId
        ) {
}
