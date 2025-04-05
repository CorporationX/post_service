package faang.school.postservice.service.image;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.postservice.config.aws.AwsProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;

@Slf4j
@Service
@AllArgsConstructor
public class S3ServiceImpl implements S3Service {
    private final AwsProperties awsProperties;
    private final AmazonS3 s3Client;

    @Override
    public void uploadFile(long fileSize, String contentType, String key, byte[] byteArray) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(fileSize);
        objectMetadata.setContentType(contentType);

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArray)) {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    awsProperties.getBucketName(), key, inputStream, objectMetadata);
            s3Client.putObject(putObjectRequest);
        } catch (Exception e) {
            throw new RuntimeException("Error uploading file: ", e);
        }
    }
}
