package faang.school.postservice.service.S3.delete;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteFileS3ServiceImpl implements FileDeletion {

    private final AmazonS3 s3Client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Override
    public void deleteFile(String key) {
        try {
            s3Client.deleteObject(bucketName, key);
        } catch (SdkClientException e) {
            log.error("Delete Service. Delete file: {} to bucket: {}", key, bucketName);
        }
    }
}
