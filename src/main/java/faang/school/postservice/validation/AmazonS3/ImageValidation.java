package faang.school.postservice.validation.AmazonS3;

import faang.school.postservice.exception.DataValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Component
public class ImageValidation {

    public void checkIsImage(MultipartFile file) {
        if (!isImage(file)) {
            log.error("file is not an image");
            throw new DataValidationException("file is not an image");
        }
    }

    private boolean isImage(MultipartFile file) {
        if (file == null || file.getContentType() == null) {
            return false;
        }
        return file.getContentType().startsWith("image/");
    }

}
