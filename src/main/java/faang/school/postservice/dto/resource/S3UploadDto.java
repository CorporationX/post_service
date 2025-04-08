package faang.school.postservice.dto.resource;

public record S3UploadDto(
        Long postId,
        Long fileSize,
        String fileName,
        String fileType,
        byte[] bytes
) {
}
