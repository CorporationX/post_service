package faang.school.postservice.dto.image;

public record ImageStorage(String fileKey, String previewKey, String contentType, long size) {
}
