package faang.school.postservice.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.postservice.model.Resource;
import liquibase.ObjectMetaData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 s3Client;

    @Value("${s3.bucketName")
    private String bucketName;

    //метод загрузки массива или нет, файлов
    @Transactional
    public Resource[] uploadFiles(MultipartFile[] files, String folder) {
        log.info("preparing {} files to saving", files.length);
        Resource[] resources = new Resource[files.length];

        for (MultipartFile file : files) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());


            PutObjectRequest putObjectRequest = new PutObjectRequest()
        }
    }
    //метод замены массива файлов
}
