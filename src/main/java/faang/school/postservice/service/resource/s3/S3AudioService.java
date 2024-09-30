package faang.school.postservice.service.resource.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import faang.school.postservice.exception.FileException;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import faang.school.postservice.model.ResourceType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

import static java.lang.String.format;

@Component
@RequiredArgsConstructor
public class S3AudioService {
    private final AmazonS3 s3client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    public Resource addFileToStorage(MultipartFile file, Post post) {
        String type = file.getContentType();
        String name = file.getOriginalFilename();
        long fileSize = file.getSize();

        try (InputStream fileStream = file.getInputStream()) {;

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(type);
            metadata.setContentLength(fileSize);

            String customKey = format("post_%d/audio/%d_%s", post.getId(), System.currentTimeMillis(), name);

            s3client.putObject(bucketName, customKey, fileStream, metadata);

            return Resource.builder()
                    .key(customKey)
                    .size(fileSize)
                    .name(name)
                    .type(ResourceType.AUDIO)
                    .post(post)
                    .build();

        } catch (IOException e) {
            throw new FileException("Error with audio file");
        }
    }

    public Resource updateFileInStorage(String key, MultipartFile newFile, Post post) {
        s3client.deleteObject(bucketName, key);
        return addFileToStorage(newFile, post);
    }

    public void removeFileInStorage(String key) {
        s3client.deleteObject(bucketName, key);
    }
}
