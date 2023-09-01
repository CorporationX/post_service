package faang.school.postservice.service.s3;

import faang.school.postservice.model.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface S3Service {
    List<Resource> uploadFile(MultipartFile[] multipartFile);
}
