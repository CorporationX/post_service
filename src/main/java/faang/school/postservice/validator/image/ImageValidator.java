package faang.school.postservice.validator.image;

import faang.school.postservice.exceptions.DataValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
@Slf4j
public class ImageValidator {
    @Value("${services.s3.imageParameters.maxFileSize}")
    private long maxFileSize;
    @Value("${services.s3.imageParameters.maxImagePerPost}")
    private int maxImagePerPost;
    public void validateFileSize(MultipartFile file){
        if (file.getSize() > maxFileSize) {
            String errorMessage = "File size is more than" + maxFileSize + " bytes";
            log.error(errorMessage + " ImageValidator-validateFileSize");
            throw new DataValidationException(errorMessage);
        }
    }

    public void validateFileSize(List<MultipartFile> files) {
        if(files.size() > maxImagePerPost) {
            String errorMessage = "Number of files is more than" + maxImagePerPost;
            log.error(errorMessage + " ImageValidator-validateFileSize");
            throw new DataValidationException(errorMessage);
        }
    }
}
