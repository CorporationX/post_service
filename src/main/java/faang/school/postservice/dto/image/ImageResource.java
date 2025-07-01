package faang.school.postservice.dto.image;

public record ImageResource(String fileName, String fileKey, String previewKey, String contentType, long size) {
}
