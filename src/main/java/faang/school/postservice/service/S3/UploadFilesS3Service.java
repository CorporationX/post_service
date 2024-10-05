package faang.school.postservice.service.S3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import faang.school.postservice.model.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
@Service
@RequiredArgsConstructor
public class UploadFilesS3Service {

    private final AmazonS3 s3Client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    @Autowired
    private ExecutorService executorService;

    /**
     * Загрузка файлов в облачное хранилище S3
     *
     * @param files Список файлов
     * @return Список ссылок на загруженные файлы
     * @throws AmazonS3Exception Если возникает проблема при обращении к хранилищу S3
     * @throws RuntimeException  Если во время выполнения одного из потоков произойдёт ошибка
     */
    public List<Resource> uploadFiles(List<MultipartFile> files) {

        try {
            executorService = Executors.newFixedThreadPool(10);
            List<Future<String>> futures = new ArrayList<>();

            for (MultipartFile file : files) {
                Future<String> future = executorService.submit(() -> {
                    String fileName = generateUniqueName();
                    var metadata = new ObjectMetadata();
                    metadata.setContentLength(file.getSize());
                    metadata.setContentType(file.getContentType());

                    s3Client.putObject(bucketName, fileName, file.getInputStream(), metadata);
                    log.info("Upload Service. Added file: {} to bucket: {}", fileName, bucketName);

                    return s3Client.getUrl(bucketName, fileName).toExternalForm();
                });

                futures.add(future);
            }

            List<String> urls = new ArrayList<>();
            for (Future<String> future : futures) {
                try {
                    String url = future.get();
                    urls.add(url);
                } catch (InterruptedException | ExecutionException e) {
                    log.error("One of the thread ended with exception. Reason: ", e);
                    throw new RuntimeException(e.getMessage());
                }
            }

            executorService.shutdown();
        } catch (AmazonS3Exception e) {
            log.error("Error uploading images to Object Storage. Reason:", e);
            throw new RuntimeException(e.getMessage());
        }

        return mapToResources(files);
    }

    private List<Resource> mapToResources(List<MultipartFile> files) {
        List<Resource> resources = new ArrayList<>();
        for (MultipartFile file : files) {
            var resource = new Resource();
            resource.setSize(BigInteger.valueOf(file.getSize()));
            resource.setType(file.getContentType());
            resource.setName(file.getName());
            resources.add(resource);
        }

        return resources;
    }

    private String generateUniqueName() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}
