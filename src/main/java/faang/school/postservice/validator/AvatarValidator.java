package faang.school.postservice.validator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class AvatarValidator {

    @Value("${spring.resources.file.max-file-size}")
    private long maxFileSize;

    public void validateFileSize(MultipartFile file) {
        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException("File size exceeds 5 MB");
        }
    }
}
