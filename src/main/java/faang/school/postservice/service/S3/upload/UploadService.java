package faang.school.postservice.service.S3.upload;

import faang.school.postservice.model.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@FunctionalInterface
public interface UploadService {

    /**
     * @see UploadFilesS3ServiceImpl #uploadImages
     */
    List<Resource> uploadFiles(List<MultipartFile> files);
}
