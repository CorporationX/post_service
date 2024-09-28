package faang.school.postservice.service.s3;

import faang.school.postservice.model.Post;
import faang.school.postservice.model.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface S3Service {

    List<Resource> addFilesToStorage(List<MultipartFile> files, Post post);

    Resource updateFileInStorage(String key, MultipartFile newFile, Post post);

    void removeFileInStorage(String key);
}
