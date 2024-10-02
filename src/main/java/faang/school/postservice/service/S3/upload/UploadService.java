package faang.school.postservice.service.S3.upload;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UploadService {

    /**
     * @see UploadImagesS3ServiceImpl #uploadImages
     */
    List<String> uploadImages(List<MultipartFile> images);
}
