package faang.school.postservice.repository;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;

import java.io.InputStream;
import java.time.Instant;
import java.util.Date;

@RequiredArgsConstructor
public abstract class S3ObjectRepository {
    protected final AmazonS3 client;
    protected final String bucketName;

    protected void putObject(String key, InputStream inputStream, long size, String contentType) {

        createBucketIfNotExist(bucketName);

        ObjectMetadata meta = new ObjectMetadata();

        meta.setContentLength(size);
        meta.setContentType(contentType);
        meta.setLastModified(Date.from(Instant.now()));

        PutObjectRequest putObjectRequest = new PutObjectRequest(
                bucketName,
                key,
                inputStream,
                meta
        );

        client.putObject(putObjectRequest);
    }

    protected void createBucketIfNotExist(String bucket) {
        if (!client.doesBucketExistV2(bucket)) {
            client.createBucket(bucket);
        }
    }

    protected void deleteObject(String key) {
        DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucketName, key);
        client.deleteObject(deleteObjectRequest);
    }

    protected InputStream getObject(String key) {
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);
        S3Object s3Object = client.getObject(getObjectRequest);
        return s3Object.getObjectContent();
    }
}
