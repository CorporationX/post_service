package faang.school.postservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import faang.school.postservice.model.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class S3ServiceImp implements S3Service {
    private final AmazonS3 s3Client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Override
    public List<Resource> uploadFile(MultipartFile[] files) {
        List<Resource> resources = new ArrayList<>();

        for (MultipartFile file : files) {
            String fileKey = String.format("%s/%s", System.currentTimeMillis(), file.getOriginalFilename());
            long fileSize = file.getSize();

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(fileSize);
            metadata.setContentType(file.getContentType());
            try {
                s3Client.putObject(bucketName, fileKey, file.getInputStream(), metadata);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Resource resource = new Resource();
            resource.setKey(fileKey);
            resource.setSize(fileSize);
            resource.setName(file.getOriginalFilename());

            resources.add(resource);
        }
        return resources;
    }
}
