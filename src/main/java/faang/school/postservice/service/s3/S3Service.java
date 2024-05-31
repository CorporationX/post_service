package faang.school.postservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.postservice.model.Resource;
import faang.school.postservice.service.ResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    @Autowired
    private final AmazonS3 s3Client;
    private final ResourceService resourceService;

    @Value("${s3.bucketName")
    private String bucketName;

    @Transactional
    public List<Resource> uploadFiles(List<MultipartFile> files, String folder) {
        log.info("preparing {} files to saving", files.size());
        List<Resource> resources = new ArrayList<>();

        for (MultipartFile file : files) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            String key = String.format("%s/%d%s", folder, System.currentTimeMillis(), file.getOriginalFilename());

            try {
                log.info("Upload file {} to S3", file.getOriginalFilename());
                PutObjectRequest putObjectRequest = new PutObjectRequest(
                        bucketName, key, file.getInputStream(), metadata);
                s3Client.putObject(putObjectRequest);
            } catch (Exception e) {
                log.error("Upload file {} error", file.getOriginalFilename() + ". " + e.getMessage());
                throw new RuntimeException("Upload file error");
            }
            log.info("File {} upload completed", file.getOriginalFilename());
            resources.add(resourceService.createResource(key, file));
        }
        return resources;
    }
    //метод замены массива файлов
}
