package faang.school.postservice.service.s3;

import faang.school.postservice.model.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface S3Service {
    Resource uploadFiles(MultipartFile multipartFile, byte[] bytes);

    Resource deleteResource(Long deletedFile);
}
