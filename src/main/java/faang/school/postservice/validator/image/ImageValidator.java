package faang.school.postservice.validator.image;

import faang.school.postservice.exceptions.DataValidationException;
import faang.school.postservice.model.Post;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class ImageValidator {
    @Value("${services.s3.imageParameters.maxFileSize}")
    private long maxFileSize;
    @Value("${services.s3.imageParameters.maxImagePerPost}")
    private int maxImagePerPost;
    @Value("${services.s3.acceptedFormats}")
    private String[] acceptedFormats;

    public void validateFileSize(MultipartFile file){
        if (file.getSize() > maxFileSize) {
            String errorMessage = "File = " + file.getOriginalFilename() + " size is more than" + maxFileSize + " bytes";
            log.error(errorMessage + " ImageValidator-validateFileSize");
            throw new DataValidationException(errorMessage);
        }
    }

    public void validateFilesSize(List<MultipartFile> files) {
        files.forEach(file -> validateFileSize(file));
    }

    public void validateImageFormat(String format) {
        if (!Arrays.asList(acceptedFormats).contains(format)) {
            throw new DataValidationException("Wrong format. Accepted only: " + acceptedFormats.toString());
        }
    }
    public void validateFileCurrentPostImages(Post post) {
        if(post.getResources().size() > maxImagePerPost) {
            String errorMessage = "Number of files is more than " + maxImagePerPost;
            log.error(errorMessage + " ImageValidator-validateFileSize");
            throw new DataValidationException(errorMessage);
        }
    }
}
