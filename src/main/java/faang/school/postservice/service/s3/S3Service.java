package faang.school.postservice.service.s3;

import faang.school.postservice.dto.resource.S3UploadDto;
import faang.school.postservice.model.Resource;

import java.io.InputStream;

public interface S3Service {

    Resource uploadResource(S3UploadDto s3UploadDto);

    InputStream downloadResource(String key);

    void deleteResource(String key);
}
