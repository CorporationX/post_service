package faang.school.postservice.service.image;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class S3Service {
    private final S3Client s3Client;

    @Value("${services.s3.bucketName}")
    private String bucketName;

    // Загрузка файла (у вас уже есть)
    public void uploadFile(String objectKey, byte[] fileBytes, String contentType) {
        s3Client.putObject(request ->
                        request
                                .bucket(bucketName)
                                .key(objectKey)
                                .contentType(contentType),
                RequestBody.fromBytes(fileBytes)
        );
    }

    // Скачивание файла
    public byte[] downloadFile(String objectKey) {
        return s3Client.getObject(request ->
                        request
                                .bucket(bucketName)
                                .key(objectKey),
                ResponseTransformer.toBytes());
    }

    // Удаление файла
    public void deleteFile(String objectKey) {
        s3Client.deleteObject(request -> // Вызов метода удаления
                request //  Lambda создает DeleteObjectRequest
                        .bucket(bucketName) // Указываем бакет ("corpbucket")
                        .key(objectKey));  // Указываем ключ файла для удаления
    }

    // Массовое удаление (для удаления всех картинок поста)
    public void deleteFiles(List<String> objectKeys) {
        if (objectKeys == null || objectKeys.isEmpty()) {
            return; // Нечего удалять
        }

        List<ObjectIdentifier> objectsToDelete = objectKeys.stream()
                .map(key -> ObjectIdentifier.builder().key(key).build())
                .toList();

        s3Client.deleteObjects(request ->
                request.bucket(bucketName)
                        .delete(deleteRequest ->
                                deleteRequest
                                        .objects(objectsToDelete)));
    }
}
