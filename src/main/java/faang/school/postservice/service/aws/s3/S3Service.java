package faang.school.postservice.service.aws.s3;

import faang.school.postservice.model.Resource;
import faang.school.postservice.utils.ImageRestrictionRule;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface S3Service {
    Resource uploadFile(MultipartFile file, String folder, ImageRestrictionRule rule) throws IOException;

    void deleteFiles(List<String> keys);

    InputStream downloadFile(String key);
}
