package faang.school.postservice.service;


import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import faang.school.postservice.config.s3.MinioConfig;
import faang.school.postservice.exceptions.DataValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class PostImageServiceImpl implements PostImageService {
    private final MinioConfig minioConfig;
    @Value("${MINIO_BUCKET_NAME}")
    private String bucketName;
    @Value("${services.s3.posts.image.size.max}")
    private int MAX_SIZE_IMAGE;

    @Override
    public void createImage(MultipartFile file, Long postId) {

        if (file.getSize() == MAX_SIZE_IMAGE) {
            throw new DataValidationException("Maximum image size exceeded! Maximum image size 5 MB");
        }
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());
        String key = String.format("%s%s",file.getOriginalFilename(), LocalDateTime.now());
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file.getInputStream(), objectMetadata)
        }catch (){

        }

    }

    @Override
    public void uploadImage() {

    }

    @Override
    public void getImage() {

    }

    @Override
    public void deleteImage() {

    }


}