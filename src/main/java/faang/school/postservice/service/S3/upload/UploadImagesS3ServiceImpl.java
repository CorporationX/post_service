package faang.school.postservice.service.S3.upload;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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
public class UploadImagesS3ServiceImpl implements UploadService {

    private final AmazonS3 s3Client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    /**
     * Загрузка фотографий в облачное хранилище S3
     *
     * @param images Список картинок в байтах
     * @return Список ссылок на загруженные файлы
     * @throws AmazonS3Exception Если возникает проблема при обращении к хранилищу S3
     * @throws RuntimeException  Если во время выполнения одного из потоков произойдёт ошибка
     */

    @Override
    public List<String> uploadImages(List<MultipartFile> images) {

        List<byte[]> bytes = images.stream().map(image -> {
            try {
                return image.getBytes();
            } catch (IOException e) {
                throw new RuntimeException(e.getCause());
            }
        }).toList();

        try {
            ExecutorService executorService = Executors.newFixedThreadPool(images.size());
            List<Future<String>> futures = new ArrayList<>();

            for (byte[] imageBytes : bytes) {
                Future<String> future = executorService.submit(() -> {
                    String fileName = generateUniqueName();
                    var metadata = new ObjectMetadata();
                    metadata.setContentLength(imageBytes.length);
                    metadata.setContentType(String.valueOf(ContentType.IMAGE_PNG));

                    ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
                    s3Client.putObject(bucketName, fileName, inputStream, metadata);
                    log.info("Upload Service. Added file: " + fileName + " to bucket: " + bucketName);

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
        }
        return List.of();
    }

    private String generateUniqueName() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}
