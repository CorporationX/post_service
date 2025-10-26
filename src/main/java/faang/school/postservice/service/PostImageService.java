package faang.school.postservice.service;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MultipartFile;

public interface PostImageService {

    void createImage(MultipartFile multipartFile, Long postId);

    void uploadImage();

    void getImage();

    void deleteImage();
}