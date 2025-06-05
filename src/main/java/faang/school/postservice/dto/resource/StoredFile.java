package faang.school.postservice.dto.resource;

public record StoredFile(
        String key,
        String name,
        long size,
        String type) {
}
