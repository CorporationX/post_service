package faang.school.postservice.service.s3;

import faang.school.postservice.dto.resource.ResourceObjectResponse;

public interface S3Service {
    void uploadFile(byte[] fileContent, String contentType, String fileKey);

    void deleteFile(String fileKey);

    ResourceObjectResponse downloadFile(String fileKey);
}
