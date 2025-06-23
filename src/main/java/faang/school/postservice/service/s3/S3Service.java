package faang.school.postservice.service.s3;

import faang.school.postservice.dto.s3.S3Dto;
import faang.school.postservice.validation.resource.InMemoryMultipartFile;
import faang.school.postservice.validation.resource.ValidationResource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@Service
@Slf4j
public class S3Service {
    private final S3Client s3Client;
    private final ValidationResource validationResource;
    @Value("${services.s3.bucket}")
    private String bucketName;

    public String generateKeyForImage(MultipartFile image) {
        String key = String.format("%s - %s", System.currentTimeMillis(), image.getOriginalFilename());

        try (InputStream inputStream = image.getInputStream()) {
            MultipartFile multipartFile = resizeImageIfNeeded(image);
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .metadata(Map.of("filename", Objects.requireNonNull(multipartFile.getOriginalFilename())))
                    .contentLength(multipartFile.getSize())
                    .contentType(multipartFile.getContentType())
                    .build();
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, multipartFile.getSize()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return key;
    }

    public void deleteImage(String key) {
        s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build());
    }

    public S3Dto downloadImage(String key) {
        ResponseInputStream<GetObjectResponse> responseObject = s3Client.getObject(
                GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build());
        Resource streamResource = new InputStreamResource(responseObject);
        HeadObjectResponse metadata = s3Client.headObject(HeadObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build());

        return S3Dto.builder()
                .name(metadata.metadata().get("filename"))
                .contentType(metadata.contentType())
                .contentLength(metadata.contentLength())
                .resource(streamResource)
                .build();
    }

    private MultipartFile resizeImageIfNeeded(MultipartFile file) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(file.getInputStream());

        validationResource.checkPictureWeight(file);
        BufferedImage resultImage = Scalr.resize(bufferedImage, Scalr.Method.AUTOMATIC, Scalr.Mode.FIT_EXACT,
                bufferedImage.getWidth(), bufferedImage.getHeight());

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ImageIO.write(resultImage, "jpg", byteArrayOutputStream);
            byte[] byteImage = byteArrayOutputStream.toByteArray();

            return new InMemoryMultipartFile(
                    "file",
                    file.getOriginalFilename(),
                    "image/jpg",
                    byteImage
            );
        }
    }
}