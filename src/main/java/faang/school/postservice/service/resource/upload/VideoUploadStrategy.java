package faang.school.postservice.service.resource.upload;

import faang.school.postservice.config.resource.ResourceProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3AsyncClient;

@Component
public class VideoUploadStrategy extends AbstractAsyncUploadStrategy implements UploadStrategy {

    public VideoUploadStrategy(S3AsyncClient s3AsyncClient, ResourceProperties resourceProperties) {
        super(s3AsyncClient, resourceProperties.getVideo());
    }

    @Override
    protected byte[] processFile(MultipartFile file) throws Exception {
        return file.getBytes();
    }

    @Override
    protected String resolveContentType(MultipartFile file) {
        return file.getContentType();
    }

    @Override
    public String getType() {
        return "VIDEO";
    }
}
